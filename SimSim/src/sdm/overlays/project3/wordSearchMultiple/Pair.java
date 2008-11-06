package sdm.overlays.project3.wordSearchMultiple;

public class Pair<K,V> {
	
	protected K first;
	protected V second;
	
	public Pair(K first, V second) {
		this.first = first;
		this.second = second;
	}
	
	public K getFirst() {
		return this.first;
	}
	
	public V getSecond() {
		return this.second;
	}

}
