package simsim.net.orbis;

import java.io.*;
import java.awt.*;
import java.util.*;
import java.awt.geom.* ;
import java.awt.image.*;

import javax.vecmath.*;

import simsim.core.*;
import simsim.utils.*;
import simsim.graphs.*;

public class OrbisNetwork extends Network {
	
	private String filename ;
	private int localLoopClasses ;
	private double corePerHopLatency ;
	private double localLoopPerClassLatencyFactor ;
	
	private Random random ;
	private final WeakHashMap<OrbisAddress, Integer> nodes = new WeakHashMap<OrbisAddress, Integer>();

	public Network init() {
		long seed = Globals.get("Sim_RandomSeed", 0L ) ;
		random = seed == 0L ? new Random() : new Random( seed ) ;

		localLoopClasses = Globals.get("Net_Orbis_LocalLoopClasses", 9 ) ;
		corePerHopLatency = Globals.get("Net_Orbis_CorePerHopLatency", 0.025) ;
		filename = Globals.get("Net_Orbis_Filename", "src/simsim/net/orbis/topos/500");
		localLoopPerClassLatencyFactor = Globals.get("Net_Orbis_LocalLoopPerClassLatencyFactor", 0.01 ) ;
		
		loadGraph() ;
		loadNodeCoordinates() ;
		processGraph() ;
		
		return this ;
	}
	
	public void setRandomSeed( long seed ) {
		random = seed == 0L ? new Random() : new Random( seed ) ;		
	}
	
	public NetAddress createAddress( MessageHandler handler) {
		OrbisAddress res = new OrbisAddress(handler);
		nodes.put(res, 0);
		return res;
	}

	public NetAddress replaceAddress( NetAddress other) {		
		OrbisAddress res = new OrbisAddress((OrbisAddress)other);
		nodes.put(res, 0);
		return res;
	}

	private BufferedImage img = null ;	
	public void display(Graphics2D gu, Graphics2D gs) {
		
		if (img == null && N > 0 ) {
			img = gu.getDeviceConfiguration().createCompatibleImage( N, N, Transparency.TRANSLUCENT);

			float M = Integer.MIN_VALUE ;
			for( int i = 0 ; i < N ; i++ )
				for( int j = 0 ; j < N ; j++ )
					M = Math.max( M, hopCount[i][j]) ;
					
			for( int i = 0 ; i < N ; i++ )
				for( int j = 0 ; j < N ; j++ ) {
					int r = (int)(255*hopCount[i][j] / M);
					int g = 0 ;
					int b = 0 ;
					int a = 64 ;
					img.setRGB(i, j, a << 24 | r << 16 | g << 8 | b ) ;					
				}
		}
		if( img != null ) {	
			gs.drawImage( img, 0, 0, 1000, 1000, null) ;
		}
		
		final double r = 10.0 ;
		gs.setColor( Color.DARK_GRAY ) ;
		for( Point3f[] i : coreLinksXYZ ) {
			XY p1 = project_3D_to_2D( i[0]) ;
			XY p2 = project_3D_to_2D( i[1]) ;
			gs.draw( new Line2D.Double( p1.x, p1.y, p2.x, p2.y )) ;
		}
		gs.setColor( Color.RED ) ;
		for( Point3f i : coreXYZ.values() ) {
			XY p = project_3D_to_2D( i) ;
			gs.fill( new Ellipse2D.Double( p.x-r/2, p.y-r/2, r, r)) ;
		}		
	}
	
	int N = -1 ;
	ArrayList<Point3f[]> coreLinksXYZ = new ArrayList<Point3f[]>() ;
	Map<Integer, Point3f> coreXYZ = new TreeMap<Integer, Point3f>() ;
	ArrayList<Link<Integer>> coreLinks = new ArrayList<Link<Integer>>() ;
	
	private void loadGraph() {
		try {
			FileInputStream fis = new FileInputStream( filename + ".topo" ) ;
			Scanner s = new Scanner( fis ) ;
			while( s.hasNextInt() ) {
				int e1 = s.nextInt() ;
				int e2 = s.nextInt() ;
				coreXYZ.put( e1, new Point3f() ) ;
				coreXYZ.put( e2, new Point3f() ) ;
				coreLinks.add( new Link<Integer>(e1, e2, 1.0f)) ;
			}
			s.close() ;
			fis.close();
		} catch( Exception x ) {
			x.printStackTrace() ;
			System.err.println("Fatal Error. Failed to load network topology file!") ;
			System.exit(0) ;
		}						
	}
	
	private void loadNodeCoordinates() {
		try {
			FileInputStream fis = new FileInputStream( filename + ".topo.xyz" ) ;
			Scanner s = new Scanner( fis ) ;
			while( s.hasNextInt() ) {
				int id = s.nextInt() ;
				double x = s.nextDouble() ;
				double y = s.nextDouble() ;
				double z = s.nextDouble() ;
				Point3f p = new Point3f( (float)x, (float)y, (float)z) ;
				coreXYZ.put( id, p ) ;
			}
			s.close() ;
			fis.close();
			for( Link<Integer> i : coreLinks ) { 
				Point3f[] pair = new Point3f[] { coreXYZ.get(i.v), coreXYZ.get(i.w) } ;
				coreLinksXYZ.add( pair ) ;
			}
		}
		catch( FileNotFoundException x ) {
			System.err.println("Warning...missing xyz node coordinate file. Using random values, instead.") ;
		}
		catch( Exception x ) {
			x.printStackTrace() ;
		}
	}
	
	private boolean loadHopCountMatrix() {
		try {
			FileInputStream fis = new FileInputStream( filename + ".topo.hops" ) ;
			Scanner s = new Scanner( fis ) ;
			for( int i = 0 ; i < N ; i++ )
				for( int j = 0 ; j < N ; j++ ) {
					hopCount[i][j] = s.nextInt() ;
				}
			s.close() ;
			fis.close();
			for( Link<Integer> i : coreLinks ) { 
				Point3f[] pair = new Point3f[] { coreXYZ.get(i.v), coreXYZ.get(i.w) } ;
				coreLinksXYZ.add( pair ) ;
			}
		}
		catch( Exception x ) {
			return false ;
		}
		return true ;
	}
	
	private void saveHopCountMatrix() {
		try {
			FileOutputStream fos = new FileOutputStream( filename + ".topo.hops" ) ;
			PrintStream ps = new PrintStream( fos ) ;
			for( int i = 0 ; i < N ; i++ ) {				
				for( int j = 0 ; j < N ; j++ ) {
					ps.print( hopCount[i][j] + (j == (N-1)? "" : " ")) ;
				}
				ps.println();
			}
			ps.close();
			fos.close();
			System.out.println("Saved hop count matrix...") ;
		}
		catch( Exception x ) {
			x.printStackTrace() ;
		}
	}
	
	int[][] hopCount ;
	ArrayList<Integer> stubNodes = new ArrayList<Integer>() ;
	
	private void processGraph() {
		
		Graph<Integer> graph = new Graph<Integer>( coreXYZ.keySet(), coreLinks) ;
		for( Integer i : graph.nodes() )
			if( graph.edges(i).size() == 1 )
				stubNodes.add(i) ;
		

		N = graph.numberOfNodes() ;
		hopCount = new int[ N ][ N ] ;
		for( int i = 0 ; i < N ; i++ )
			for( int j = 0 ; j < i ; j++ )
				hopCount[i][j] = hopCount[j][i] = Short.MAX_VALUE ;
		
		
		for( Link<Integer> i : coreLinks ) {
			hopCount[i.v][i.w] = hopCount[i.w][i.v] = 1 ;
		}
		
		if( ! loadHopCountMatrix() ) {
			System.out.println("Warning....Missing Hop matrix file.") ;
			System.out.println("Computing hop matrix. May take a while...") ;			
			for( Integer i : coreXYZ.keySet() ) {
				System.out.printf("\rProcessing Node: %d/%d", i, N) ;
				ShortestPathsTree<Integer> ste = new ShortestPathsTree<Integer>( i, graph ) ;	
				for( Integer j : coreXYZ.keySet() ) {
					hopCount[i][j] = hopCount[j][i] = (int)ste.cost(j) ;
				}
			}
			saveHopCountMatrix() ;
			System.out.println("Done.") ;
		}
//		for(int i = 0 ; i < N ; i++ ) {
//			for( int j = 0 ; j < N ; j++ ) 
//				System.out.print( hopCount[i][j] + "  ") ;
//			System.out.println() ;
//		}
		System.out.println("Orbis Network Ready...") ;
	}
	
	private XY project_3D_to_2D( Point3f pos ) {
		final double R = 60000 ;
		double x = 500 + R * (pos.x / (100+pos.z)) ;
		double y =  500 + R * (pos.y / (100+pos.z)) ;
		return new XY( x, y ) ;
	}
	
	class OrbisAddress extends NetAddress {

		int stubRouter ;
		double localLookLatency ;
		
		OrbisAddress( MessageHandler handler) {
			super(handler);
			this.stubRouter = stubNodes.get(random.nextInt( stubNodes.size() )) ;
			this.localLookLatency = localLoopPerClassLatencyFactor * (1 + random.nextInt( localLoopClasses)) ;
			this.pos = project_3D_to_2D( coreXYZ.get( this.stubRouter) ) ;
		}

		OrbisAddress( OrbisAddress other) {
			super(other.endpoint.handler);
			
			this.pos = other.pos ;
			this.stubRouter = other.stubRouter ;
			this.localLookLatency = other.localLookLatency ;
		}

		public double latency( OrbisAddress other) {			
			double ll_lat = this.localLookLatency + other.localLookLatency ;
			double cn_lat = hopCount[this.stubRouter][ other.stubRouter] * corePerHopLatency ;
			return cn_lat + ll_lat ;
		}		

		@Override
		public double latency( NetAddress other) {
			return this.latency((OrbisAddress) other ) ;
		}

		public void display(Graphics2D gu, Graphics2D gs) {
		}

		@Override
		public NetAddress replace() {
			return replaceAddress( this);
		}
	}

	@Override
	public Set<NetAddress> addresses() {
		Set<NetAddress> res = new HashSet<NetAddress>();
		for (Map.Entry<OrbisAddress, Integer> i : nodes.entrySet()) {
			OrbisAddress n = i.getKey();
			if (n != null)
				res.add(n);
		}
		return res;
	}
}