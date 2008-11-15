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
	int currentFinger = 0;
	boolean colorate = false;

	public XY pos;
	public Circle shape;

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
		shape = new Circle(pos.x, pos.y, 10);
	}


	// Populate the node's routing table.
	public void init() {
		rtable.populate(NodeDB.nodes());
		initNewNode();
	}

	public void initNewNode() {
		words = new RandomList<Word>(WordsDB.randomWords(10));

		new PeriodicTask(0.5) {
			public void run() {
				stabilize();
			}
		};

		new PeriodicTask(0.1) {
			public void run() {
				pingPred();
			}
		};

		new PeriodicTask(0.5) {
			public void run() {
				refreshFingers();
			}
		};

	}

//	public void routeTo( double dst) {
//	onReceive(endpoint, new ChordMessage(dst));
//	}

	public void join(EndPoint firstBlood) {
		colorate = true;
		rtable.setPredecessor(null,-1);
		udpSend(firstBlood,new GiefSuccessor(endpoint,this.chordKey));
		System.out.println("YAY?");
		//initNewNode();
	}

	public void fingerize() {
		for (int i = 0; i < NodeDB.MAX_KEY_LENGTH ; i++ )
			udpSend(rtable.getSuccessor().endpoint, new LookupMessage(endpoint,i,currentFinger));
	}

	public void stabilize() {
			udpSend(rtable.getSuccessor().endpoint,new GiefPredecessor());
	}

	public void display(Graphics2D gu, Graphics2D gs) {
		if (colorate)
			gs.setColor(Color.RED);
		gs.draw(shape);
		gs.fill(shape);
	}

	public void pingPred() {
		udpSend(rtable.getPredecessor().endpoint,new Ping());
	}

	public String toString() {
		return Double.toString(chordKey);
	}

	void refreshFingers() {
		double fingerKey = rtable.exactFingerKey(currentFinger);
		onReceive(endpoint, new LookupMessage(endpoint,fingerKey,currentFinger));
		currentFinger++;
		if (currentFinger >= rtable.fingers.length)
			currentFinger = 0;
	}

	public boolean contains(double a, double b, double x) {
		if (b - a > 0)
			return x > a && x < b;
		else 
			if (b-a > 0)
				return contains(a,1,x) || contains(0,b,x);
		return false;
	}


	/*
	 * MESSAGE HANDLERS
	 */

	public void onReceive(EndPoint src, GiefPredecessor m) {
		udpSend(src,new HereIsMyPredecessor(rtable.getPredecessor().key,rtable.getPredecessor().endpoint));
	}

	public void onReceive(EndPoint src, HereIsMyPredecessor m) {
		if (contains(this.chordKey,rtable.getSuccessor().key,m.getPredKey())) {
			rtable.setSuccessor(m.getPredecessor(), m.getPredKey());
			//System.out.println("Successor updated considering:\n[ "+chordKey+" ; "+m.getPredKey()+" ; "+rtable.getSuccessor().key+" ]");
		}
		udpSend(rtable.getSuccessor().endpoint, new NotifyMessage(this.chordKey,this.endpoint));
	}

	public void onReceive(EndPoint src, Ping m) {
	}

	public void onSendFailure(EndPoint src, Ping m) {
		System.out.println("LULZ");
		rtable.setPredecessor(null, -1);
	}
	
	public void onReceive(EndPoint src, NotifyMessage m) {
		if (rtable.getPredecessor().endpoint == null || contains(rtable.getPredecessor().key,chordKey,m.getPredKey())) {
			rtable.setPredecessor(m.getPredecessor(), m.getPredKey());
			//System.out.println("Predecessor updated considering:\n[ "+rtable.getPredecessor().key+" ; "+m.getPredKey()+" ; "+chordKey+" ]");
		
		}
	}

	public void onReceive(EndPoint src, LookupMessage m) {
		EndPoint nextHop = rtable.nextHop( m.getKey() );
		if (nextHop != null && nextHop != this.endpoint) {
			this.udpSend(nextHop, new LookupMessage(m));
		} else {
			this.udpSend(m.getSource(),new LookupReply(chordKey, endpoint, m.getFingerNumber()));
		}
	}

	public void onReceive(EndPoint src, GiefSuccessor m) {
		EndPoint nextHop = rtable.nextHop( m.getKey() );
		if (nextHop != null && nextHop != this.endpoint) {
			//System.out.println("AYE? "+m.getKey()+" "+chordKey);
			this.udpSend(nextHop, new GiefSuccessor(m));
		} else {
			//System.out.println("AYE! "+m.getKey()+" "+chordKey);
			
			RTableEntry successor = rtable.getSuccessor();
			System.out.println("AYE Result: "+successor.key);
			this.udpSend(m.getSource(),new HereIsYourSuccessor(successor.key, successor.endpoint));
		}
	}

	public void crash() {
		NodeDB.dispose(this) ;
	}

	public void onReceive(EndPoint src, HereIsYourSuccessor m) {
		rtable.setSuccessor(m.getSuccessor(),m.getSuccKey());
		
//		System.out.println("Successor retrieved from: "+src.address.pos);
//		System.out.println("at Node: "+chordKey);
//		System.out.println("with position: "+m.getSuccKey());
//		System.out.println();
		//fingerize();
		initNewNode();
	}

	public void onReceive(EndPoint src, LookupReply m) {
		rtable.setFinger(m.getFingerNumber(),m.getSuccessor(),m.getSuccKey());
		//System.out.println("Finger #"+m.getFingerNumber()+" updated at "+chordKey+" with keyValue "+m.getSuccKey());
	}

	/* Implements the Chord Routing Table */
	class ChordRoutingTable implements Displayable {
		int currentFinger = 0;
		final double ownKey;
		RTableEntry predecessor = new RTableEntry(-1,null);
		RTableEntry[] fingers = new RTableEntry[NodeDB.MAX_KEY_LENGTH];

		ChordRoutingTable(Node owner) {
			this.ownKey = owner.chordKey;
		}

		RTableEntry getSuccessor() {
			return fingers[fingers.length-1];
		}

		RTableEntry getPredecessor() {
			return predecessor;
		}

		void setSuccessor(EndPoint nsuc,double nsucKey) {
			fingers[fingers.length-1] = new RTableEntry(nsucKey,nsuc);
		}

		void setPredecessor(EndPoint npred,double npredKey) {
			predecessor = new RTableEntry(npredKey,npred);
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
				if (i != null && i.endpoint != null) {
					final XY radius = new XY(10,10) ;
					Node other = (Node) i.endpoint.handler ;
					gs.fill( new Ellipse( other.pos, radius ) ) ;
				}
			}
			double j = 0.75 ;
			for( RTableEntry i : fingers ) {
				if (i != null && i.endpoint!=null) {
					j *= 1.5 ;
					Node other = (Node) i.endpoint.handler ;				
					XY m = pos.add(other.pos).mult(0.5) ;
					XY c = new XY( m.x + (500 - m.x) / j, m.y + (500-m.y) / j ) ;
					gs.draw( new QuadCurve( pos.x, pos.y, c.x, c.y, other.pos.x, other.pos.y) ) ;
				}
			}
			if (predecessor.endpoint != null) {
				j *= 1.5 ;
				Node other = (Node) predecessor.endpoint.handler ;				
				XY m = pos.add(other.pos).mult(0.5) ;
				XY c = new XY( m.x + (500 - m.x) / j, m.y + (500-m.y) / j ) ;
				gs.setColor(Color.BLUE);
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
