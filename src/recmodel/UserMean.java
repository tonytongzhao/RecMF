package recmodel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UserMean extends AbstractMFModel{
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
			U[i][0] = 0;
		for(int i=0;i<super.numV;i++)
			V[i][0] = 0;
		System.out.println(super.numU+" "+super.numV);
		/*int uid = 0;
		for(Iterator<Integer> uit=ratings.keySet().iterator();uit.hasNext();)
			idMap.put(uit.next(), uid++);
		*/
		return ratings;
	}
	
	@Override
	public void buildModel() throws Exception{
		for (Iterator<Integer> uit = ratings.keySet().iterator(); uit
				.hasNext();) {
			int u = uit.next();
			int uid = u;
			// if(idMap!=null) uid = idMap.get(u);
			Map<Integer, Double> row = ratings.get(u);
			if (row == null)
				continue;
			for (Iterator<Integer> vit = row.keySet().iterator(); vit.hasNext();) {
				int v = vit.next();
				U[uid][0]+=ratings.get(uid).get(v);
			}
			U[uid][0]=(double)(U[uid][0])/ratings.get(uid).size();
		}
	}
	
	@Override
	public double predict(int u, int v){
		
		return U[u][0];
	}
}
