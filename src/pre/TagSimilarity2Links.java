package pre;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class TagSimilarity2Links {
	public String tmp;
	public String[] tmpsplit;
	public HashMap<Integer, Vector<Integer>> trustlinks;
	public HashMap<Integer, HashMap<Integer, Double>> trustrelation;
	public HashMap<Integer, HashMap<Integer, Double>> similarityrelation;
	int totallink=0;
	int estimatelink=0;
	public TagSimilarity2Links() {
		trustlinks = new HashMap<>();
		trustrelation=new HashMap<>();
		similarityrelation= new HashMap<>();
	}

	public void ReadinF(String f) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(new File(f)));
		int maxuser=0;
		while ((tmp = br.readLine()) != null) {
			tmpsplit = tmp.split(" ");
			totallink++;
			Vector<Integer> v = new Vector<>();
		
			int user1 = Integer.parseInt(tmpsplit[0]);
			int user2 = Integer.parseInt(tmpsplit[1]);
			if(user1==user2)
			{
				System.out.println("GOGOGO!");
			}
			if(user1>maxuser)
			{
				maxuser=user1;	
			}
			if(user2>maxuser)
			{
				maxuser=user2;	
			}
			if(trustrelation.containsKey(user1))
			{
				trustrelation.get(user1).put(user2, 1.0);
			}
			else
			{
				HashMap<Integer, Double> tmphash=new HashMap<>();
				tmphash.put(user2, 1.0);
				trustrelation.put(user1,tmphash);
			}
			if (trustlinks.containsKey(user1)) {
				v = trustlinks.get(user1);
				v.add(user2);
				trustlinks.put(user1, v);
			} else {
				v.add(user2);
				trustlinks.put(user1, v);
			}
		}
		System.out.println("Links: "+totallink);
		br.close();
		totallink=0;
		Iterator<Integer> it=trustrelation.keySet().iterator();
		while(it.hasNext())
		{
			int user1=it.next();
			Iterator<Integer> it2=trustrelation.get(user1).keySet().iterator();
			while(it2.hasNext())
			{
				int user2=it2.next();
				totallink++;
			}
		}
		System.out.println("ALL: "+totallink+" "+maxuser);
	}

	public void Similarity(String file, String filestar, double threshold) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		BufferedWriter bw =new BufferedWriter(new FileWriter(new File(filestar)));
		int totallink=0;
		int simlinks=0;
		int maxuser=0;
		while ((tmp = br.readLine()) != null) {
			tmpsplit = tmp.split(" ");
			simlinks++;
			//System.out.println(tmpsplit[0]+" "+Math.round(Double.parseDouble(tmpsplit[0])));
			int user1 = (int)Math.round(Double.parseDouble(tmpsplit[0]))-1;
			int user2 = (int)Math.round(Double.parseDouble(tmpsplit[1]))-1;
			if(user1>maxuser)
			{
				maxuser=user1;	
			}
			if(user2>maxuser)
			{
				maxuser=user2;	
			}
			double value = Double.parseDouble(tmpsplit[2]);
			if(trustrelation.containsKey(user1)&&trustrelation.get(user1).containsKey(user2))
			{
				totallink++;
				if(similarityrelation.containsKey(user1))
				{
					similarityrelation.get(user1).put(user2, value);
				}
				else
				{
					HashMap<Integer, Double> hasht=new HashMap<>();
					hasht.put(user2, value);
					similarityrelation.put(user1, hasht);
				}
			}
			if(trustrelation.containsKey(user2)&&trustrelation.get(user2).containsKey(user1))
			{
				totallink++;
				if(similarityrelation.containsKey(user2))
				{
					similarityrelation.get(user2).put(user1, value);
				}
				else
				{
					HashMap<Integer, Double> hasht=new HashMap<>();
					hasht.put(user1, value);
					similarityrelation.put(user2, hasht);
				}
			}
		}
		System.out.println("Max "+maxuser);
		System.out.println(simlinks);
		System.out.println(totallink);
		totallink=0;
		Iterator<Integer> it=similarityrelation.keySet().iterator();
		while(it.hasNext())
		{
			int user1=it.next();
			Iterator<Integer> it2=similarityrelation.get(user1).keySet().iterator();
			while(it2.hasNext())
			{
				totallink++;
				int user2=it2.next();
				bw.write(user1+" "+user2+" "+similarityrelation.get(user1).get(user2));
				bw.newLine();
			}
		}
		System.out.println("AL: "+totallink);
		bw.close();
	}
	
	public double[] Similarity(String file, double threshold) throws Exception {
		double[] similarityvalue = new double[10];
		double pair[] = new double[10];
		double proportion = 0;
		for (int i = 0; i < pair.length; i++) {
			pair[i] = 0;
			similarityvalue[i] = 0; 	
		}
		BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		while ((tmp = br.readLine()) != null) {
			tmpsplit = tmp.split(" ");
			int user1 = (int) Double.parseDouble(tmpsplit[0]);
			int user2 = (int) Double.parseDouble(tmpsplit[1]);
			double value = Double.parseDouble(tmpsplit[2]);
			/*		int i = (int) (value / 0.1);
			//System.out.println(value+" "+i);
			if(i==10)
			{i=9;}
			pair[i]++;
			if ((trustlinks.containsKey(user1)
					&& trustlinks.get(user1).contains(user2))||(trustlinks.containsKey(user2)
							&& trustlinks.get(user2).contains(user1))) {
				similarityvalue[i]++;
				estimatelink++;
			}
			*/
			for(int i=0;i<similarityvalue.length;i++)
			{
				if((int)(value/0.1)>=i)
				{
					pair[i]++;
					if ((trustlinks.containsKey(user1)
							&& trustlinks.get(user1).contains(user2))||(trustlinks.containsKey(user2)
									&& trustlinks.get(user2).contains(user1))) {
						similarityvalue[i]++;
						estimatelink++;
					}
				}
			}
		
		}
		br.close();
		for (int i = 0; i < pair.length; i++) {
			//System.out.println(similarityvalue[i]+" "+pair[i]);
			similarityvalue[i] /= estimatelink;
			System.out.println(similarityvalue[i]);
		}
		System.out.println(estimatelink);
		return similarityvalue;
	}

	public static void main(String args[]) throws Exception {
		TagSimilarity2Links tsl = new TagSimilarity2Links();
		String simi = "D:\\Tsinghua\\Paper\\发表筹备\\Soc+Short Text\\Matlab Code\\Lastfm all pair similarity.txt";
		String F = "D:\\Tsinghua\\Paper\\发表筹备\\Soc+Short Text\\Matlab Code\\Lastfm\\Lastfm v1 sigmoid\\F.txt";
		String F_star = "D:\\Tsinghua\\Paper\\发表筹备\\Soc+Short Text\\Matlab Code\\Lastfm\\Lastfm v1 sigmoid\\F_star.txt";
		String Dsimi = "D:\\Tsinghua\\Paper\\发表筹备\\Soc+Short Text\\Matlab Code\\Delicious all pair similarity.txt";
		String DF = "D:\\Tsinghua\\Paper\\发表筹备\\Soc+Short Text\\Matlab Code\\Delicious\\Delicious v1 sigmoid\\F.txt";
		String DF_star = "D:\\Tsinghua\\Paper\\发表筹备\\Soc+Short Text\\Matlab Code\\Delicious\\Delicious v1 sigmoid\\F_star.txt";
		tsl.ReadinF(F);
		tsl.Similarity(simi, 0.1);
		
	}

}
