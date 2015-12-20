package utils;

public class KVPair implements Comparable<KVPair> {
	
	public int id;
	
	public double value;
	
	public KVPair(int id, double val){
		this.id = id;
		this.value = val;
	}
	
	@Override
	public int compareTo(KVPair arg) {
		return (this.value - arg.value)>0.0 ? -1 : 1;
	}
}
