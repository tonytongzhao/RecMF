package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class DataManager {

	/**
	 * Construct instance from array
	 * 
	 * @param values
	 * @return instance
	 */
	public int numberofuser = 0;
	public int numberofitem = 0;
	public int numberofcategory = 0;
	public HashMap<Integer, Integer> usermap;
	public HashMap<Integer, Integer> itemmap;

	public DataManager() {
		usermap = new HashMap<Integer, Integer>();
		itemmap = new HashMap<Integer, Integer>();
	}

	public static Instance constructInstance(List<Double> values) {
		Instance instance = new Instance(values.size());
		for (int i = 0; i < values.size(); i++) {
			instance.setValue(i, values.get(i));
		}
		return instance;
	}

	public static Instances generateInstances(double[][] matrix) {
		/* Construct instances skeleton */
		int numfs = matrix[0].length;
		FastVector attList = new FastVector(numfs);
		for (int i = 0; i < numfs; i++) {
			Attribute att = new Attribute("a" + (i + 1));
			attList.addElement(att);
		}

		/* Transform data */
		Instances newData = new Instances("TMP", attList, matrix.length);
		for (int i = 0; i < matrix.length; i++) {
			List<Double> valueList = new ArrayList<Double>(matrix.length);
			for (double f : matrix[i]) {
				valueList.add(f);
			}
			newData.add(constructInstance(valueList));
		}
		newData.setClassIndex(-1);
		return newData;
	}

	public static Map<Triple<Integer, Integer, Integer>, Double> readTripleData(
			String dataName) throws Exception {
		Map<Triple<Integer, Integer, Integer>, Double> tripleList = new HashMap<Triple<Integer, Integer, Integer>, Double>();
		BufferedReader fis = new BufferedReader(new FileReader(dataName));
		String line = null;
		while ((line = fis.readLine()) != null) {
			String items[] = line.split(" ", -1);
			int user = Integer.parseInt(items[0]);
			int item = Integer.parseInt(items[1]);
			double rating = transform(Double.parseDouble(items[2]));
			int category = Integer.parseInt(items[3]);
			Triple<Integer, Integer, Integer> id = new Triple<Integer, Integer, Integer>(
					user, item, category);
			tripleList.put(id, rating);
		}
		fis.close();
		return tripleList;
	}

	public static Map<Integer, Map<Integer, Map<Integer, Integer>>> readTensorData(
			String dataName) throws Exception {
		Map<Integer, Map<Integer, Map<Integer, Integer>>> wholeData = new HashMap<Integer, Map<Integer, Map<Integer, Integer>>>();
		BufferedReader fis = new BufferedReader(new FileReader(dataName));
		String line = null;
		while ((line = fis.readLine()) != null) {
			String items[] = line.split(",", -1);
			int user = Integer.parseInt(items[0]);
			int item = Integer.parseInt(items[1]);
			int tag = Integer.parseInt(items[2]);
			Map<Integer, Map<Integer, Integer>> matrix = null;
			if (wholeData.containsKey(user))
				matrix = wholeData.get(user);
			else
				matrix = new HashMap<Integer, Map<Integer, Integer>>();
			Map<Integer, Integer> list = null;
			if (matrix.containsKey(item))
				list = matrix.get(item);
			else
				list = new HashMap<Integer, Integer>();
			list.put(tag, 1);
			matrix.put(item, list);
			wholeData.put(user, matrix);
		}
		fis.close();
		return wholeData;
	}

	public Map<Integer, Map<Integer, Double>> readTestMatrixData(String dataName)
			throws Exception {
		Map<Integer, Map<Integer, Double>> wholeData = new HashMap<Integer, Map<Integer, Double>>();
		BufferedReader fis = new BufferedReader(new FileReader(dataName));
		String line = null;
		int linenumber = 0;
		while ((line = fis.readLine()) != null) {

			String items[] = line.split("\t");
			int user = Integer.parseInt(items[0]);
			int item = Integer.parseInt(items[1]);
			if (numberofuser <= user) {
				numberofuser = user + 1;
			}
			if (numberofitem <= item) {
				numberofitem = item + 1;
			}
			Double rating = transform(Double.parseDouble(items[2]));
			Map<Integer, Double> list = new HashMap<Integer, Double>();
			if (wholeData.containsKey(user)) {
				list = wholeData.get(user);
			}
			list.put(item, rating);
			wholeData.put(user, list);
			linenumber++;
		}
		fis.close();
		System.out.println("Te: "+linenumber);
		return wholeData;
	}

	public Map<Integer, Map<Integer, Double>> readMatrixData(String dataName)
			throws Exception {
		Map<Integer, Map<Integer, Double>> wholeData = new HashMap<Integer, Map<Integer, Double>>();
		BufferedReader fis = new BufferedReader(new FileReader(dataName));
		String line = null;
		int linenumber = 0;
		numberofitem = 0;
		numberofuser = 0;
		while ((line = fis.readLine()) != null) {
			linenumber++;
			String items[] = line.split("\t");
			int user = Integer.parseInt(items[0]);
			int item = Integer.parseInt(items[1]);
			// System.out.println(user+" "+item );
			if (numberofuser <= user) {
				numberofuser = user + 1;
			}
			if (numberofitem <= item) {
				numberofitem = item + 1;
			}
			Double rating = transform(Double.parseDouble(items[2]));
			Map<Integer, Double> list = new HashMap<Integer, Double>();
			if (wholeData.containsKey(user)) {
				list = wholeData.get(user);
			}
			list.put(item, rating);
			wholeData.put(user, list);
		}
		fis.close();
		System.out.println("Tr: "+linenumber);
		return wholeData;
	}

	public Map<Integer, Map<Integer, Double>> readRelationMatrixData(
			String dataName) throws Exception {
		Map<Integer, Map<Integer, Double>> wholeData = new HashMap<Integer, Map<Integer, Double>>();
		BufferedReader fis = new BufferedReader(new FileReader(dataName));
		String line = null;
		int linenumber = 0;
		while ((line = fis.readLine()) != null) {
			linenumber++;
			String items[] = line.split(" ");
			int user = Integer.parseInt(items[0]);
			int item = Integer.parseInt(items[1]);
			// System.out.println(user+" "+item );
			if (numberofuser <= user) {
				numberofuser = user + 1;
			}
			if (numberofuser <= item) {
				numberofuser = item + 1;
			}
			Double rating =  transform(Double.parseDouble(items[2]));
			Map<Integer, Double> list = new HashMap<Integer, Double>();
			if (wholeData.containsKey(user)) {
				list = wholeData.get(user);
			}
			list.put(item, rating);
			wholeData.put(user, list);
		}
		fis.close();
		System.out.println("relations: "+linenumber);
		return wholeData;
	}

	public Map<Integer, Integer> readCategoryData(String dataName)
			throws Exception {
		Map<Integer, Integer> wholeData = new HashMap<Integer, Integer>();
		BufferedReader fis = new BufferedReader(new FileReader(dataName));
		String line = null;
		int linenumber = 0;
		while ((line = fis.readLine()) != null) {
			linenumber++;
			String items[] = line.split(" ");
			// int user = Integer.parseInt(items[0]);
			int item = Integer.parseInt(items[1]);
			// Double rating = transform(Double.parseDouble(items[2]));
			int category = Integer.parseInt(items[3]);
			if (numberofcategory <= category) {
				numberofcategory = category + 1;
			}
			if(item==36)
			{
				//System.out.println(item+" "+category);
			}	
			wholeData.put(item, category);
		}
		fis.close();
		return wholeData;
	}

	public static List<Integer> set2List(Set<Integer> set) {
		List<Integer> res = new ArrayList<Integer>();
		for (Iterator<Integer> it = set.iterator(); it.hasNext();) {
			res.add(it.next());
		}
		return res;
	}

	public Map<String, List<Integer>> getPairList(String dataName)
			throws Exception {
		Map<String, List<Integer>> pairList = new HashMap<String, List<Integer>>();
		BufferedReader fis = new BufferedReader(new FileReader(dataName));
		String line = null;
		while ((line = fis.readLine()) != null) {
			String items[] = line.split("\t", -1);
			int user = Integer.parseInt(items[0]);
			int item = Integer.parseInt(items[1]);
			int tag = Integer.parseInt(items[2]);
			String key = user + "," + item;
			List<Integer> pList = null;
			if (pairList.containsKey(key))
				pList = pairList.get(key);
			else
				pList = new ArrayList<Integer>();
			pList.add(tag);
			pairList.put(key, pList);
		}
		return pairList;
	}

	public static double transform(double v) {
		return v/4;
		//return (v - 1) / 4;
		/*
		 * if (v >= 4.5) return 5.0; if (v >= 3.5) return 4.0; if (v >= 2.5)
		 * return 3.0; if (v >= 1.5) return 2.0; if (v > 0.0) return 1.0; return
		 * 0.0;
		 */
	}
}
