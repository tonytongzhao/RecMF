
package recmodel;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import utils.Criterion;
import utils.KVPair;

public abstract class AbstractMFModel {
	/**
	 * Rating data
	 */
	protected Map<Integer, Map<Integer, Double>> ratings = null;
	/**
	 * Relation data
	 */
	protected Map<Integer, Map<Integer, Double>> relations = null;

	protected Map<Integer, Integer> categories = null;

	/**
	 * Number of users
	 */
	protected int numU = -1;
	/**
	 * Number of items
	 */
	protected int numV = -1;
	/**
	 * Number of latent factors
	 */
	protected int numD = -1;

	protected int numC = -1;
	/**
	 * learning rate of SGD
	 */
	protected double learningRate = 0.01;
	/**
	 * Number of iterations of SGD
	 */
	protected int numIt = 100;
	/**
	 * Regular weight
	 */
	protected double lambda = 0.01;

	public String fileName = "";


	public Map<Integer, Map<Integer, Double>> init(
			Map<Integer, Map<Integer, Double>> ratings, int numU, int numV,
			int numD) throws Exception {
		this.ratings = ratings;
		this.numU = numU;
		this.numV = numV;
		this.numD = numD;
		return ratings;
	}

	public Map<Integer, Map<Integer, Double>> init(
			Map<Integer, Map<Integer, Double>> ratings,
			Map<Integer, Map<Integer, Double>> relations, int numU, int numV,
			int numD) throws Exception {
		this.ratings = ratings;
		this.relations = relations;
		this.numU = numU;
		this.numV = numV;
		this.numD = numD;
		return ratings;
	}

	public Map<Integer, Map<Integer, Double>> init(
			Map<Integer, Map<Integer, Double>> ratings,
			Map<Integer, Map<Integer, Double>> relations,
			Map<Integer, Integer> categories, int numU, int numV, int numD,
			int numC) throws Exception {
		this.ratings = ratings;
		this.relations = relations;
		this.categories = categories;
		this.numC = numC;
		this.numU = numU;
		this.numV = numV;
		this.numD = numC;
		return ratings;
	}

	public void writePrediction(String fileName,
			Map<Integer, Map<Integer, Double>> testData) throws Exception {
		FileWriter fw = new FileWriter(fileName);
		for (Iterator<Integer> it = testData.keySet().iterator(); it.hasNext();) {
			int u = it.next();
			Map<Integer, Double> row = testData.get(u);
			for (Iterator<Integer> pit = row.keySet().iterator(); pit.hasNext();) {
				int v = pit.next();
				double s = predict(u, v);
				fw.write(u + "," + v + "," + s + "\n");
			}
			fw.flush();
		}
		fw.close();
	}

	public double getMAP(Map<Integer, Map<Integer, Double>> testData,
			Map<Integer, Map<Integer, Double>> trainData) throws Exception {
		double sumMAP = 0.0, num = 0.0;
		for (Iterator<Integer> it = trainData.keySet().iterator(); it.hasNext();) {
			int u = it.next();
			if (!testData.containsKey(u))
				continue;
			Map<Integer, Double> trainList = trainData.get(u);
			Map<Integer, Double> testList = testData.get(u);
			List<KVPair> p = new ArrayList<KVPair>();
			List<KVPair> r = new ArrayList<KVPair>();
			for (Iterator<Integer> tit = testList.keySet().iterator(); tit
					.hasNext();) {
				int v = tit.next();
				r.add(new KVPair(v, testList.get(v)));
			}
			for (int v = 0; v < numV; v++) {
				if (trainList.containsKey(v))
					continue;
				p.add(new KVPair(v, predict(u, v)));
			}
			if (testList.size() >= 20) {

				sumMAP += Criterion.getMAP(p, r, r.size(), 0.001);
				num++;
			}
		}
		return sumMAP / num;
	}

	public double getRecall(Map<Integer, Map<Integer, Double>> testData,
			Map<Integer, Map<Integer, Double>> trainData, int topN)
			throws Exception {
		double sumPrecision = 0.0, num = 0.0;
		double totalrecall = 0;
		for (Iterator<Integer> it = trainData.keySet().iterator(); it.hasNext();) {
			int u = it.next();
			if (!testData.containsKey(u))
				continue;
			Map<Integer, Double> trainList = trainData.get(u);
			Map<Integer, Double> testList = testData.get(u);
			List<KVPair> p = new ArrayList<KVPair>();
			List<KVPair> r = new ArrayList<KVPair>();
			for (Iterator<Integer> tit = testList.keySet().iterator(); tit
					.hasNext();) {
				int v = tit.next();
				r.add(new KVPair(v, testList.get(v)));
			}
			for (int v = 0; v < numV; v++) {
				if (trainList.containsKey(v))
					continue;
				p.add(new KVPair(v, predict(u, v)));
			}
			System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
			Collections.sort(p);
			for (int i = 0; i < topN; i++) {
				if (testList.containsKey(p.get(i).id)) {
					sumPrecision++;
				}
			}
			if (testList.size() >= 5) {
				
				totalrecall += (double) sumPrecision / testList.size();
				num++;
			}

			sumPrecision = 0;
		}
		return totalrecall / num;
	}

	public double getPrecision(Map<Integer, Map<Integer, Double>> testData,
			Map<Integer, Map<Integer, Double>> trainData, int topN)
			throws Exception {
		double sumPrecision = 0.0, num = 0.0;
		double perprecision = 0;
		for (Iterator<Integer> it = trainData.keySet().iterator(); it.hasNext();) {
			int u = it.next();
			if (!testData.containsKey(u))
				continue;
		//	System.out.println("u");
			Map<Integer, Double> trainList = trainData.get(u);
			Map<Integer, Double> testList = testData.get(u);
			List<KVPair> p = new ArrayList<KVPair>();
			List<KVPair> r = new ArrayList<KVPair>();
			for (Iterator<Integer> tit = testList.keySet().iterator(); tit
					.hasNext();) {
				int v = tit.next();
				r.add(new KVPair(v, testList.get(v)));
			}
			for (int v = 0; v < numV; v++) {
				if (trainList.containsKey(v))
					continue;
				p.add(new KVPair(v, predict(u, v)));
			}
			//System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
			Collections.sort(p);
			for (int i = 0; i < topN; i++) {
			//	System.out.println(p.get(i).value);
				if (testList.containsKey(p.get(i).id)) {
					sumPrecision++;
				}
			}
			if (testList.size() >= 5) {

				perprecision += sumPrecision / topN;
				num++;
			}

			sumPrecision = 0;
		}
		return perprecision / num;
	}

	/**
	 * Get prediction error
	 * 
	 * @param testData
	 * @return
	 * @throws Exception
	 */
	public double getError(Map<Integer, Map<Integer, Double>> testData)
			throws Exception {
		List<Double> r = new ArrayList<Double>();
		List<Double> p = new ArrayList<Double>();
		//BufferedWriter bw =new BufferedWriter(new FileWriter(new File("D:\\CUHK PHD Life\\Lectures\\Big Data\\HW\\assignment2\\data for B\\data\\dataset\\result.dat")));
		for (Iterator<Integer> it = testData.keySet().iterator(); it.hasNext();) {
			int u = it.next();
			Map<Integer, Double> row = testData.get(u);
			for (Iterator<Integer> pit = row.keySet().iterator(); pit.hasNext();) {
				int v = pit.next();
				// fw.write(v+" ");

				double s = predict(u, v);
				//bw.write(s+"\n");
				r.add(row.get(v));
				p.add(s);

			}
		}
	//	bw.close();
		return Criterion.getRMSE(p, r);
	}

	public double getSpecificError(Map<Integer, Map<Integer, Double>> testData,
			int userID) throws Exception {
		double specificmae = 0;
		int specificu = userID;
		List<Double> r = new ArrayList<Double>();
		List<Double> p = new ArrayList<Double>();
		for (Iterator<Integer> it = testData.keySet().iterator(); it.hasNext();) {
			int u = it.next();
			Map<Integer, Double> row = testData.get(u);
			for (Iterator<Integer> pit = row.keySet().iterator(); pit.hasNext();) {
				int v = pit.next();
				// fw.write(v+" ");

				double s = predict(u, v);
				// fw.write(row.get(v)+"\n");
				r.add(row.get(v));
				p.add(s);
				if (u == specificu) {
					specificmae += (row.get(v) - s) * (row.get(v) - s);
				//	 System.out.println(specificmae);
				}
			}
			if (u == specificu) {
				return Math.sqrt(specificmae / row.size());
			}
		}
		return Criterion.getRMSE(p, r);
	}

	/**
	 * Get prediction error
	 * 
	 * @param testData
	 * @return
	 * @throws Exception
	 */
	public double getMAE(Map<Integer, Map<Integer, Double>> testData)
			throws Exception {
		List<Double> r = new ArrayList<Double>();
		List<Double> p = new ArrayList<Double>();
		for (Iterator<Integer> it = testData.keySet().iterator(); it.hasNext();) {
			int u = it.next();
			Map<Integer, Double> row = testData.get(u);
			for (Iterator<Integer> pit = row.keySet().iterator(); pit.hasNext();) {
				int v = pit.next();
				double s = predict(u, v);
				//System.out.println(s+" "+row.get(v));
				r.add(row.get(v));
				p.add(s);
			}
		}
		return Criterion.getMAE(p, r);
	}

	public double getSpecificMAE(Map<Integer, Map<Integer, Double>> testData,
			int userID) throws Exception {
		double specificmae = 0;
		int specificu = userID;
		List<Double> r = new ArrayList<Double>();
		List<Double> p = new ArrayList<Double>();
		for (Iterator<Integer> it = testData.keySet().iterator(); it.hasNext();) {
			int u = it.next();
			Map<Integer, Double> row = testData.get(u);
			for (Iterator<Integer> pit = row.keySet().iterator(); pit.hasNext();) {
				int v = pit.next();
				double s = predict(u, v);
				r.add(row.get(v));
				p.add(s);
				if (u == specificu) {
					specificmae += Math.abs(row.get(v) - s);
					// System.out.println(specificmae);
				}
			}
			if (u == specificu) {
				return specificmae / row.size();

			}
		}
		return Criterion.getMAE(p, r);
	}

	/**
	 * Predict the rating of a <user,item> pair
	 * 
	 * @param u
	 * @param v
	 * @return prediction value
	 * @throws Exception
	 */
	public abstract double predict(int u, int v) throws Exception;

	/**
	 * Build the Model
	 * 
	 * @throws Exception
	 */
	public abstract void buildModel() throws Exception;

	public void update(double[][] U, double[][] V,
			Map<Integer, Map<Integer, Double>> currentratings,
			Map<Integer, Integer> idMap) {
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
				for (int d = 0; d < numD; d++)
					prediction += U[u][d] * V[v][d];
				double e = prediction - row.get(v);

				for (int d = 0; d < numD; d++) {
					U[u][d] -= learningRate * (e * V[v][d] + lambda * U[u][d]);
					V[v][d] -= learningRate * (e * U[u][d] + lambda * V[v][d]);
				}
			}
		}
		// System.out.println(Criterion.getRMSE(U,V,currentratings));
	}
}
