package sdm.time.project6.logical;

import java.awt.*;

import simsim.core.*;
import simsim.utils.*;
import simsim.gui.geom.*;
import static simsim.core.Simulation.*;

import sdm.time.project6.timeStamps.msgs.*;

public class Node extends AbstractNode implements NodeMessageHandler, Displayable {

	public int index;
	private NetAddress parent;

	public Node() {
		super(true); // This node will have its own clock, with drift and skew...
		index = NodeDB.store(this);
		super.setColor( Color.getHSBColor(0.2f, 0.5f, 0.9f));
	}

	public String toString() {
		return "" + index;
	}

	public void init( NetAddress rootNode) {
		if (rootNode == this.address) {
			clock.isMasterClock(true);
			super.setColor( Color.getHSBColor(0.0f, 0.5f, 0.8f));
		}

		// Dado o nó com o relógio de referência, que será raiz da árvore de
		// sincronização, obtém o pai do nó corrente nessa árvore.
		parent = Spanner.parent(rootNode, this.address);

		if (parent != null)
			initClockSynchronizationTask();
	}

	// --------------------------------------------------------------------------------------------------------------
	// Tarefa para a sincronização dos relógios...
	// Usa algoritmo de Cristian numa árvore.
	// i.e., Cada nó, periodicamente, pede o valor do relógio ao pai, calcula o
	// desvio com a resposta e faz o acerto no seu relógio.
	
	private void initClockSynchronizationTask() {
		new PeriodicTask(this, 10 * rg.nextDouble(), 5.0) {
			public void run() {
				udpSend(parent, new SyncTimeRequest(currentTime()));
			}
		};
	}

	public void onReceive(EndPoint src, SyncTimeRequest m) {
		udpSend(src, new SyncTimeReply(m.timeStamp, this.currentTime()));
	}

	public void onReceive(EndPoint src, SyncTimeReply m) {
		double rtt = this.currentTime() - m.timeStamp;

		double offset = (m.referenceTime + rtt / 2) - this.currentTime();

		super.clock.adjustClock(offset);
	}

	public void display(Graphics2D gu, Graphics2D gs) {
		
		gs.setStroke(new BasicStroke(3.0f));
		address.display(gu, gs) ;
		
		double UTC = Simulation.currentTime();

		XY a = address.pos;
		XY b = new XY(a.x + 0, a.y + 5000 * (super.currentTime() - UTC));

		gs.setColor(Color.blue);
		gs.setStroke(new BasicStroke(5.0f));
		gs.draw( new Line( a, b )) ;
	}

}
