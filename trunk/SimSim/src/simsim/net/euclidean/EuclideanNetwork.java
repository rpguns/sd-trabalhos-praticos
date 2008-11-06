package simsim.net.euclidean;

import java.awt.*;
import java.util.*;

import simsim.core.*;
import simsim.utils.*;
import simsim.gui.geom.*;

public class EuclideanNetwork extends Network {

	private Random random ;
	private float fontSize ;
	private double costFactor;
	private double nodeRadius ;
	private boolean displayLabels ;
	private double minNodeDistance;
	private boolean toggleNodeShape;
	private double squareSideLength ;
	
	private final WeakHashMap<EuclideanAddress, Integer> nodes = new WeakHashMap<EuclideanAddress, Integer>();

	public Network init() {
		long seed = Globals.get("Net_RandomSeed", 0L ) ;
		random = seed == 0L ? new Random() : new Random( seed ) ;

		this.fontSize = Globals.get("Net_FontSize", 12.0f) ;
		this.nodeRadius = Globals.get("Net_Euclidean_NodeRadius", 10.0) ;
		this.costFactor = Globals.get("Net_Euclidean_CostFactor", 0.00001) ;
		this.displayLabels = Globals.get("Net_Euclidean_DisplayNodeLabels", false ) ;
		this.minNodeDistance = Globals.get("Net_Euclidean_MinimumNodeDistance", 15.0) ;
		this.toggleNodeShape = Globals.get("Net_Euclidean_ToggleDrawNodeMode", false ) ;	
		this.squareSideLength = Globals.get("Net_Euclidean_SquareSideLength", 1000.0 ) ;
		
		return this ;
	}
	
	public void setRandomSeed( long seed ) {
		random = seed == 0L ? new Random() : new Random( seed ) ;		
	}

	public NetAddress createAddress( MessageHandler handler) {
		EuclideanAddress res = new EuclideanAddress(handler, getRandomPosition());
		nodes.put(res, 0);
		return res;
	}

	public NetAddress replaceAddress( NetAddress other) {
		EuclideanAddress res = new EuclideanAddress( other.endpoint.handler, other.pos);
		nodes.put(res, 0);
		return res;
	}

	public void display(Graphics2D gu, Graphics2D gs) {
		gs.setStroke( new BasicStroke(0.5f));
		gs.setFont( gs.getFont().deriveFont( fontSize )) ;
		
		for (Map.Entry<EuclideanAddress, Integer> i : nodes.entrySet()) {
			EuclideanAddress n = i.getKey();
			if (n != null)
				n.display( gu, gs);
		}
	}
	
	private XY getRandomPosition() {
		int tries = 0 ;
		ArrayList<EuclideanAddress> res = new ArrayList<EuclideanAddress>(nodes.keySet());
		Searching: for (;;) {
			if( ++tries % 99 == 0 ) {
				//System.err.println("Euclidean network plane too crowded...") ;
				//System.err.println("Reducing minumum allowed node distance by 15%") ;
				minNodeDistance *= 0.85 ;
			}
			XY pos = new XY( random).scale( squareSideLength );
			if (minNodeDistance > 0)
				for (EuclideanAddress i : res)
					if (i != null && i.isOnline() && pos.distance(i.pos) < minNodeDistance)
						continue Searching;

			return pos;
		}
	}

	@Override
	public Set<NetAddress> addresses() {
		Set<NetAddress> res = new HashSet<NetAddress>();
		for (Map.Entry<EuclideanAddress, Integer> i : nodes.entrySet()) {
			EuclideanAddress n = i.getKey();
			if (n != null)
				res.add(n);
		}
		return res;
	}

	class EuclideanAddress extends NetAddress {

		EuclideanAddress(MessageHandler handler, XY pos) {
			super(handler);
			this.pos = pos;
			this.shape = new Circle( pos, nodeRadius ) ;			
		}

		@Override
		public double latency(NetAddress other) {
			return pos.distance(((EuclideanAddress) other).pos) * costFactor;
		}

		public void display(Graphics2D gu, Graphics2D gs) {
			gs.setColor(color);
			
			if (online && !toggleNodeShape )
				gs.fill( shape );
			else
				gs.draw( shape );

			if( online && displayLabels ) {
				gs.setColor(Color.black);
				gs.drawString( super.toString(), (float) shape.x, (float) (shape.y - 0.5 * shape.height));
			}
		}

		@Override
		public NetAddress replace() {
			this.dispose() ;
			return replaceAddress( this);
		}
		
		private Circle shape ; 
	}
}