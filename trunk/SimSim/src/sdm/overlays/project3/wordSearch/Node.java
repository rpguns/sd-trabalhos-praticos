package sdm.overlays.project3.wordSearch;

import static simsim.core.Simulation.rg;

import java.awt.* ;
import java.util.* ;

import simsim.core.*;
import simsim.utils.*;

import sdm.overlays.words.*;
import sdm.overlays.project3.wordSearch.msgs.*;
import simsim.gui.geom.Line;


public class Node extends AbstractNode implements ExtendedMessageHandler, Displayable {
	private static final int NUM_CONTACTS = 20 ;
	private static final int MAX_SAVED_MSG = 20;
	boolean displayable = false;
	Set<Word> words ;
	RandomList<EndPoint> contacts ;
	boolean gotQuery = false;
	//Dictionary<SearchQuery,SearchQuery> something = new Hashtable<SearchQuery,SearchQuery>(NUM_SAVED_MSG);
	Deque<Integer> queryCache = new LinkedList<Integer>();
	HashSet<Integer> myQueriesID = new HashSet<Integer>(10);
	Queue<Pair<EndPoint,Message>> toSendBuffer = new LinkedList<Pair<EndPoint,Message>>(); 
	int answeredQueries = 0;


	public Node() {
		super() ;
		NodeDB.store( this ) ;		
	}

	public void init() {
		words = WordsDB.randomWords(10);
		super.setColor( Color.green ) ;
		contacts = NodeDB.randomEndPoints(this, NUM_CONTACTS) ; // número de contactos até 50
		//contacts = NodeDB.closestNodes( this, NUM_CONTACTS) ; // obtém X nós na proximidade, útil para fazer debug visual da progressão das pesquisas...

		new PeriodicTask(this, rg.nextDouble(), 2.0) {
			public void run() {
				while (queryCache.size() >= MAX_SAVED_MSG)
					queryCache.removeLast();
				Queue<Pair<EndPoint,Message>> currentBuffer = new LinkedList<Pair<EndPoint,Message>>(toSendBuffer);
				toSendBuffer = new LinkedList<Pair<EndPoint,Message>>();
				for (Pair<EndPoint,Message> p : currentBuffer) 
					udpSend(p.getFirst(), p.getSecond());
				
			}
		};
		
		new PeriodicTask(this, rg.nextDouble(), 10.0) {
			public void run() {
				udpSend( contacts.randomElement(), new SeedExchange(contacts));
			}
		};
		
		
	}

	/*
	 * MESSAGE HANDLERS
	 */
	
	public int computeID(EndPoint origin, Word word) {
		return new Random(origin.hashCode() + word.hashCode()).nextInt(1000000);
	}

	public void query(Word word) {
//		System.out.println("Querying...waiting for answer");
		super.setColor( Color.BLACK ) ;
		SearchQuery thisQuery = new SearchQuery(this.endpoint,word,computeID(this.endpoint,word),5);
		myQueriesID.add(thisQuery.getID());
		for( EndPoint i : contacts )
			toSendBuffer.add(new Pair<EndPoint,Message>(
					i,thisQuery));

	}

	public void onReceive(EndPoint src, SearchQuery msg) {
		//TODO: nao mandar isto com flood completo.
		

		gotQuery = true;
		if (!queryCache.contains(msg.getID()) && !myQueriesID.contains(msg.getID())) {
			super.setColor( Color.WHITE ) ;
//			if (queryCache.size() == MAX_SAVED_MSG)
//				queryCache.removeLast();
			queryCache.addFirst(msg.getID());

			if (words.contains(msg.getWord())) {
				toSendBuffer.add(new Pair<EndPoint,Message>(
						msg.getOrigin(),new QueryReply(msg.getWord(),msg.getID()) ));
				super.setColor( Color.BLUE );
			}
			else
				if (msg.getTTL() > 0)
				{
					for (EndPoint x:contacts)
						if (!x.equals(src))
							toSendBuffer.add(new Pair<EndPoint,Message>(
									x,new SearchQuery(msg.getOrigin(),msg.getWord(),msg.getID(),msg.getTTL()-1) ));
				}
		}
	}

	public void onReceive(EndPoint src, QueryReply msg) {
		//TODO
		if (myQueriesID.contains(msg.getID())) {
			System.out.println(this.address.pos + " got an answer to a query\nID: "+msg.getID()+" Word: "+msg.getWord().value+" Node: "+src.address.pos+"\n");
			answeredQueries++;
			myQueriesID.remove(msg.getID());
		}
	}
	
	public void onReceive(EndPoint src, SeedExchange m) {
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