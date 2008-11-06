package simsim.core;

import simsim.graphs.*;
import simsim.utils.*;
import static simsim.core.Simulation.*;

import java.awt.*;
import java.util.*;
import java.awt.geom.*;

/**
 * 
 * Creates a simplified graph covering all the nodes in the network.
 * Can only be used with small sized systems, up to about 200 nodes.
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 * 
 */
public class Spanner implements Displayable {

	
	/**
	 * Controls how the complete graph connecting all nodes will be simplified
	 * to create a cover graph (spanner)
	 * 
	 * @param T -
	 *            Is the maximum cost degradation allowed between any pair of
	 *            nodes in the simplified graph, relative to the cost in the
	 *            original complete graph. Any value larger than 1 is allowed,
	 *            the greater the value, the sparser the resulting graph/tree is.
	 * 
	 */
	public void setThreshold(double T) {
		if (K != T && T > 1) {
			K = (float) T;
			spanner = null;
			trees.clear();
		}
		if (T <= 1)
			throw new RuntimeException("Invalid Argument. Use a value greater than 1.");
	}

	/**
	 * Given a node, chosen as the root node of a shortest paths tree, this
	 * method returns the children of that node.
	 * 
	 * The shortest paths tree is computed as needed from the spanner graph
	 * produced by the current Threshold value, which can be changed
	 * using setThreshold().
	 * 
	 * @param root -
	 *            The root node of the shortest paths tree.
	 * @param parent -
	 *            A node in the network.
	 * @return the children nodes of node selected as parent.
	 */
	public Set<NetAddress> children(NetAddress root, NetAddress parent) {
		Set<NetAddress> res = shortestPathsTree(root).children(parent);
		assert res != null;
		return res;
	}

	// ------------------------------------------------------------------------------------------------------------------
	public void display(Graphics2D gu, Graphics2D gs) {

		gs.setColor(Color.gray);
		gs.setStroke(new BasicStroke(1f));
		for (Link<NetAddress> i : spanner().links()) {
			gs.draw(new Line2D.Double(i.v.pos.x, i.v.pos.y, i.w.pos.x, i.w.pos.y));
		}

		XY mouse = Gui.getMouseXY_Scaled( gs );
		NetAddress closest = null;
		for (NetAddress i : Network.addresses())
			if (closest == null || mouse.distance(i.pos) < mouse.distance(closest.pos))
				closest = i;

		gs.setColor(Color.darkGray);
		gs.setStroke(new BasicStroke(5f));
		if (closest != null && mouse.distance(closest.pos) < 10) {
			for (Link<NetAddress> i : shortestPathsTree(closest).edges()) {
				gs.draw(new Line2D.Double(i.v.pos.x, i.v.pos.y, i.w.pos.x, i.w.pos.y));
			}
		}
	}

	ShortestPathsTree<NetAddress> shortestPathsTree(NetAddress root) {
		ShortestPathsTree<NetAddress> res = trees.get(root);
		if (res == null) {
			//res = new ShortestPathsTree<NetAddress>(root, spanner());
			res = shortestPaths().symetricTree(root) ;
			trees.put(root, res);
		}
		return res;
	}

	kSpanner<NetAddress> spanner() {
		if (spanner == null) {
			spanner = spanner(K);
		}
		return spanner;
	}

	private ShortestPaths<NetAddress> shortestPaths() {
		if (shortestPaths == null) {
			shortestPaths = new ShortestPaths<NetAddress>( spanner(K) ) ;
		}
		return shortestPaths;
	}
	
	private kSpanner<NetAddress> spanner(float f) {
		ArrayList<Link<NetAddress>> n2n = new ArrayList<Link<NetAddress>>();

		ArrayList<NetAddress> eal = new ArrayList<NetAddress>( Network.addresses());

		int N = eal.size();
		for (int i = 0; i < N; i++) {
			NetAddress eI = eal.get(i);
			for (int j = 0; j < i; j++) {
				NetAddress eJ = eal.get(j);
				n2n.add( new Link<NetAddress>(eI, eJ, eI.latency(eJ)));
			}
		}
		return new kSpanner<NetAddress>(f, eal, n2n);
	}
	
	private float K = 5.0f;
	private kSpanner<NetAddress> spanner = null;
	private ShortestPaths<NetAddress> shortestPaths = null;
	private Map<NetAddress, ShortestPathsTree<NetAddress>> trees = new HashMap<NetAddress, ShortestPathsTree<NetAddress>>();
}
