package sdm.overlays.project3.expressionSearch;

import static simsim.core.Simulation.rg;

import java.awt.* ;

import java.util.* ;

import simsim.core.*;
import simsim.utils.*;

import sdm.overlays.words.*;
import sdm.overlays.project3.expressionSearch.msgs.*;
import simsim.gui.geom.Line;


public class Node extends AbstractNode implements ExtendedMessageHandler, Displayable {
	private static final int NUM_CONTACTS = 20 ;
	private static final int MAX_SAVED_MSG = 100;
	boolean displayable = false;
	RandomList<Word> words ;
	RandomList<EndPoint> contacts ;
	boolean gotQuery = false;
	boolean startedQuery = false;
	Map<Pair<String,String>,Set<EndPoint>> myQueryAnswers = new HashMap<Pair<String,String>,Set<EndPoint>>();
	Deque<Integer> queryCache = new LinkedList<Integer>();
	HashSet<Integer> myQueriesID = new HashSet<Integer>(10);
	Queue<Pair<EndPoint,Message>> toSendBuffer = new LinkedList<Pair<EndPoint,Message>>(); 
	int messagesSent = 0;


	public Node() {
		super() ;
		NodeDB.store( this ) ;		
	}

	public void init() {
		words = new RandomList<Word>(WordsDB.randomWords(10));
		super.setColor( Color.green ) ;
		contacts = NodeDB.randomEndPoints(this, NUM_CONTACTS) ; // número de contactos até 50
		//contacts = NodeDB.closestNodes( this, NUM_CONTACTS) ; // obtém X nós na proximidade, útil para fazer debug visual da progressão das pesquisas...

		new PeriodicTask(this, rg.nextDouble(), 5.0) {
			public void run() {
				while (queryCache.size() >= MAX_SAVED_MSG)
					queryCache.removeLast();
				Queue<Pair<EndPoint,Message>> currentBuffer = new LinkedList<Pair<EndPoint,Message>>(toSendBuffer);
				toSendBuffer = new LinkedList<Pair<EndPoint,Message>>();
				for (Pair<EndPoint,Message> p : currentBuffer) { 
					udpSend(p.getFirst(), p.getSecond());
					messagesSent++;
				}
				
			}
		};
		
		new PeriodicTask(this, rg.nextDouble(), 5.0) {
			public void run() {
				RandomList<EndPoint> listToSend = new RandomList<EndPoint>();
				listToSend.add(endpoint);
				udpSend( contacts.randomElement(), new ContactExchange(listToSend));
				messagesSent++;
			}
		};
		
		
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
	
	public int computeID(EndPoint origin, String word) {
		return new Random(origin.hashCode() + word.hashCode()).nextInt(1000000);
	}

	public void query(String pattern1, String pattern2) {
//		System.out.println("Querying...waiting for answer");
		startedQuery = true;
		//super.setColor( Color.BLACK ) ;
		SearchQuery thisQuery = new SearchQuery(this.endpoint,pattern1,pattern2,computeID(this.endpoint,pattern1+pattern2),3);
		myQueriesID.add(thisQuery.getID());
		myQueryAnswers.put(new Pair<String,String>(pattern1,pattern2),new HashSet<EndPoint>());
		for( EndPoint i : contacts )
			toSendBuffer.add(new Pair<EndPoint,Message>(
					i,thisQuery));

	}

	public void onReceive(EndPoint src, SearchQuery msg) {
		//TODO: nao mandar isto com flood completo.
		

		gotQuery = true;
		if (!queryCache.contains(msg.getID()) && !myQueriesID.contains(msg.getID())) {
			//super.setColor( Color.WHITE ) ;
//			if (queryCache.size() == MAX_SAVED_MSG)
//				queryCache.removeLast();
			queryCache.addFirst(msg.getID());

			Pair<Pair<Word,String>,Pair<Word,String>> matchingResults = 
				patternizer(msg.getExpression1(),msg.getExpression2());
			
			if (matchingResults != null) {
				toSendBuffer.add(new Pair<EndPoint,Message>(
						msg.getOrigin(),new QueryReply(matchingResults.getFirst(),matchingResults.getSecond(),msg.getID()) ));
				//super.setColor( Color.BLUE );
			}
			//else
				if (msg.getTTL() > 0)
				{
					for (EndPoint x:contacts)
						if (!x.equals(src))
							toSendBuffer.add(new Pair<EndPoint,Message>(x,
									new SearchQuery(msg.getOrigin(),msg.getExpression1(),msg.getExpression2(),
											msg.getID(),msg.getTTL()-1) ));
				}
		}
	}

	public void onReceive(EndPoint src, QueryReply msg) {
		//TODO
		Pair<String,String> receivedPatterns = new Pair<String,String>(msg.getMatchingWord1().getSecond(),msg.getMatchingWord2().getSecond());
		if (myQueriesID.contains(msg.getID()) && !myQueryAnswers.get(receivedPatterns).contains(src)) {
//			System.out.println(this.address.pos + " got an answer to a query from "+src.address.pos);
//			System.out.println("Word: "+msg.getMatchingWord1().getFirst().value+" Pattern: "+msg.getMatchingWord1().getSecond());
//			System.out.println("Word: "+msg.getMatchingWord2().getFirst().value+" Pattern: "+msg.getMatchingWord2().getSecond());
//			System.out.println();
//			
			myQueryAnswers.get(receivedPatterns).add(src);
		}
	}
	
	public void onReceive(EndPoint src, ContactExchange m) {
		Set<EndPoint> x = new HashSet<EndPoint>(contacts);
		x.add(src);
		x.addAll(m.seeds);
		contacts = new RandomList<EndPoint>(x);

		while (contacts.size() > NUM_CONTACTS)
			contacts.removeRandomElement();
		
		queryCache = new LinkedList<Integer>();
	}
	

	public void display(Graphics2D gu, Graphics2D gs) {
		if (displayable)
			for( EndPoint i : contacts )
				gs.draw( new Line( address.pos, i.address.pos ) ) ;
	}


}