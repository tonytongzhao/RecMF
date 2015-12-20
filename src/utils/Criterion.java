package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Criterion {
	
	public static double getFMeasure(Map<Integer,Double> preList, Map<Integer,Boolean> trueList){
		double recall = getRecall(preList, trueList);
		double precision = getPrecision(preList, trueList);
		return 2*recall*precision/(recall+precision);
	}
	
	public static double getPrecision(Map<Integer,Double> preList, Map<Integer,Boolean> trueList){
		double numRetrieved = preList.size();
		double union = 0.0;
		for(Iterator<Integer> it = trueList.keySet().iterator();it.hasNext();){
			if(preList.containsKey(it.next()))
				union += 1.0;
		}
		return union/numRetrieved;
	}
	
	public static double getRecall(Map<Integer,Double> preList, Map<Integer,Boolean> trueList){
		double numRelevant = trueList.size();
		double union = 0.0;
		for(Iterator<Integer> it = trueList.keySet().iterator();it.hasNext();){
			if(preList.containsKey(it.next()))
				union += 1.0;
		}
		return union/numRelevant;
	}
	
	public static double getMAE(List<Double> p, List<Double> r){
		double rmse = 0.0;
		for(int i=0;i<p.size();i++)
			rmse += Math.abs(p.get(i)-r.get(i));
		return rmse/p.size();
	}
	
	public static double getRMSE(List<Double> p, List<Double> r){
		double rmse = 0.0;
		for(int i=0;i<p.size();i++)
			rmse += (p.get(i)-r.get(i))*(p.get(i)-r.get(i));
		return Math.sqrt(rmse/p.size());
	}

	public static double getMAP(List<KVPair> p, List<KVPair> r, int K, double R){
		Collections.sort(p);
		Map<Integer, Double> trueList = new HashMap<Integer, Double>();
		for(int i=0;i<r.size();i++)
			trueList.put(r.get(i).id, r.get(i).value);
		double MAP = 0.0;
		for(int i=0;i<K && i<p.size();i++){
			double s = 0.0;
			for(int j=0;j<=i;j++){
				KVPair kv = p.get(i);
				if(trueList.containsKey(kv.id) && kv.value>=0)
					s+=1.0;
			}
			MAP += s/(i+1);
		}
		return MAP/K;
	}
	
	public static double getRMSE(double[][] U, double[][] V, Map<Integer, Map<Integer, Double>> testData){
		List<Double> r = new ArrayList<Double>();
		List<Double> p = new ArrayList<Double>();
		int uid = 0;
		for(Iterator<Integer> it = testData.keySet().iterator();it.hasNext();){
			int u = it.next();
			Map<Integer, Double> row = testData.get(u);
			for(Iterator<Integer> pit = row.keySet().iterator(); pit.hasNext();){
				int v = pit.next();
				r.add(row.get(v));
				double s = 0.0;
				for(int k=0;k<U[0].length;k++)
					s+=U[uid][k]*V[v][k]; 
				p.add(s);
			}
			uid++;
		}
		return getRMSE(p, r);
	}
	

}
