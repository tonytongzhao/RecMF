package pre;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;

public class SplitContextMatrix {
	public HashMap<Integer, HashMap<Integer, Integer>> user2rating;
	
	public HashMap<Integer, HashMap<Integer, String>> contextrating;
	
	public HashMap<Integer, HashMap<Integer, Integer>> relationship;
	
	public int numU;
	
	public int numV;
	
	public String line;
	
	public String[] linesplit;
	public HashMap<Integer, String> id2user;

	public HashMap<String, Integer> user2id;

	public HashMap<Integer, String> id2item;

	public HashMap<String, Integer> item2id;

	public HashMap<String, Integer> category2id;
	
	public HashMap<Integer, String> id2category;

	public SplitContextMatrix() {
		user2rating = new HashMap<>();
		relationship=new HashMap<>();
		contextrating=new HashMap<>();
		id2user = new HashMap<Integer, String>();
		user2id = new HashMap<String, Integer>();
		id2item = new HashMap<Integer, String>();
		item2id = new HashMap<String, Integer>();
		id2category = new HashMap<Integer, String>();
		category2id = new HashMap<String, Integer>();
	}

	public void ReadinMatrix(String filename) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(
				new File(filename)));
		int LineNumber = 0;
		while ((line = br.readLine()) != null) {
			LineNumber++;
			linesplit = line.split(" ");
			String user = linesplit[0];
			String item = linesplit[1];
			String rating = linesplit[2];
			if (user2rating.containsKey(finduser(user))) {
				user2rating.get(finduser(user)).put(finditem(item),
						transform(Double.parseDouble(rating)));
			} else {
				HashMap<Integer, Integer> tmphash = new HashMap<Integer, Integer>();
				tmphash.put(finditem(item),
						transform(Double.parseDouble(rating)));
				user2rating.put(finduser(user), tmphash);
				tmphash.clear();
			}

		}
		br.close();
		System.out.println("User Number: " + id2user.size());
		System.out.println("Item Number: " + id2item.size());
		System.out.println("Rating Number: " + LineNumber);

	}
	public void ReadinTJLMatrix(String filename, String relations) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(
				new File(filename)));
		int LineNumber = 0;
		while ((line = br.readLine()) != null) {
			LineNumber++;
			linesplit = line.split(",");
			String user = linesplit[0];
			String item = linesplit[1];
			String category=linesplit[2];
			String rating = linesplit[3];
			String helpness=linesplit[5];
			String time=linesplit[6];
			//System.out.println(finduser(user)+" "+linesplit.length);
			if (contextrating.containsKey(finduser(user))) {
				contextrating.get(finduser(user)).put(finditem(item),
						transform(Double.parseDouble(rating))+"");
			} else {
				HashMap<Integer, String> tmphash = new HashMap<Integer, String>();
				tmphash.put(finditem(item),
						transform(Double.parseDouble(rating))+"");
				contextrating.put(finduser(user), tmphash);
				tmphash.clear();
			}
			

		}
		br.close();
		numU=id2user.size();
		numV=id2item.size();
		System.out.println("User Number: " + id2user.size());
		System.out.println("Item Number: " + id2item.size());
		System.out.println("Rating Number: " + LineNumber);
		br=new BufferedReader(new FileReader(new File(relations)));
		int trustnum=0;
		while((line=br.readLine())!=null)
		{
			trustnum++;
			linesplit = line.split(",");
			String user = linesplit[0];
			String item = linesplit[1];
			if(1>0)
			{
				if (relationship.containsKey(finduser(user))) {
					relationship.get(finduser(user)).put(finduser(item),
							1);
				} else {
					HashMap<Integer, Integer> tmphash = new HashMap<Integer, Integer>();
					tmphash.put(finduser(item),
							1);
					relationship.put(finduser(user), tmphash);
					tmphash.clear();
				}
			}
			
		}

		System.out.println("Trust Number: "+trustnum);
		br.close();
	}

	public void ReadinTrustletEpinionsMatrix(String filename, String relations) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(
				new File(filename)));
		int LineNumber = 0;
		while ((line = br.readLine()) != null) {
			LineNumber++;
			linesplit = line.split("\t");
			String user = linesplit[1];
			String item = linesplit[0];
//			String category=linesplit[3];
			String rating = linesplit[2];
	//		String helpness=linesplit[5];
//			String time=linesplit[6];
			//System.out.println(finduser(user)+" "+linesplit.length);
			if (contextrating.containsKey(finduser(user))) {
				contextrating.get(finduser(user)).put(finditem(item),
						transform(Double.parseDouble(rating))+"");
			} else {
				HashMap<Integer, String> tmphash = new HashMap<Integer, String>();
				tmphash.put(finditem(item),
						transform(Double.parseDouble(rating))+"");
				contextrating.put(finduser(user), tmphash);
				tmphash.clear();
			}
			
			if(LineNumber%10000==0)
			{
				System.out.print(LineNumber);
			}
		}
		br.close();
		numU=id2user.size();
		numV=id2item.size();
		br=new BufferedReader(new FileReader(new File(relations)));
		while((line=br.readLine())!=null)
		{
			linesplit = line.split("\t");
			String user = linesplit[0];
			String item = linesplit[1];
			if(finduser(user)<numU&&finduser(item)<numU)
			{
				if (relationship.containsKey(finduser(user))) {
					relationship.get(finduser(user)).put(finduser(item),
							Integer.parseInt(linesplit[2]));
				} else {
					HashMap<Integer, Integer> tmphash = new HashMap<Integer, Integer>();
					tmphash.put(finduser(item),
							Integer.parseInt(linesplit[2]));
					tmphash.put(finduser(user),
							1);
					
					relationship.put(finduser(user), tmphash);
					tmphash.clear();
				}
			}
			
		}
		System.out.println("User Number: " + id2user.size());
		System.out.println("Item Number: " + id2item.size());
		System.out.println("Rating Number: " + LineNumber);
		br.close();
	}

	public void Split(String trainfile, String testfile) throws Exception {
		BufferedWriter bw1 = new BufferedWriter(new FileWriter(new File(
				trainfile)));
		BufferedWriter bw2 = new BufferedWriter(new FileWriter(new File(
				testfile)));
		int Testnumber = 0;
		int Trainnumber = 0;
		int usertotal = 0;
		Iterator<Integer> it1 = user2rating.keySet().iterator();
		while (it1.hasNext()) {
			int user = it1.next();
			int index = 0;
			while (index <= (int) (user2rating.get(user).size() * 3 / 10)
					&& (int) (user2rating.get(user).size() * 3 / 10) != 0) {
				// System.out.println(index+" "+user2rating.get(user).size()+" "+(int)(
				// user2rating.get(user).size() * 0.3));
				Iterator<Integer> it2 = user2rating.get(user).keySet()
						.iterator();
				while (it2.hasNext()) {
					int item = it2.next();
					if (Math.random() < 0.5
							&& user2rating.get(user).get(item) <= 10
							&& index <= (int) (user2rating.get(user).size() * 0.3)) {
						user2rating.get(user).put(item,
								user2rating.get(user).get(item) + 10);
						index++;
						Testnumber++;
						bw2.write(user + " " + item + " "
								+ (user2rating.get(user).get(item) - 10));
						bw2.newLine();
					}
				}
			}
			usertotal += user2rating.get(user).size();
			Iterator<Integer> it2 = user2rating.get(user).keySet().iterator();
			while (it2.hasNext()) {
				int item = it2.next();
				if (user2rating.get(user).get(item) < 10) {
					Trainnumber++;
					bw1.write(user + " " + item + " "
							+ user2rating.get(user).get(item));
					bw1.newLine();

				}
			}
		}
		bw1.close();
		bw2.close();
		System.out.println("Train number: " + Trainnumber);
		System.out.println("Test number: " + Testnumber);
		System.out.println("Total number: " + usertotal);
	}
	public void Split(String trainfile, String testfile, String relationfile) throws Exception {
		BufferedWriter bw1 = new BufferedWriter(new FileWriter(new File(
				trainfile)));
		BufferedWriter bw2 = new BufferedWriter(new FileWriter(new File(
				testfile)));
		int Testnumber = 0;
		int Trainnumber = 0;
		int usertotal = 0;
		Iterator<Integer> it1 = contextrating.keySet().iterator();
		while (it1.hasNext()) {
			int user = it1.next();
			int index = 0;			
			while (index <= (int) (contextrating.get(user).size() * 3 / 10)
					&& (int) (contextrating.get(user).size() * 3 / 10) != 0) {
				// System.out.println(index+" "+user2rating.get(user).size()+" "+(int)(
				// user2rating.get(user).size() * 0.3));
				Iterator<Integer> it2 = contextrating.get(user).keySet()
						.iterator();
				while (it2.hasNext()) {
					int item = it2.next();
					if (Math.random() < 0.5
							&& !contextrating.get(user).get(item).contains("test")
							&& index <= (int) (contextrating.get(user).size() * 0.3)) {
						contextrating.get(user).put(item,
								contextrating.get(user).get(item) + " test");
						index++;
						Testnumber++;
						bw2.write(user + " " + item + " "
								+ (contextrating.get(user).get(item).split(" ")[0])+" ");
						bw2.newLine();
					}
				}
			}
			usertotal += contextrating.get(user).size();
			Iterator<Integer> it2 = contextrating.get(user).keySet().iterator();
			while (it2.hasNext()) {
				int item = it2.next();
				if (!contextrating.get(user).get(item).contains("test")) {
					Trainnumber++;
					bw1.write(user + " " + item + " "
							+ contextrating.get(user).get(item));
					bw1.newLine();

				}
			}
		}
		bw1.close();
		bw2.close();
		bw1=new BufferedWriter(new FileWriter(new File(relationfile)));
		it1=relationship.keySet().iterator();
		int linenumber=0;
		while(it1.hasNext())
		{
			int user1=it1.next();
			Iterator<Integer> it2=relationship.get(user1).keySet().iterator();
			while(it2.hasNext())
			{
				linenumber++;
				int user2=it2.next();
				bw1.write(user1+" "+user2+" 1");
				bw1.newLine();
			}
		}
		bw1.close();
		System.out.println("Train number: " + Trainnumber);
		System.out.println("Test number: " + Testnumber);
		System.out.println("Total number: " + usertotal);
		System.out.println("Trust number: " + linenumber);
		
	}

	public static int transform(double v) {
		if (v >= 4.5)
			return 5;
		if (v >= 3.5)
			return 4;
		if (v >= 2.5)
			return 3;
		if (v >= 1.5)
			return 2;
		if (v > 0.0)
			return 1;
		return 0;
	}

	public int finduser(String i) {
		if (id2user.containsValue(i)) {
			return user2id.get(i);
		} else {
			int k = id2user.size();
			id2user.put(k, i);
			user2id.put(i, k);
			return k;
		}
	}

	public int finditem(String i) {
		if (id2item.containsValue(i)) {
			return item2id.get(i);
		} else {
			int k = id2item.size();
			id2item.put(k, i);
			item2id.put(i, k);
			return k;
		}
	}
	public int findcategory(String i) {
		if (id2category.containsValue(i)) {
			return category2id.get(i);
		} else {
			int k = id2category.size();
			id2category.put(k, i);
			category2id.put(i, k);
			return k;
		}
	}

	public static void main(String[] args) throws Exception {
		SplitContextMatrix spmatrix = new SplitContextMatrix();
		String TrustletEpinionspath = "D:\\Datasets\\Epinions\\Trustlet Epinions\\user_rating.txt";
		String TrustletEpinionsRating = "rating.txt";
		String TrustletEpinionsRelation="user_rating.txt";
		
		String TJLCiaopath="D:\\Datasets\\ciao\\CIAO_Data\\ciao_with_rating_timestamp_txt";
		String TJLCiaoRating="rating_with_timestamp.txt";
		String TJLCiaoRelation="trust.txt";
		

		String TJLEpinionspath="D:\\Datasets\\Epinions\\epinions_with_timestamp_27_categories\\epinions_with_timestamps_11";
		String TJLEpinionsRating="rating.txt";
		String TJLEpinionsRelation="trust.txt";
		
		spmatrix.ReadinTJLMatrix(TJLEpinionspath + "\\" + TJLEpinionsRating, TJLEpinionspath + "\\" + TJLEpinionsRelation);
		spmatrix.Split(TJLEpinionspath + "\\TJLEpinions_Train.txt", TJLEpinionspath + "\\TJLEpinions_Test.txt", TJLEpinionspath + "\\TJLEpinions_Relations.txt");
		//spmatrix.ReadinMatrix(path + "\\" + Rating);
		//spmatrix.Split(path + "\\Lastfm_Train.txt", path + "\\Lastfm_Test.txt");
	}
}
