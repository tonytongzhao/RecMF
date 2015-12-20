package utils;

public class Pair implements Comparable<Pair>{
	public int a,b;
	public Pair(int a,int b){
		this.a = a; this.b = b;
	}
	@Override
	public int compareTo(Pair other) {
		if(this.a<other.a) return -1;
		else if(this.a==other.a && this.b<other.b) return -1;
		else if(this.a==other.a && this.b==other.b) return 0;
		return 1;
	}
}
