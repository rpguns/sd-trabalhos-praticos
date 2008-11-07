package sdm.overlays.project3.expressionSearch;

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

	public boolean equals (Object o) {
		Pair<K,V> other = (Pair<K,V>) o;
		return this.getFirst().equals(other.getFirst()) && this.getSecond().equals(other.getSecond());
	}
	
	public int hashCode() {
		return this.getFirst().hashCode() + this.getSecond().hashCode();
	}
	
}
