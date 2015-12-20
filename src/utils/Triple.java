package utils;

public class Triple<Ta,Tb,Tc> {
	public Ta uid;
	public Tb vid;
	public Tc tid;
	public Triple(Ta u, Tb v, Tc t){
		uid = u; vid = v; tid = t;
	}
}