package sdm.overlays.project4.expressionSearch;

import java.awt.*;
import java.util.*;


import sdm.overlays.project4.expressionSearch.msgs.*;
import sdm.overlays.words.*;
import simsim.core.*;
import simsim.utils.*;
import simsim.gui.geom.*;

public class Node extends AbstractNode implements ExtendedMessageHandler, Displayable {

	public long key;
	public double chordKey;
	public RandomList<Word> words;
	public Map<Double,HashSet<EndPoint>> wordDictionary;
	public int messagesSent;
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
		messagesSent = 0;
		answeredQueries = 0;
		

		final double R = 450.0;
		double a = chordKey * 2 * Math.PI - Math.PI / 2;
		pos = new XY(500 + R * Math.cos(a), 500 + R * Math.sin(a));
		shape = new Line(pos.x, pos.y, pos.x + 1, pos.y);
	}

	//Coloca os pares word/endpoint na DHT
	public void initWordDHT() {
		for (Word w : words) 
			onReceive(endpoint, new PutMessage(w,endpoint));
	}
	
	// Populate the node's routing table.
	public void init() {
		rtable.populate(NodeDB.nodes());
		words = new RandomList<Word>(WordsDB.randomWords(10));
		initWordDHT();
		super.setColor(Color.green);
	}

//	public void routeTo( double dst) {
//		onReceive(endpoint, new ChordMessage(dst));
//	}

	public void query( Word word ) {
		System.out.println("Node "+endpoint.address.pos+" starting query for Word \""+word.value+"\"...");
		onReceive(endpoint, new GetMessage(word,endpoint));
	}
	
	public void circulate(String pattern1,String pattern2) {
		System.out.println("Node "+endpoint.address.pos+" is drawing a circle...");
		udpSend(rtable.fingers[rtable.fingers.length-1].endpoint,new CircularGetMessage(endpoint,pattern1,pattern2) );
		messagesSent++;
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
	
	public Pair<Word,String> matchExpression(String expression) {
		Word matchedWord = null;
		for (Word w : words) {
			if (w.value.matches(expression))
				matchedWord = w;
		}
		if (matchedWord != null)
			return new Pair<Word,String>(matchedWord,expression);
		return null;
	}
	
	public Pair<Pair<Word,String>,Pair<Word,String>> patternizer(String pat1, String pat2) {

		Pair<Word,String> matchingPair1 = matchExpression(pat1);
		Pair<Word,String> matchingPair2 = matchExpression(pat2);
		if (matchingPair1 != null && matchingPair2 != null)
			return new Pair<Pair<Word,String>,Pair<Word,String>>(matchingPair1,matchingPair2);
		return null;
	}
	
	public void onReceive(EndPoint src, PutMessage m) {

		EndPoint nextHop = rtable.nextHop( m.getDst() );
		if (nextHop != null && nextHop != this.endpoint) {
			this.udpSend(nextHop, new PutMessage(m));
			messagesSent++;
		}
		else {
			HashSet<EndPoint> previous = wordDictionary.get(m.getWord().dHashValue());
			if (previous == null) previous = new HashSet<EndPoint>();
			previous.add(m.getOrigin());
			wordDictionary.put(m.getWord().dHashValue(),previous);
			//System.out.println("Saved word: "+m.getWord().value+" at nodeKey: "+this.chordKey+" with wordKey " + m.getWord().dHashValue());
		}
	}
	
	public void onReceive(EndPoint src, GetMessage m) {

		EndPoint nextHop = rtable.nextHop( m.getDst() );
		if (nextHop != null && nextHop != this.endpoint) {
			this.udpSend(nextHop, new GetMessage(m));
		}
		else {
			if (wordDictionary.containsKey(m.getWord().dHashValue()))
				udpSend(m.getSender(),new GetReply(m.getWord(),wordDictionary.get(m.getWord().dHashValue())) );
			else
				udpSend(m.getSender(),new GetReply(m.getWord(),null));
		}
		messagesSent++;
	}
	
	public void onReceive(EndPoint src, GetReply m) {

		if(m.getNodes() != null) {
			System.out.println("Word \""+m.getWord().value+"\" is at Nodes :");
			Iterator<EndPoint> i = m.getNodes().iterator();
			while(i.hasNext())
				System.out.println(i.next().address.pos);
		}
		else
			System.out.println("Word \""+m.getWord().value+"\" is not in the DHT");
	}
	
	public void onReceive(EndPoint src, CircularGetMessage m) {

		
		if (m.getSender().equals(endpoint)) {
			answeredQueries++;
			if(m.getMatchingResults().size() > 0) {
				System.out.println("Matching results for patterns: \""+m.getPattern1()+"\"|\""+m.getPattern2()+"\"");
				Iterator<EndPoint> i = m.getMatchingResults().keySet().iterator();
				while(i.hasNext())
					System.out.println(i.next().address.pos);
			}
			else
				System.out.println("No matching results for patterns: \""+m.getPattern1()+"\"|\""+m.getPattern2()+"\"");
		}
		else {
			Pair<Pair<Word,String>,Pair<Word,String>> matchingResults = 
				patternizer(m.getPattern1(),m.getPattern2());
			
			Pair<Word,Word> matchingWords = null;
			if (matchingResults != null)
				matchingWords = new Pair<Word,Word>(
						matchingResults.getFirst().getFirst(),matchingResults.getSecond().getFirst());

			udpSend(rtable.fingers[rtable.fingers.length-1].endpoint,new CircularGetMessage(m,endpoint,matchingWords));
			messagesSent++;
		}
	}
	
/*	public void onReceive(EndPoint src, ChordMessage m) {

		EndPoint nextHop = rtable.nextHop( m.dst);
		if (nextHop != null && nextHop != this.endpoint) {
			Node x = (Node) nextHop.handler;
			System.out.printf("At:%.8f -> dst: %.8f relay-> %.8f\n", chordKey, m.dst, x.chordKey);
			this.udpSend(nextHop, new ChordMessage(m));
		} else {
			System.out.printf("Stopped at: %.8f-> dst: %.8f\n", chordKey, m.dst);
		}
	}*/

	/* Implements the Chord Routing Table */
	class ChordRoutingTable implements Displayable {
		final double ownKey;

		final RTableEntry[] fingers = new RTableEntry[NodeDB.MAX_KEY_LENGTH];

		ChordRoutingTable(Node owner) {
			this.ownKey = owner.chordKey;
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

		class RTableEntry {
			double key;
			EndPoint endpoint;

			RTableEntry(double k, EndPoint e) {
				key = k;
				endpoint = e;
			}
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
}
