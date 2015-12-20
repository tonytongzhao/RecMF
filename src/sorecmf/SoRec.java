package sorecmf;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import recmodel.AbstractMFModel;
import utils.MyMath;

public class SoRec extends AbstractMFModel {
	/**
	 * User latent factors
	 */
	protected double[][] U;
	protected double[][] Z;
	/**
	 * Item latent factors
	 */
	protected double[][] V;
	/**
	 * ID mapping for users
	 */
	
	protected int[] indegree;
	protected int[] outdegree;
	
	protected Map<Integer, Integer> idMap = new HashMap<Integer, Integer>();

	protected double beta = 0.01;
	protected double lambdac= 10;
	

	@Override
	public Map<Integer, Map<Integer, Double>> init(
			Map<Integer, Map<Integer, Double>> ratings,
			Map<Integer, Map<Integer, Double>> relations, int numU, int numV,
			int numD) throws Exception {
		super.init(ratings, relations, numU, numV, numD);
		U = new double[super.numU][numD];
		Z = new double[super.numU][numD];
		V = new double[super.numV][numD];
		indegree=new int[super.numU];
		outdegree=new int[super.numU];
		for (int i = 0; i < super.numU; i++)
		{
			indegree[i]=0;
			outdegree[i]=0;
			U[i] = MyMath.generateRand(numD);
			Z[i] = MyMath.generateRand(numD);
		}
		
		
		for (int i = 0; i < super.numV; i++)
			V[i] = MyMath.generateRand(numD);
		System.out.println(super.numU + " " + super.numV);
		Iterator<Integer> userit=relations.keySet().iterator();
		while(userit.hasNext())
		{
			int user1=userit.next();
			Iterator<Integer> userit2=relations.get(user1).keySet().iterator();
			outdegree[user1]+=relations.get(user1).size();
			while(userit2.hasNext())
			{
				int user2=userit2.next();
				indegree[user2]++;
			}
		}
		userit=relations.keySet().iterator();
		while(userit.hasNext())
		{
			int user1=userit.next();
			Iterator<Integer> userit2=relations.get(user1).keySet().iterator();
			while(userit2.hasNext())
			{
				int user2=userit2.next();
				double ck=0;
				ck=Math.sqrt(indegree[user2]/(outdegree[user1]+indegree[user2]));
				relations.get(user1).put(user2, ck);
			}
		}
		return ratings;
	}

	@Override
	public void buildModel() throws Exception {
		calculatesimilarity();
		for (int i = 0; i < numIt; i++) {
			//System.out.println("Iteration "+i);
			Sorecupdate(U, V, Z, ratings, relations);
		}
	}

	public void Sorecupdate(double[][] U, double[][] V,double[][] Z,
			Map<Integer, Map<Integer, Double>> currentratings,
			Map<Integer, Map<Integer, Double>> relations) {

		for (Iterator<Integer> uit = currentratings.keySet().iterator(); uit
				.hasNext();) {
			int u = uit.next();
			int uid = u;
			// if(idMap!=null) uid = idMap.get(u);
			Map<Integer, Double> row = currentratings.get(u);
			if (row == null)
				continue;
			for (Iterator<Integer> vit = row.keySet().iterator(); vit.hasNext();) {
				int v = vit.next();
				double prediction = 0.0;
				// System.out.println(u+" "+v);
				for (int d = 0; d < numD; d++)
					prediction += U[u][d] * V[v][d];
				
				double prediction1=Math.exp(prediction)/((1+Math.exp(prediction))*(1+Math.exp(prediction)));
				double e = 1/(1+Math.exp(-prediction)) - row.get(v);
				//double e = prediction- row.get(v);
				
				for (int d = 0; d < numD; d++) {
					U[u][d] -= learningRate * (prediction1*e * V[v][d] + lambda * U[u][d]);
					V[v][d] -= learningRate * (prediction1*e * U[u][d] + lambda * V[v][d]);
					
				}
			}
		}
		for (int i = 0; i < numU; i++) {
			if (relations.containsKey(i)) {
				Iterator<Integer> itp = relations.get(i).keySet().iterator();
				while (itp.hasNext()) {
					int p = itp.next();
					double ip=0;
					for(int d=0;d<numD;d++)
					{
						ip+=U[i][d]*Z[p][d];
					}
					for (int d = 0; d < numD; d++) {
						U[i][d] -= learningRate
								* (lambdac * (Math.exp(ip)/((1+Math.exp(ip))*(1+Math.exp(ip))))*(1/(1+Math.exp(-ip))-relations.get(i).get(p))*Z[p][d]+lambda*Z[p][d]);

						Z[p][d]-=learningRate
								* (lambdac * (Math.exp(ip)/((1+Math.exp(ip))*(1+Math.exp(ip))))*(1/(1+Math.exp(-ip))-relations.get(i).get(p))*U[i][d]+lambda*U[i][d]);
					}
				}
			}
		}

	}

	@Override
	public double predict(int u, int v) {
		double p = 0.0;
		// int uid = idMap.get(u);
		for (int i = 0; i < numD; i++) {
			p += U[u][i] * V[v][i];
		}
//		System.out.println(1/(1+Math.exp(-p)));
		return 1/(1+Math.exp(-p));
		
	}

	public void calculatesimilarity() {
		Iterator<Integer> it1;
		double ui = 0;
		double uj = 0;
		double uij = 0;
		int commonitem = 0;
		Iterator<Integer> itu = relations.keySet().iterator();
		while (itu.hasNext()) {
			int i = itu.next();
			Iterator<Integer> itz = relations.get(i).keySet().iterator();
			while (itz.hasNext()) {
				boolean hascommon = false;
				int j = itz.next();
				double avi = averagerate(i);
				double avj = averagerate(j);
				if(!ratings.containsKey(i))
				{
					break;
				}
				it1 = ratings.get(i).keySet().iterator();
				while (it1.hasNext()) {
					commonitem = it1.next();
					if (ratings.containsKey(j)&&ratings.get(j).containsKey(commonitem)) {
						hascommon = true;
						ui += (ratings.get(i).get(commonitem) - avi)
								* (ratings.get(i).get(commonitem) - avi);
						uj += (ratings.get(j).get(commonitem) - avj)
								* (ratings.get(j).get(commonitem) - avj);
						uij += (ratings.get(i).get(commonitem) - avi)
								* (ratings.get(j).get(commonitem) - avj);

					}
				}
				if (hascommon == true) {
					relations
							.get(i)
							.put(j,
									(uij / (Math.sqrt(ui) * Math.sqrt(uj) + 1) + 1) / 2);

				} else {
					relations.get(i).put(j, 0.001);
				}
				hascommon = false;
				ui = 0;
				uj = 0;
				uij = 0;
			}
		}
	}

	public double averagerate(int user) {
		double avnum = 0;
		if (ratings.containsKey(user)) {
			Iterator<Integer> it = ratings.get(user).keySet().iterator();
			while (it.hasNext()) {
				avnum += ratings.get(user).get(it.next());
			}
			avnum = avnum / ratings.get(user).size();

		}
		return avnum;
	}

}
