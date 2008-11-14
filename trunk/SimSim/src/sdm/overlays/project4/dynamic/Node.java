package sdm.overlays.project4.dynamic;

import java.awt.*;
import java.util.*;


import sdm.overlays.project4.dynamic.msgs.*;
import sdm.overlays.words.*;
import simsim.core.*;
import simsim.utils.*;
import simsim.gui.geom.*;

public class Node extends AbstractNode implements ExtendedMessageHandler, Displayable {

	final static int PARTITION_LEVELS = 4;

	protected int test = 0;
	public long key;
	public double chordKey;
	public RandomList<Word> words;
	public Map<Double,HashSet<EndPoint>> wordDictionary;
	public Map< Pair<String,String>, Map<EndPoint,Pair<Word,Word>> > queryBuffers;
	public Map< Pair<String,String>, Integer> queryTimers;
	public int sentMessages;
	public int answeredQueries;



	public XY pos;
	public Line shape;

	ChordRoutingTable rtable;

	public Node() {
		super();
		key = NodeDB.store(this);
		chordKey = (double) key / (1L << NodeDB.MAX_KEY_LENGTH);
		rtable = new ChordRoutingTable(this);
		wordDictionary = new HashMap<Double,HashSet<EndPoint>>(100);
		queryBuffers = new HashMap< Pair<String,String>, Map<EndPoint,Pair<Word,Word>> >(10);
		queryTimers = new HashMap< Pair<String,String>, Integer>(10);

		final double R = 450.0;
		double a = chordKey * 2 * Math.PI - Math.PI / 2;
		pos = new XY(500 + R * Math.cos(a), 500 + R * Math.sin(a));
		shape = new Line(pos.x, pos.y, pos.x + 1, pos.y);
	}


	// Populate the node's routing table.
	public void init() {
		rtable.populate(NodeDB.nodes());
		words = new RandomList<Word>(WordsDB.randomWords(10));
		//initWordDHT();
		super.setColor(Color.green);
	}

//	public void routeTo( double dst) {
//	onReceive(endpoint, new ChordMessage(dst));
//	}

	public void join(EndPoint firstBlood) {
			rtable.setPredecessor(null);
			udpSend(firstBlood,new GiefSuccessor(endpoint,this.chordKey));
		}

	public void display(Graphics2D gu, Graphics2D gs) {
		gs.draw(shape);
	}


	public String toString() {
		return Double.toString(chordKey);
	}

	/*
	 * MESSAGE HANDLERS
	 */

	public void onReceive(EndPoint src, GiefSuccessor m) {
		EndPoint nextHop = rtable.nextHop( m.getKey() );
		if (nextHop != null && nextHop != this.endpoint) {
			this.udpSend(nextHop, new GiefSuccessor(m));
		} else {
			RTableEntry successor = rtable.getSuccessor();
			this.udpSend(m.getSource(),new HereIsYourSuccessor(successor.key, successor.endpoint));
		}
	}
	
	public void onReceive(EndPoint src, HereIsYourSuccessor m) {
		rtable.setSuccessor(m.getSuccessor(),m.getSuccKey());
	}

	/* Implements the Chord Routing Table */
	class ChordRoutingTable implements Displayable {
		int currentFinger = 0;
		final double ownKey;
		EndPoint sucessor,predecessor;
		RTableEntry[] fingers = new RTableEntry[NodeDB.MAX_KEY_LENGTH];

		ChordRoutingTable(Node owner) {
			this.ownKey = owner.chordKey;
		}
		
		RTableEntry getSuccessor() {
			return fingers[fingers.length-1];
		}
		
		EndPoint getPredecessor() {
			return predecessor;
		}
		
		void setSuccessor(EndPoint nsuc,double nsucKey) {
			fingers[fingers.length-1] = new RTableEntry(nsucKey,nsuc);
		}
		
		void setPredecessor(EndPoint npred) {
			predecessor = npred;
		}

		void setFinger(int nFinger, EndPoint finger, double fingerKey) {
			fingers[nFinger] = new RTableEntry(fingerKey,finger);
		}
		
		void populate(Collection<Node> nodes) {

			TreeSet<Node> sn = new TreeSet<Node>( new ChordNodeSorter( exactFingerKey(0)));
			sn.addAll(nodes);

			int j = 0;
			for (Node i : sn) {
				if (distanceBetween( exactFingerKey(j), i.chordKey) > 0.5) {
					continue;
				}
				while (j < fingers.length && distanceBetween(exactFingerKey(j), i.chordKey) <= 0.5) {
					fingers[j++] = new RTableEntry(i.chordKey, i.endpoint);
				}
				if (j >= fingers.length)
					break;
			}

			for (RTableEntry i : fingers)
				assert i != null;

			TreeSet<Node> pn = new TreeSet<Node>( new ChordNodeSorter(ownKey));
			pn.addAll(nodes);

			for (int i = fingers.length; --i >= 0;)
				if (fingers[i].key == ownKey) {
					fingers[i].key = pn.last().chordKey;
					fingers[i].endpoint = pn.last().endpoint;
				}
		}

		EndPoint nextHop(double dst) {
			if (dst != ownKey) {
				double d = distanceBetween(dst, ownKey);
				for (RTableEntry i : fingers)
					if (i != null && distanceBetween(dst, i.key) < d)
						return i.endpoint;
			}
			return null;
		}

		double exactFingerKey(int j) {
			double k = ownKey + Math.pow(2, -(j + 1));
			return k < 1 ? k : k - 1.0;
		}
		
		double distanceBetween(double key, double candidate) {
			double d = key - candidate;
			return d >= 0 ? d : d + 1.0;
		}

		void dump() {
			System.out.println("RTable for :" + ownKey);
			for (RTableEntry i : fingers)
				System.out.printf("%.8f\n", i.key);

		}

		class ChordNodeSorter implements Comparator<Node> {

			final double key;
			ChordNodeSorter(double key) {
				this.key = key;
			}

			public int compare(Node a, Node b) {
				if (a.chordKey == b.chordKey)
					return 0;
				double dA = key - a.chordKey;
				dA = dA >= 0 ? dA : dA + 1.0;
				double dB = key - b.chordKey;
				dB = dB >= 0 ? dB : dB + 1.0;
				return dA < dB ? -1 : 1;
			}
		}

		public void display( Graphics2D gu, Graphics2D gs) {
			gs.setColor( Color.DARK_GRAY ) ;
			gs.setStroke( new BasicStroke(2.0f)) ;
			for( RTableEntry i : fingers ) {
				final XY radius = new XY(10,10) ;
				Node other = (Node) i.endpoint.handler ;
				gs.fill( new Ellipse( other.pos, radius ) ) ;
			}
			double j = 0.75 ;
			for( RTableEntry i : fingers ) {
				j *= 1.5 ;
				Node other = (Node) i.endpoint.handler ;				
				XY m = pos.add(other.pos).mult(0.5) ;
				XY c = new XY( m.x + (500 - m.x) / j, m.y + (500-m.y) / j ) ;
				gs.draw( new QuadCurve( pos.x, pos.y, c.x, c.y, other.pos.x, other.pos.y) ) ;
			}
		}
	}
	
	class RTableEntry {
		double key;
		EndPoint endpoint;

		RTableEntry(double k, EndPoint e) {
			key = k;
			endpoint = e;
		}
	}
	
}
