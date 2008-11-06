package simsim.core;

import java.awt.*;
import java.util.*;

import simsim.gui.*;
import simsim.net.euclidean.*;
import simsim.net.orbis.*;
import simsim.utils.Threading;

/**
 * The base class for creating a simulation application.
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 * 
 */
abstract public class Simulation implements Displayable {

	/**
	 * The global random number generator used throughout the simulator. If
	 * created with a seed e.g., new Random(1234), it is possible to reproduce a
	 * simulation run.
	 */
	public static Gui Gui;
	public static Random rg;

	public static Spanner Spanner;
	public static Network Network;
	public static Scheduler Scheduler;
	public static Simulation Simulation;

	static GuiDesktop guiDesktop ;
	static Traffic Traffic;
	private static TimeWarpTask timeWarpTask;

	static public enum DisplayFlags {
		SCHEDULER, NETWORK, SIMULATION, SPANNER, TRAFFIC, TIME, TASKS, THREADS;
		Displayable displayable() {
			switch (this) {
			case TRAFFIC:
				return Traffic;
			case SPANNER:
				return Spanner;
			case NETWORK:
				return Network;
			case SCHEDULER:
				return Scheduler;
			case SIMULATION:
				return Simulation;
			case TIME:
				return Scheduler.time ;
			case TASKS:
				return Scheduler.tasks ;
			case THREADS:
				return Scheduler.threads ;
			}
			return null;
		}
	};

	/**
	 * Initialize the simulation environment without the GUI.
	 * 
	 */
	protected Simulation() {
		this(-1, null);
	}

	/**
	 * Initialize the simulation environment.
	 * 
	 * @param fps
	 *            - The number of frames per second intended for the graphical
	 *            display.
	 * @param flags
	 *            - An enumeration of flags used to specify the items to be
	 *            displayed and the display mode. SYNCHRONOUS mode means the
	 *            drawing will be done by the scheduler thread. Slower but
	 *            ensure the simulation state was synchronized with the
	 *            graphical output.
	 */
	protected Simulation(double fps, EnumSet<DisplayFlags> flags) {
		long seed = Globals.get("Sim_RandomSeed", 0L);
		rg = seed == 0L ? new Random() : new Random(seed);

		Simulation = this;
		Spanner = new Spanner();
		Scheduler = new Scheduler();
		Traffic = new Traffic();
		String netName = Globals.get("Net_Type", "Euclidean");

		if (netName == "Orbis")
			Network = new OrbisNetwork();
		else
			Network = new EuclideanNetwork();

		if (fps > 0) {
			Gui = guiDesktop = new GuiDesktop();
			if (flags != null) {
				for (DisplayFlags i : flags)
					Gui.addDisplayable("MainFrame", i.displayable(), fps);
			}
		} else
			Gui = guiDesktop = null;

		Network.init();
	}

	/**
	 * Returns the elapsed time since the simulation started. This is a global
	 * clock that should not be used within nodes for coordination purposes. The
	 * values returned by this method are best used to compute performance
	 * statistics, such as the average number of messages sent by unit of
	 * time...
	 */
	public double currentTime() {
		return Scheduler.now();
	}

	/**
	 * Returns the elapsed time since the simulation started in real time.
	 */
	public double realTime() {
		return Scheduler.rt_now();
	}

	/**
	 * Controls the speed of the simulation relative to real time. For instance,
	 * passing 2.0 will cause the simulation to run up to twice as fast as real
	 * time.
	 */
	public void setSimulationMaxTimeWarp(final double factor) {
		if (timeWarpTask == null)
			timeWarpTask = new TimeWarpTask(factor);
		else
			timeWarpTask.setFactor(factor);
	}

	/**
	 * Starts the simulation. The simulation ends when the task scheduler
	 * completes all pending tasks. (Periodic tasks need to be canceled for this
	 * to happen.)
	 * 
	 */
	protected void start() {
		System.err.println("Starting simulation...");
		if (Gui != null)
			guiDesktop.start();
		Scheduler.start();
		System.err.println("Simulation complete...");
	}

	/**
	 * Explicitly ends the simulation, by canceling all pending tasks. Typically
	 * called when an external observer detects a certain global condition.
	 */
	protected void stop() {
		new Task(0) {
			public void run() {
				System.err.println("Stopping...");
				Scheduler.stop();
			}
		};
	}

	public void display(Graphics2D gu, Graphics2D gs) {
	}

	/**
	 * 
	 * @author Sérgio Duarte (smd@di.fct.unl.pt)
	 * 
	 */
	class TimeWarpTask extends PeriodicTask {

		double factor, warp;
		private double sleep = 10.0;
		private double dynPeriod = 0.00001;

		private double rt_ref, vt_ref;

		TimeWarpTask(double f) {
			super(0, 0.0001);
			this.warp = 1;
			this.factor = f;
			this.rt_ref = realTime();
			this.vt_ref = currentTime();
		}

		void setFactor(double f) {
			if (f != factor) {
				factor = f;
				reSchedule(0);
			}
		}

		public void run() {
			updateWarp();
			dynPeriod = Math.max(0.0001, Math.min(1.0, dynPeriod * (warp > factor ? 0.95 : 1.05)));
			sleep = Math.max(1, Math.min(20.0, sleep * (warp < factor ? 0.95 : 1.05)));

			if (warp > factor)
				Threading.sleep(1);

			this.reSchedule(dynPeriod);
		}

		void updateWarp() {
			double rt_now = realTime(), vt_now = currentTime();
			warp = (vt_now - vt_ref) / Math.max(1e-9, rt_now - rt_ref);
			if (rt_now - rt_ref > 5.0) {
				rt_ref = rt_now;
				vt_ref = vt_now;
			}
		}

	}
}
