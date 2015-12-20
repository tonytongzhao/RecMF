package socontextrec;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import recmodel.AbstractMFModel;
import utils.MyMath;

public class SoCategoryRec extends AbstractMFModel {
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
	public HashMap<Integer, HashMap<Integer, Double>> I[];
	public HashMap<Integer, HashMap<Integer, Double>> C[];
	/**
	 * ID mapping for users
	 */
	protected Map<Integer, Integer> idMap = new HashMap<Integer, Integer>();

	protected double beta = 0.1;
	protected double alpha = 0.1;
	protected double gamma = 100;

	@Override
	public Map<Integer, Map<Integer, Double>> init(
			Map<Integer, Map<Integer, Double>> ratings,
			Map<Integer, Map<Integer, Double>> relations,
			Map<Integer, Integer> categories, int numU, int numV, int numD,
			int numC) throws Exception {
		super.init(ratings, relations, categories, numU, numV, numD, numC);
		System.out.println("latent " + this.numD);
		System.out.println(gamma);
		I = new HashMap[numD];
		C = new HashMap[numD];
		U = new double[super.numU][numD];
		V = new double[super.numV][numD];
		for (int i = 0; i < super.numU; i++)
			U[i] = MyMath.generateRand(numD);
		for (int i = 0; i < super.numV; i++)
			V[i] = MyMath.generateRand(numD);
		System.out.println(super.numU + " " + super.numV);
		for (int k = 0; k < numD; k++) {
			I[k] = new HashMap<Integer, HashMap<Integer, Double>>();
			C[k] = new HashMap<Integer, HashMap<Integer, Double>>();
		}
		Iterator<Integer> uit1 = relations.keySet().iterator();
		while (uit1.hasNext()) {
			int i = uit1.next();
			Iterator<Integer> uit2 = relations.get(i).keySet().iterator();
			while (uit2.hasNext()) {
				int j = uit2.next();
				for (int k = 0; k < numD; k++) {
					if (I[k].containsKey(i)) {
						I[k].get(i).put(j, Math.random());
						C[k].get(i).put(j, 0.0);
					} else {
						HashMap<Integer, Double> tmph = new HashMap<>();
						tmph.put(j, Math.random());
						I[k].put(i, tmph);

						HashMap<Integer, Double> tmphh = new HashMap<>();
						tmphh.put(j, 0.0);
						C[k].put(i, tmphh);
					}
				}
			}
		}
		return ratings;
	}

	@Override
	public void buildModel() throws Exception {
		calculatesimilarity();
		for (int i = 0; i < numIt; i++) {
			System.out.println("Iteration " + i + "...");
			SoCategoryRecupdate(U, V, I, C, ratings, relations);
		}
	}

	public void SoCategoryRecupdate(double[][] U, double[][] V,
			HashMap<Integer, HashMap<Integer, Double>>[] I,
			HashMap<Integer, HashMap<Integer, Double>>[] C,
			Map<Integer, Map<Integer, Double>> currentratings,
			Map<Integer, Map<Integer, Double>> relations) {

		for (Iterator<Integer> uit = currentratings.keySet().iterator(); uit
				.hasNext();) {
			int u = uit.next();
			int uid = u;
			// if(idMap!=null) uid = idMap.get(u);
			Map<Integer, Double> row = currentratings.get(u);
			if (row == null || !relations.containsKey(u))
				continue;
			for (Iterator<Integer> vit = row.keySet().iterator(); vit.hasNext();) {
				int v = vit.next();
				int c=categories.get(v);
				double prediction = 0.0;
				// System.out.println(u+" "+v);
				for (int p = 0; p < numD; p++) {
					double mis = 0;
					for (int l = 0; l < numU; l++) {
						if (relations.get(u).containsKey(l)) {
							mis += matchcategory(c, p)*I[p].get(u).get(l) * U[l][p] / numU;
							// System.out.println(p+" "+u+" "+I[p].get(u).get(l)
							// + " " + U[l][p] + " " + I[p].get(u).size());
						}

					}
					mis *= V[v][p];
					prediction += mis;
				}

				double e = prediction - row.get(v);
				//System.out.println(prediction*50+" "+row.get(v));
				for (int d = 0; d < numD; d++) {
					double curtmp = 0;
					Iterator<Integer> curuit = I[d].get(u).keySet().iterator();
					while (curuit.hasNext()) {
						int secuser = curuit.next();
						curtmp +=  matchcategory(c, d)*I[d].get(u).get(secuser) * U[secuser][d];
						// System.out.println(I[d].get(u).get(secuser));
						double tmpvalue = I[d].get(u).get(secuser);
						// System.out.println("C "+C[d].get(u).get(secuser));
						I[d].get(u).put(
								secuser,
								tmpvalue
										- learningRate
										* (e
												* U[secuser][d]
												* V[v][d]
												/ numU
												+ gamma
												* (tmpvalue - C[d].get(u).get(
														secuser)) + lambda
												* tmpvalue));
						// System.out.println(I[d].get(u).get(secuser) + " " +
						// U[u][d]+ " " + V[v][d]);

					}

					U[u][d] -= learningRate
							* (e * matchcategory(c,d)*I[d].get(u).get(u) * V[v][d] / numU + lambda
									* U[u][d]);
					V[v][d] -= learningRate
							* (e * curtmp * U[u][d] / numU + lambda * V[v][d]);

				}
			}
		}
		/*
		 * for (int i = 0; i < numU; i++) { if (relations.containsKey(i)) {
		 * Iterator<Integer> itp = relations.get(i).keySet().iterator(); while
		 * (itp.hasNext()) { int p = itp.next(); // System.out.println(p); for
		 * (int d = 0; d < numD; d++) { U[i][d] -= learningRate (beta *
		 * C[d].get(i).get(p) * (U[i][d] - U[p][d]));
		 * 
		 * } } } }
		 */

	}

	@Override
	public double predict(int u, int v) {
		double prediction = 0.0;
		int categoryid = (int) (Math.random() * numD);
		if (categories.containsKey(v)) {
			categoryid = categories.get(v);
		}
		if (!relations.containsKey(u)) {
			//System.out.println(averagerate(u, categoryid));
			return averagerate(u, categoryid);
		}
		for (int p = 0; p < numD; p++) {
			double mis = 0;
			for (int l = 0; l < numU; l++) {
				if (relations.get(u).containsKey(l)) {
					mis += matchcategory(categoryid, p)*I[p].get(u).get(l) * U[l][p] / I[p].get(u).size();
					// System.out.println(I[p].get(u).get(l) + " " + U[l][p]
					// + " " + I[p].get(u).size());
				}

			}
			mis *= V[v][p];
			prediction += mis;
		}
	//	System.out.println("Prediction " + prediction);

		return prediction;
	}

	public void calculatesimilarity() {
		Iterator<Integer> it1;
		double ui[] = new double[numC];
		double uj[] = new double[numC];
		double uij[] = new double[numC];
		int commonitem = 0;
		Iterator<Integer> itu = relations.keySet().iterator();
		while (itu.hasNext()) {
			int i = itu.next();
			Iterator<Integer> itz = relations.get(i).keySet().iterator();
			while (itz.hasNext()) {
				boolean hascommon = false;
				int j = itz.next();
				for (int index = 0; index < numC; index++) {
					ui[index] = 0;
					uj[index] = 0;
					uij[index] = 0;
				}
				if (!ratings.containsKey(i)) {
					break;
				}
				it1 = ratings.get(i).keySet().iterator();
				while (it1.hasNext()) {
					commonitem = it1.next();
					int category = categories.get(commonitem);
					if (ratings.containsKey(j)
							&& ratings.get(j).containsKey(commonitem)) {
						hascommon = true;
						//System.out.println(category);
						double avi = averagerate(i, category);
						double avj = averagerate(j, category);
						ui[category] += (ratings.get(i).get(commonitem) - avi)
								* (ratings.get(i).get(commonitem) - avi);
						uj[category] += (ratings.get(j).get(commonitem) - avj)
								* (ratings.get(j).get(commonitem) - avj);
						uij[category] += (ratings.get(i).get(commonitem) - avi)
								* (ratings.get(j).get(commonitem) - avj);

					}
				}
				for (int index = 0; index < numC; index++) {
					C[index].get(i).put(i, 1.0);
					I[index].get(i).put(i, Math.random());
					if (hascommon == true) {
						C[index].get(i)
								.put(j,
										(uij[index]
												/ (Math.sqrt(ui[index])
														* Math.sqrt(uj[index]) + 1) + 1) / 2);

					} else {
						C[index].get(i).put(j, Math.random());
					}
				}
				hascommon = false;
			}
		}
	}

	public double averagerate(int user, int category) {
		double avnum = 0;
		double av = 0;
		int index = 0;
		Iterator<Integer> it = ratings.get(user).keySet().iterator();
		while (it.hasNext()) {
			int k = it.next();
		//	System.out.println(k);
		//	System.out.println(categories.get(k));
			av += ratings.get(user).get(k);
			if (categories.get(k) == category) {
				avnum += ratings.get(user).get(k);
				index++;
			}
		}
		if (index == 0) {
			avnum = av / ratings.get(user).size();
		} else {
			avnum = avnum / index;
		}

		return avnum;
	}
	public double matchcategory(int c, int p)
	{
		if(c==p)
			return 1.8;
		else
			return 0.9;
	}
}
