package sdm.overlays.project4.expressionSearch_Traveler;

import java.awt.*;
import java.util.*;


import sdm.overlays.project4.expressionSearch_Traveler.msgs.*;
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

	//Coloca os pares word/endpoint na DHT
	public void initWordDHT() {
		for (Word w : words) 
			onReceive(endpoint, new PutMessage(w,endpoint));
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

	public void circularQuery(String p1, String p2) {
		Pair<String,String> queryKey = new Pair<String,String>(p1,p2);
		queryBuffers.put(queryKey,
				new HashMap<EndPoint,Pair<Word,Word>>(10));
		queryTimers.put(queryKey, 0);

		this.travel(endpoint,p1,p2,0,rtable.fingers[0].endpoint,PARTITION_LEVELS-1);
		udpSend(rtable.fingers[0].endpoint, new TravelMessage(endpoint,p1,p2,0,endpoint,PARTITION_LEVELS-1));
		sentMessages++;
	}

	public void travel( EndPoint returnPath, String p1, String p2, int nFinger, EndPoint destination, int currentLevel) {

		//Caso base
		if (currentLevel == 0) {

			onReceive(endpoint, new CircularMessage(returnPath,p1,p2,destination));

		} else {

			//Recursividade local
			this.travel(returnPath, p1, p2, nFinger+1, 
					rtable.fingers[nFinger+1].endpoint, currentLevel-1);

			//Recursividade distribuida
			udpSend(rtable.fingers[nFinger+1].endpoint, 
					new TravelMessage(returnPath,p1,p2,nFinger+1,destination,currentLevel-1));
			sentMessages++;
		}

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
			sentMessages++;
		}
		else {
			HashSet<EndPoint> previous = wordDictionary.get(m.getWord().dHashValue());
			if (previous == null) previous = new HashSet<EndPoint>();
			previous.add(m.getOrigin());
			wordDictionary.put(m.getWord().dHashValue(),previous);
			//System.out.println("Saved word: "+m.getWord().value+" at nodeKey: "+this.chordKey+" with wordKey " + m.getWord().dHashValue());
		}
	}

	public void onReceive(EndPoint src, TravelMessage m) {

		this.travel(m.getReturnPath(), m.getPattern1(), m.getPattern2(), 
				m.getFingerNumber(), m.getDestination(), m.getLevel());

	}

	public void onReceive(EndPoint src, CircularMessage m) {

		if (m.getDestination().equals(endpoint)) {
			udpSend(m.getReturnPath(),new ReplyMessage(m.getPattern1(),m.getPattern2(),m.getMatchingResults(),m.getHopCount()));
			sentMessages++;
		}
		else {
			Pair<Pair<Word,String>,Pair<Word,String>> matchingResults = 
				patternizer(m.getPattern1(),m.getPattern2());

			Pair<Word,Word> matchingWords = null;
			if (matchingResults != null)
				matchingWords = new Pair<Word,Word>(
						matchingResults.getFirst().getFirst(),matchingResults.getSecond().getFirst());

			udpSend(rtable.fingers[rtable.fingers.length-1].endpoint, new CircularMessage(m,endpoint,matchingWords));
			sentMessages++;
			setColor(m.getColor());
		}
	}

	public void onReceive(EndPoint src, ReplyMessage m) {

		//System.out.println("Node "+endpoint.address.pos+" received a reply from "+
		//src.address.pos+" with a knowledge of "+m.getNodes()+" nodes");
		Pair<String,String> queryKey = 
			new Pair<String,String>(m.getPattern1(),m.getPattern2());

		Map<EndPoint,Pair<Word,Word>> queryResults = 
			queryBuffers.get(queryKey);
		queryResults.putAll(m.getMatchingResults());

		queryTimers.put(queryKey,queryTimers.get(queryKey)+1);
		if (queryTimers.get(queryKey) == Math.pow(2,PARTITION_LEVELS)) {
			answeredQueries++;
			if (queryResults.size() > 0) {
				System.out.println("Node "+endpoint.address.pos+" found the following matches for this pattern ( "+
						m.getPattern1()+" | "+m.getPattern2()+" )");
				int i = 1;
				for (EndPoint p : queryResults.keySet()) {
					System.out.println("Answer #"+i+": ( "+
							queryResults.get(p).getFirst().value+" | "+queryResults.get(p).getSecond().value+ " ) @ node "+p.address.pos);
					i++;
				}
			} else {
				System.out.println("Node "+endpoint.address.pos+" found no matches for this pattern ( "+
						m.getPattern1()+" | "+m.getPattern2()+" )");
			}

			System.out.println();

		}
	}

//	public void onReceive(EndPoint src, GetMessage m) {

//	EndPoint nextHop = rtable.nextHop( m.getDst() );
//	if (nextHop != null && nextHop != this.endpoint)
//	this.udpSend(nextHop, new GetMessage(m));
//	else {
//	if (wordDictionary.containsKey(m.getWord().dHashValue()))
//	udpSend(m.getSender(),new GetReply(m.getWord(),wordDictionary.get(m.getWord().dHashValue())) );
//	else
//	udpSend(m.getSender(),new GetReply(m.getWord(),null));
//	}
//	}

//	public void onReceive(EndPoint src, GetReply m) {

//	if(m.getNodes() != null) {
//	System.out.println("Word \""+m.getWord().value+"\" is at Nodes :");
//	Iterator<EndPoint> i = m.getNodes().iterator();
//	while(i.hasNext())
//	System.out.println(i.next().address.pos);
//	}
//	else
//	System.out.println("Word \""+m.getWord().value+"\" is not in the DHT");
//	}

//	public void onReceive(EndPoint src, CircularGetMessage m) {


//	if (m.getSender().equals(endpoint))
//	if(m.getMatchingResults().size() > 0) {
//	System.out.println("Matching results for patterns: \""+m.getPattern1()+"\"|\""+m.getPattern2()+"\"");
//	Iterator<EndPoint> i = m.getMatchingResults().keySet().iterator();
//	while(i.hasNext())
//	System.out.println(i.next().address.pos);
//	}
//	else
//	System.out.println("No matching results for patterns: \""+m.getPattern1()+"\"|\""+m.getPattern2()+"\"");
//	else {
//	Pair<Pair<Word,String>,Pair<Word,String>> matchingResults = 
//	patternizer(m.getPattern1(),m.getPattern2());

//	Pair<Word,Word> matchingWords = null;
//	if (matchingResults != null)
//	matchingWords = new Pair<Word,Word>(
//	matchingResults.getFirst().getFirst(),matchingResults.getSecond().getFirst());

//	udpSend(rtable.fingers[rtable.fingers.length-1].endpoint,new CircularGetMessage(m,endpoint,matchingWords));

//	}
//	}


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
