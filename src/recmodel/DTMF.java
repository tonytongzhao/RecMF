package recmodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import utils.Criterion;
import utils.DataManager;
import utils.KVPair;
import utils.MyMath;

@SuppressWarnings("unchecked")
public class DTMF extends AbstractMFModel {
	/**
	 * Trees
	 */
	protected Map<Integer, MFNode>[] trees = null;
	/**
	 * Number of trees
	 */
	public int numTrees = 5;
	/**
	 * Depth of the tree
	 */
	public int depth = 3;
	/**
	 * Minimal number of users in leaf node
	 */
	public int minNum = 5;
	/**
	 * Split using user or item
	 */
	public boolean avg = true;
	
	/**
	 * Initialization
	 * @param fileName
	 * @param numU
	 * @param numV
	 * @param numD
	 * @throws Exception
	 */
	public Map<Integer, Map<Integer, Double>> init(Map<Integer, Map<Integer, Double>> ratings,  int numU, int numV, int numD) throws Exception{
		super.init(ratings, numU, numV, numD);
		trees = new Map[numTrees];
		for(int i=0;i<numTrees;i++)
			trees[i] = new HashMap<Integer, MFNode>();
		minNum = (int) (numU * 0.09);
		
		return ratings;
	}
	
	@Override
	public double predict(int u, int v) throws Exception{
		double p = 0.0;
		for(int i=0;i<numTrees;i++){
			double s = goDownTree(i,0,u,v,1);
			if(avg) s/=(depth+1);
			//System.out.println("s: "+s);
			p+=s;
		}
		return p/numTrees;
	}
	
	/**
	 * Go down from the root to the leaves
	 * @param tid	
	 * @param nid
	 * @param u
	 * @param v
	 * @param h
	 * @return prediction value
	 * @throws Exception 
	 */
	private double goDownTree(int tid, int nid, int u, int v, int h) throws Exception{
		MFNode current = trees[tid].get(nid);
		//System.out.println("current node "+u+" "+v+" nid "+nid);
		double res = 0.0;
		//System.out.println("current umap"+current.uidMap.size());
		//System.out.println("current imap"+current.vidMap.size());
		if(!current.vidMap.containsKey(v)||!current.uidMap.containsKey(u))
		{
			return res;
		}
		int upt = current.uidMap.get(u);
		int vpt = current.vidMap.get(v);
		
		for(int i=0;i<numD;i++)
			res += current.U[upt][i]*current.V[vpt][i];
		if(current.leftNode == null || current.rightNode == null) {
			//System.out.print(nid+" ");
			//fw.write(nid+" ");
			return res;
		}
		if(current.userBased){
			if(current.leftNode.containsKey(u)){
				if(avg) return res + goDownTree(tid,nid*2+1,u,v,h+1);
				return goDownTree(tid,nid*2+1,u,v,h+1);
			}
			else {
				if(avg) return res + goDownTree(tid,nid*2+2,u,v,h+1);
				return goDownTree(tid,nid*2+2,u,v,h+1);
			}
		}
		else{
			if(current.leftNode.containsKey(v)){
				if(avg) return res + goDownTree(tid,nid*2+1,u,v,h+1);
				return goDownTree(tid,nid*2+1,u,v,h+1);
			}
			else {
				if(avg) return res + goDownTree(tid,nid*2+2,u,v,h+1);
				return goDownTree(tid,nid*2+2,u,v,h+1);
			}
		}
	}
	
	@Override
	public void buildModel() throws Exception{
		for(int i=0;i<numTrees;i++){
			MFNode root = new MFNode(numU,numV,numD);
			int uid = 0, vid = 0;
			for(int j=0;j<numU;j++) root.uidMap.put(j, uid++);
			for(int j=0;j<numV;j++) root.vidMap.put(j, vid++);
			root.userBased =MyMath.generateRand();
			buildTree(0,i,0,root);
		}
	}
	
	/**
	 * Build a single tree
	 * @param h
	 * @param tid
	 * @param nid
	 * @param current
	 * @param l
	 * @throws Exception
	 */
	private void buildTree(int h, int tid, int nid, MFNode current) throws Exception{
		update(current.U, current.V, current);
		double[] res = MyMath.getMeanVariance(ratings,current.uidMap,current.vidMap);
		//System.out.println("h="+h+" tid="+tid+" nid="+nid+" mean="+res[0]+" variance="+res[1]);
		if(h>=depth || current.uidMap.size()<=minNum || current.vidMap.size()<=minNum){
			current.leftNode = null;
			current.rightNode = null;
			trees[tid].put(nid, current);
			return;
		}
		Map<Integer,Integer>[] subNodes = null;
		if(current.userBased)
			subNodes = partition(current.U, DataManager.set2List(current.uidMap.keySet()), current.uidMap);
		else subNodes = partition(current.V, DataManager.set2List(current.vidMap.keySet()), current.vidMap);
		if(subNodes[0].size() == 0 || subNodes[1].size() == 0){
			current.leftNode = null;
			current.rightNode = null;
			trees[tid].put(nid, current);
			return;
		}
		current.leftNode = subNodes[0];
		current.rightNode = subNodes[1];		
		trees[tid].put(nid, current);
		List<Integer> lL = DataManager.set2List(subNodes[0].keySet());
		List<Integer> rL = DataManager.set2List(subNodes[1].keySet());
		MFNode lNode = null, rNode = null;
		double[][] lS = new double[subNodes[0].size()][];
		double[][] rS = new double[subNodes[1].size()][];
		if(current.userBased){
			for(int i=0;i<lL.size();i++)
				lS[subNodes[0].get(lL.get(i))] = current.U[current.uidMap.get(lL.get(i))];
			for(int i=0;i<rL.size();i++)
				rS[subNodes[1].get(rL.get(i))] = current.U[current.uidMap.get(rL.get(i))];
			lNode = new MFNode(lS,current.V);
			lNode.uidMap = subNodes[0];
			lNode.vidMap = current.vidMap;
			lNode.userBased = MyMath.generateRand();
			rNode = new MFNode(rS,current.V);
			rNode.uidMap = subNodes[1];
			rNode.vidMap = current.vidMap;
			rNode.userBased = MyMath.generateRand();
		}
		else{
			for(int i=0;i<lL.size();i++)
				lS[subNodes[0].get(lL.get(i))] = current.V[current.vidMap.get(lL.get(i))];
			for(int i=0;i<rL.size();i++)
				rS[subNodes[1].get(rL.get(i))] = current.V[current.vidMap.get(rL.get(i))];
			lNode = new MFNode(current.U,lS);
			lNode.uidMap = current.uidMap;
			lNode.vidMap = subNodes[0];
			lNode.userBased = MyMath.generateRand();
			rNode = new MFNode(current.U,rS);
			rNode.uidMap = current.uidMap;
			rNode.vidMap = subNodes[1];
			rNode.userBased = MyMath.generateRand();
		}
		buildTree(h+1,tid,nid*2+1,lNode);
		buildTree(h+1,tid,nid*2+2,rNode);
	}
	
	/**
	 * Partition the data according to their factor values
	 * @param U
	 * @param l
	 * @param idMap
	 * @return two partitions
	 * @throws Exception
	 */
	protected Map<Integer,Integer>[] partition(double[][] latentMatrix, List<Integer> l, Map<Integer, Integer> idMap) throws Exception{
		int sF = MyMath.generateRand(0, numD);
		double[] newM = new double[latentMatrix.length];
		for(int i=0;i<latentMatrix.length;i++)
			newM[i] = latentMatrix[i][sF];
		Arrays.sort(newM);
		int rt = newM.length/2+MyMath.generateRand(-newM.length/10, newM.length/10);
		double pt = newM[rt];
		Map<Integer,Integer>[] res = new Map[2];
		res[0] = new HashMap<Integer,Integer>();
		res[1] = new HashMap<Integer,Integer>();
		int ri = 0, li = 0;
		for(int i=0;i<l.size();i++){
			if(latentMatrix[idMap.get(l.get(i))][sF]<=pt) res[0].put(l.get(i),li++);
			else res[1].put(l.get(i),ri++);
		}
		return res;
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
			sumMAP += Criterion.getMAP(p, r, 100, 0.1);
			num++;
		}
		return sumMAP / num;
	}
	
	/**
	 * Update the latent matrices
	 * @param U
	 * @param V
	 * @param current
	 */
	public void update(double[][] U, double[][] V, MFNode current){
		for(int i=0;i<numIt;i++){
			for(Iterator<Integer> uit=current.uidMap.keySet().iterator();uit.hasNext();){
				int u = uit.next();
				int uid = current.uidMap.get(u);
				Map<Integer, Double> row = ratings.get(u);
				if(row==null) continue;
				for(Iterator<Integer> vit=row.keySet().iterator();vit.hasNext();){
					int v = vit.next();
					if(!current.vidMap.containsKey(v)) continue;
					int vid = current.vidMap.get(v);
					double prediction = 0.0;
					for(int d=0;d<numD;d++)
						prediction += U[uid][d]*V[vid][d];
					double e = prediction - row.get(v);
					for(int d=0;d<numD;d++){
						U[uid][d] -= learningRate * (e*V[vid][d] + lambda*U[uid][d]);
						V[vid][d] -= learningRate * (e*U[uid][d] + lambda*V[vid][d]);
					}
				}					
			}
		}
		
		List<Double> trueValues = new ArrayList<Double>();
		List<Double> preValues = new ArrayList<Double>();
		for(Iterator<Integer> uit=current.uidMap.keySet().iterator();uit.hasNext();){
			int u = uit.next();
			int uid = current.uidMap.get(u);
			Map<Integer, Double> row = ratings.get(u);
			if(row==null) continue;
			for(Iterator<Integer> vit=row.keySet().iterator();vit.hasNext();){
				int v = vit.next();
				if(!current.vidMap.containsKey(v)) continue;
				int vid = current.vidMap.get(v);
				double r = row.get(v), p = 0.0;
				for(int d=0;d<numD;d++)
					p += U[uid][d]*V[vid][d];
				trueValues.add(r);
				preValues.add(p);
			}	
		}
		//System.out.println(Criterion.getRMSE(preValues, trueValues));
		
	}
	
	/**
	 * Tree Node
	 * @author purlin
	 *
	 */
	public class MFNode{
		public double[][] U;		//User factor vector
		public double[][] V;		//Item factor vector
		public boolean userBased; 	//User-base or Item-base
		public Map<Integer,Integer> leftNode = new HashMap<Integer,Integer>();	//user id in left node
		public Map<Integer,Integer> rightNode = new HashMap<Integer,Integer>();	//user id in right node
		public Map<Integer, Integer> uidMap = new HashMap<Integer, Integer>();	//id map for user
		public Map<Integer, Integer> vidMap = new HashMap<Integer, Integer>();	//id map for item
		public MFNode(int m, int n, int d){
			U = new double[m][d];
			V = new double[n][d];
			for(int i=0;i<m;i++)
				U[i] = MyMath.generateRand(d);
			for(int i=0;i<n;i++)
				V[i] = MyMath.generateRand(d);
		}
		public MFNode(double[][] U, double[][] V){
			this.U = new double[U.length][U[0].length];
			this.V = new double[V.length][V[0].length];
			//System.out.println(U[0].length);
			for(int i=0;i<U.length;i++)
				for(int d=0;d<U[i].length;d++)
					this.U[i][d] = U[i][d];
			for(int i=0;i<V.length;i++)
				for(int d=0;d<V[i].length;d++)
					this.V[i][d] = V[i][d];
		}
	}
}
