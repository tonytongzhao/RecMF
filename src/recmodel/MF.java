package recmodel;

import java.util.HashMap;
import java.util.Map;

import utils.MyMath;

public class MF extends AbstractMFModel{
	/**
	 * User latent factors
	 */
	protected double[][] U;
	/**
	 * Item latent factors
	 */
	protected double[][] V;
	/**
	 * ID mapping for users
	 */
	protected Map<Integer, Integer> idMap = new HashMap<Integer, Integer>();
	
	@Override
	public Map<Integer, Map<Integer, Double>> init(Map<Integer, Map<Integer, Double>> ratings,  int numU, int numV, int numD) throws Exception{
		super.init(ratings, numU, numV, numD);
		U = new double[super.numU][numD];
		V = new double[super.numV][numD];
		for(int i=0;i<super.numU;i++)
			U[i] = MyMath.generateRand(numD);
		for(int i=0;i<super.numV;i++)
			V[i] = MyMath.generateRand(numD);
		System.out.println(super.numU+" "+super.numV);
		/*int uid = 0;
		for(Iterator<Integer> uit=ratings.keySet().iterator();uit.hasNext();)
			idMap.put(uit.next(), uid++);
		*/
		return ratings;
	}
	
	@Override
	public void buildModel() throws Exception{
		for(int i=0;i<numIt;i++){
			update(U,V,ratings,idMap);
		}
	}
	
	@Override
	public double predict(int u, int v){
		double p = 0.0;
		//int uid = idMap.get(u);
		for(int i=0;i<numD;i++){
			p += U[u][i]*V[v][i];
		}
		return p;
	}
}
