package sdm.time.project6.vector;

import java.util.*;

import simsim.utils.*;

class ProcessDB {
	static RandomList<Process> processes = new RandomList<Process>();
	static TreeMap<Integer, Process> k2p = new TreeMap<Integer, Process>();

	static void store(Process n) {
		processes.add(n) ;
		k2p.put( n.index, n ) ;
	}

	Set<Integer> keys() {
		return k2p.keySet();
	}

	static int size() {
		return k2p.size();
	}

	static Process randomProcess() {
		return processes.randomElement();
	}
}
