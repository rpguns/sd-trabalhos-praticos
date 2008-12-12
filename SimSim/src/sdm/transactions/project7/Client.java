package sdm.transactions.project7;

import java.awt.*;
import java.util.*;

import simsim.core.*;
import simsim.ext.rmi.*;

import sdm.transactions.project7.msgs.*;
import sdm.transactions.common.*;
import sdm.transactions.common.grid.*;
import static simsim.core.Simulation.*;
import static sdm.transactions.common.transaction.Transaction.*;

public class Client extends Node {

	public ClientMiddleware myRemoteInterface;
	
	public Client() {
		super(Color.green);
		myRemoteInterface = new ClientMiddleware(this);
	}

	protected long tid = -1;

	public void init() {

		new Task(this, 50 * rg.nextDouble()) {
			public void run() {
				try {
					if (rg.nextDouble() < 0.05)
						doCentralizedBlank();
					else
						doCentralizedCircle();

				} catch (RemoteException x) {
					System.err.println("Error...[Server offline?]");
				}
				reSchedule(20 + 30 * rg.nextDouble());
			}
		};
	}

	void doCentralizedBlank() {
		TransactionalGridOperations tgo = Naming.lookupServer(endpoint(0), "//Server" + rg.nextInt(ServerDB.size()) + "/tgo");
		
		long tid = tgo.openTransaction(myRemoteInterface);
		myRemoteInterface.addTransaction(tid);

		int[] gs = tgo.gridSize(tid);

		int c = rg.nextInt() >>> 1; // make positive
		for (int i = 0; i < gs[0]; i++)
			for (int j = 0; j < gs[1]; j++) {
				tgo.writeColor(tid, i, j, c);
				tgo.writeShape(tid, i, j, c);
			}
		Result res = tgo.closeTransaction(tid);

		System.out.println("Exit: doBlank: " + res);
		myRemoteInterface.removeTransaction(tid);
	}

	void doCentralizedCircle() {
		TransactionalGridOperations tgo = Naming.lookupServer(endpoint(0), "//Server" + rg.nextInt(ServerDB.size()) + "/tgo");

		tid = tgo.openTransaction(myRemoteInterface);
		myRemoteInterface.addTransaction(tid);

		int[] gs = tgo.gridSize(tid);

		int c = rg.nextInt() >>> 1 ;
		int radius = rg.nextInt( gs[0] );
		boolean inColor = rg.nextBoolean();		
		for (int ij : circleCoords(gs[0], gs[1], radius/10 + 2)) {
			int x = ij >> 16, y = ij & 0xFFFF;
			c += inColor ? tgo.readColor(tid, x, y) : tgo.readShape(tid, x, y) ;			
		}

		for (int ij : circleCoords(gs[0], gs[1], radius/4 + 5)) {

			int x = ij >> 16, y = ij & 0xFFFF;

			if (inColor)
				tgo.writeColor(tid, x, y, c);
			else
				tgo.writeShape(tid, x, y, c);
		}

		Result res = tgo.closeTransaction(tid);
		tid = -1;
		System.out.println("Exit: doCentralizedCircle: " + res);
		myRemoteInterface.removeTransaction(tid);
	}

	/**
	 * Produces a set of cell coordinates <i, j> for a circumference spanning a
	 * single grid (in a single server)
	 * 
	 * @param gs
	 *            size of the grid stored by each server
	 * @return The set of integers corresponding to the coordinates of the cells
	 *         that must be written to. Each coordinate (x,y) is encoded as a 32
	 *         bit integer: x in the high 16 bits, y in the low 16 bits.
	 */
	Set<Integer> circleCoords(int width, int height, int radius) {
		Set<Integer> res = new TreeSet<Integer>();

		radius =  Math.max( 5, radius ) ;
		int centerX = rg.nextInt(width), centerY = rg.nextInt(height);

		for (double angle = 0; angle < 3.14; angle += 0.01) {
			double x = centerX + radius * Math.cos(angle);
			double dy = radius * Math.sin(angle);
			for (double y = centerY - dy; y < centerY + dy; y += 0.1) {
				int i = (int) x, j = (int) y;
				if (i >= 0 && i < width && j >= 0 && j < height)
					res.add(i << 16 | j);
			}
		}
		return res;
	}

	public void crash() {
		myRemoteInterface.clear();
		this.dispose();
	}

	public String toString() {
		return "Client " + key ;
	}
}
