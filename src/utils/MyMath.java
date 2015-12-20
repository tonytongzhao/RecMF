package utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class MyMath {
	/**
	 * Generate random vector
	 * @param num
	 * @return vector
	 */
	public static double[] generateRand(int num) {
		double vec[] = new double[num];
		for (int i = 0; i < num; i++) {
			vec[i] = Math.random();
		}
		return vec;
	}
	
	/**
	 * Generate random number in [l,u)
	 * @param l
	 * @param u
	 * @return random number
	 */
	public static int generateRand(int l, int u) {
		long seed = (long) (Math.random() * 100000000L);
		Random rand = new Random();
		rand.setSeed(seed);
		return rand.nextInt(u - l) + l;
	}
	
    /**
     * Generate a random number in [l,u)
     * @param l
     * @param u
     * @return random number
     */
    public static double generateRand(double l, double u){
    	long seed = (long)(Math.random()*100000000L);
    	Random rand = new Random();
    	rand.setSeed(seed);
    	return rand.nextDouble()*(u-l);
    }
    
    public static boolean generateRand(){
    	//return true;
    	return generateRand(0.0,1.0)>0.5;
    }
    
    public static double[] getMeanVariance(Map<Integer, Map<Integer, Double>> data, Map<Integer,Integer> uMap, Map<Integer,Integer> vMap){
    	double mean = 0.0; int cnt = 0;
    	for(Iterator<Integer> uit = data.keySet().iterator();uit.hasNext();){
    		int u = uit.next();
    		if(!uMap.containsKey(u)) continue;
    		Map<Integer,Double> vList = data.get(u);
    		for(Iterator<Integer> vit = vList.keySet().iterator(); vit.hasNext();){
    			int v = vit.next();
    			if(!vMap.containsKey(v)) continue;
    			cnt++;
    			mean += vList.get(v);
    		}
    	}
    	mean /= cnt;
    	double variance = 0.0;
    	for(Iterator<Integer> uit = data.keySet().iterator();uit.hasNext();){
    		int u = uit.next();
    		if(!uMap.containsKey(u)) continue;
    		Map<Integer,Double> vList = data.get(u);
    		for(Iterator<Integer> vit = vList.keySet().iterator(); vit.hasNext();){
    			int v = vit.next();
    			if(!vMap.containsKey(v)) continue;
    			variance += (vList.get(v)-mean)*(vList.get(v)-mean);
    		}
    	}
    	variance = Math.sqrt(variance/(cnt-1));
    	double[] res = {mean,variance};
    	return res;
    }
}
