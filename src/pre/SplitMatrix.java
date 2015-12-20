package pre;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class SplitMatrix {
	public HashMap<Integer, HashMap<Integer, Integer>> user2rating;

	public HashMap<Integer, HashMap<Integer, Double>> relationship;

	public String line;

	public String[] linesplit;
	public HashMap<Integer, String> id2user;

	public HashMap<String, Integer> user2id;

	public HashMap<Integer, String> id2item;

	public HashMap<String, Integer> item2id;

	public SplitMatrix() {
		user2rating = new HashMap<>();
		relationship = new HashMap<>();
		id2user = new HashMap<Integer, String>();
		user2id = new HashMap<String, Integer>();
		id2item = new HashMap<Integer, String>();
		item2id = new HashMap<String, Integer>();

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

	public void ReadinMatrix(String filename, String relations)
			throws Exception {
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
		int trust=0;
		br = new BufferedReader(new FileReader(new File(relations)));
		while ((line = br.readLine()) != null) {
			trust++;
			linesplit = line.split(" ");
			String user = linesplit[0];
			String item = linesplit[1];
			String relation = linesplit[2];
			if (relationship.containsKey(finduser(user))) {
				relationship.get(finduser(user)).put(finduser(item), Double.parseDouble(relation));
			} else {
				HashMap<Integer, Double> tmphash = new HashMap<Integer, Double>();
				tmphash.put(finduser(item), Double.parseDouble(relation));
				relationship.put(finduser(user), tmphash);
				tmphash.clear();
			}

		}
		System.out.println("User Number: " + id2user.size());
		System.out.println("Item Number: " + id2item.size());
		System.out.println("Trust Number: " + trust);
	//	System.out.println("Rating Number: " + LineNumber);
		br.close();
	}

	public void ReadinLBSMatrix(String filename, String relations)
			throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(
				new File(filename)));
		int LineNumber = 0;
		while ((line = br.readLine()) != null) {
			LineNumber++;

			//System.out.println(line);
			linesplit = line.split("\t");
			if(linesplit.length!=5)
				continue;
			String user = linesplit[0];
			String item = linesplit[4];
			String rating = linesplit[1].split("-")[0] + ""
					+ linesplit[1].split("-")[1];
			//System.out.println(rating);
			
			if (rating.startsWith("2010")) {

				if (user2rating.containsKey(finduser(user))) {
					user2rating.get(finduser(user)).put(finditem(item),
							Integer.parseInt(rating));
				} else {
					HashMap<Integer, Integer> tmphash = new HashMap<Integer, Integer>();
					tmphash.put(finditem(item),
							Integer.parseInt(rating));
					user2rating.put(finduser(user), tmphash);
					tmphash.clear();
				}
			}
		}
		br.close();
		System.out.println("User Number: " + id2user.size());
		System.out.println("Item Number: " + id2item.size());
		System.out.println("Rating Number: " + LineNumber);
		br = new BufferedReader(new FileReader(new File(relations)));
		while ((line = br.readLine()) != null) {
			linesplit = line.split("\t");
			String user = linesplit[0];
			String item = linesplit[1];
		//	String relation = linesplit[2];
			if(user2id.containsKey(user)&&user2id.containsKey(item))
			{
				if (relationship.containsKey(finduser(user))) {
					relationship.get(finduser(user)).put(finduser(item), 1.0);
				} else {
					HashMap<Integer, Double> tmphash = new HashMap<Integer, Double>();
					tmphash.put(finduser(item), 1.0);
					relationship.put(finduser(user), tmphash);
					tmphash.clear();
				}
			}
		}

		br.close();
	}

	public void SplitLBS(String trainfile, String testfile, String relationfile)
			throws Exception {
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
			Iterator<Integer> it2 = user2rating.get(user).keySet().iterator();
			while (it2.hasNext()) {
				int item = it2.next();
				//System.out.println(user2rating.get(user).get(item));
				if (user2rating.get(user).get(item)>201008) {
					bw2.write(user + " " + item + " 1.0");
					bw2.newLine();
				} else {
					bw1.write(user + " " + item + " 1.0");
					bw1.newLine();
				}
			}
		}
		bw1.close();
		bw2.close();
		bw1 = new BufferedWriter(new FileWriter(new File(relationfile)));
		it1 = relationship.keySet().iterator();
		while (it1.hasNext()) {
			int user1 = it1.next();
			Iterator<Integer> it2 = relationship.get(user1).keySet().iterator();
			while (it2.hasNext()) {
				int user2 = it2.next();
				bw1.write(user1 + " " + user2 + " 1");
				bw1.newLine();
			}
		}
		bw1.close();
		System.out.println("Train number: " + Trainnumber);
		System.out.println("Test number: " + Testnumber);
		System.out.println("Total number: " + usertotal);
	}

	public void Split(String trainfile, String testfile, String relationfile)
			throws Exception {
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
		bw1 = new BufferedWriter(new FileWriter(new File(relationfile)));
		it1 = relationship.keySet().iterator();
		while (it1.hasNext()) {
			int user1 = it1.next();
			Iterator<Integer> it2 = relationship.get(user1).keySet().iterator();
			while (it2.hasNext()) {
				int user2 = it2.next();
				bw1.write(user1 + " " + user2 + " 1");
				bw1.newLine();
			}
		}
		bw1.close();
		System.out.println("Train number: " + Trainnumber);
		System.out.println("Test number: " + Testnumber);
		System.out.println("Total number: " + usertotal);
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
	public void calculatesimilarity() {
		double total=0;
		double number=0;
		Iterator<Integer> it1;
		double ui = 0;
		double uj = 0;
		double uij = 0;
		int commonitem = 0;
		double[] sepsim=new double[5];
		double[] num=new double[5];
		
		Iterator<Integer> itu = relationship.keySet().iterator();
		while (itu.hasNext()) {
			int i = itu.next();
			Iterator<Integer> itz = relationship.get(i).keySet().iterator();
			while (itz.hasNext()) {
				boolean hascommon = false;
				int j = itz.next();
			//	i=(int)(Math.random()*id2user.size());
			//	j=(int)(Math.random()*id2user.size());
			//	System.out.println(i+" "+j);
			//	System.out.println(relationship.get(i).get(j));
			//	int relationid=(int)(relationship.get(i).get(j)/1.0);
				double avi = averagerate(i);
				double avj = averagerate(j);
			//	System.out.println(i+" "+avi);
			//	System.out.println(j+" "+avj);
				if(!user2rating.containsKey(i))
				{
					break;
				}
				it1 = user2rating.get(i).keySet().iterator();
				while (it1.hasNext()) {
					commonitem = it1.next();
					if (user2rating.containsKey(j)&&user2rating.get(j).containsKey(commonitem)) {
						hascommon = true;
						ui += (user2rating.get(i).get(commonitem) - avi)
								* (user2rating.get(i).get(commonitem) - avi);
						uj += (user2rating.get(j).get(commonitem) - avj)
								* (user2rating.get(j).get(commonitem) - avj);
						uij += (user2rating.get(i).get(commonitem) - avi)
								* (user2rating.get(j).get(commonitem) - avj);

					}
				}
				if (hascommon == true&&(Math.sqrt(ui) * Math.sqrt(uj))!=0) {
				//	relationship
				//			.get(i)
				//			.put(j,
				//					((uij) / (Math.sqrt(ui) * Math.sqrt(uj)+1)+1) / 2);
					total+=((uij) / (Math.sqrt(ui) * Math.sqrt(uj))+1) / 2;
					number++;
				//	sepsim[relationid-1]+=((uij) / (Math.sqrt(ui) * Math.sqrt(uj)+1)+1) / 2;
				//	num[relationid-1]++;
				} else {
				//	relationship.get(i).put(j,0.5);
				}
				hascommon = false;
				ui = 0;
				uj = 0;
				uij = 0;
			}
			
		}
		System.out.println("Total: "+total/number);
		for(int i=0;i<num.length;i++)
		{
			System.out.println(sepsim[i]/num[i]);
		}
	}

	public double averagerate(int user) {
		double avnum = 0;
		if (user2rating.containsKey(user)) {
			Iterator<Integer> it = user2rating.get(user).keySet().iterator();
			while (it.hasNext()) {
				avnum += user2rating.get(user).get(it.next());
			}
			avnum = avnum / user2rating.get(user).size();

		}
		return avnum;
	}
	public void calculateTrustorandee() throws Exception
	{
		System.out.println("Calculate Trustor Stat");
		System.out.println("Calculate Trustor Stat");
		HashMap<Integer, Integer> num2trustee=new HashMap<>();
		Iterator<Integer> it=relationship.keySet().iterator();
		while(it.hasNext())
		{
			int a=it.next();
			num2trustee.put(a, relationship.get(a).size());
		}
		HashMap<Integer, Integer> num2trunum=new HashMap<>();
		HashMap<Integer, Integer> trustor=new HashMap<>();
		it=relationship.keySet().iterator();
		while(it.hasNext())
		{
			int a=it.next();
			Iterator<Integer> it2=relationship.get(a).keySet().iterator();
			while(it2.hasNext())
			{
				int b=it2.next();
				if(trustor.containsKey(b))
				{
					trustor.put(b, trustor.get(b)+1);
				}
				else
				{
					trustor.put(b, 1);
				}
			}
		}
		Vector<Integer> trustors=new Vector<>();
		Vector<Integer> trustees=new Vector<>();
		
		for( int i=0;i<id2user.size();i++)
		{
			if(trustor.containsKey(i))
			{
				trustors.add(trustor.get(i));
			}
			else
			{
				trustors.add(0);
			}
			if(num2trustee.containsKey(i))
			{
				trustees.add(num2trustee.get(i));
			}
			else
			{
				trustees.add(0);
			}
		}
		BufferedWriter bw =new BufferedWriter(new FileWriter(new File("D:\\Datasets\\ciao\\CIAO_Data\\ciao_with_rating_timestamp_txt\\Ctreeor.txt")));
		for(int i=0;i<trustees.size();i++)
		{
			System.out.print(trustees.get(i)+" ");
			bw.write(trustees.get(i)+" ");
		}
		bw.newLine();
		System.out.println();
		for(int i=0;i<trustees.size();i++)
		{
			System.out.print(trustors.get(i)+" ");
			bw.write(trustors.get(i)+" ");
		}
		bw.newLine();
		bw.close();
	}
	public static void main(String[] args) throws Exception {
		SplitMatrix spmatrix = new SplitMatrix();
		String path = "D:\\Datasets\\Brightkite";
		String Rating = "Brightkite_totalCheckins.txt";
		String Relation = "Brightkite_edges.txt";
		
		String Ciaopath = "D:\\Datasets\\ciao\\CIAO_Data\\ciao_with_rating_timestamp_txt";
		String Ciaotrain = "CIAO_Train.txt";
		String Ciaotest = "CIAO_Test.txt";
		String Ciaorelations = "CIAO_Relations.txt";
		String Ciaoimplicitsupplementaryrelations="New_Cocitation_Implicit_1_CIAO_Relations.txt";
		

		String Epinionspath="D:\\Datasets\\Epinions\\epinions_with_timestamp_27_categories";
		String Epinionstrain="TJLEpinions_Train.txt";
		String Epinionstest="TJLEpinions_Test.txt";
		String Epinionsrelations="TJLEpinions_Relations.txt";
		String Epinionssupplementaryrelations="New_Coupling_Supplementary_1_TJLEpinions_Relations.txt";
		String Epinionsrandomsupplementaryrelations="Random_1_Supplementary_TJLEpinions_Relations.txt";
		String Epinionsimplicitsupplementaryrelations="Implicit_1_TJLEpinions_Relations.txt";
		
		spmatrix.ReadinMatrix(Ciaopath + "\\" +Ciaotrain, Ciaopath + "\\" + Ciaoimplicitsupplementaryrelations);
		
	//	spmatrix.ReadinMatrix(Epinionspath + "\\" +Epinionstrain, Epinionspath + "\\" + Epinionsimplicitsupplementaryrelations);
		//spmatrix.calculateTrustorandee();
		spmatrix.calculatesimilarity();
		//spmatrix.ReadinLBSMatrix(path + "\\" + Rating, path + "\\" + Relation);
		// spmatrix.Split(path + "\\Delicious_Train.txt", path +
		// "\\Delicious_Test.txt", path + "\\Delicious_Relations.txt");
//		spmatrix.SplitLBS(path + "\\Brightkite_Train2010.txt", path+ "\\Brightkite_Test2010.txt", path+ "\\Brightkite_Relations.txt");

		// spmatrix.ReadinMatrix(path + "\\" + Rating);
		// spmatrix.Split(path + "\\Lastfm_Train.txt", path +
		// "\\Lastfm_Test.txt");
	}
}
