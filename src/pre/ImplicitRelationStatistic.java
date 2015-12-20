package pre;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import utils.KVPair;

public class ImplicitRelationStatistic {
	public String tmp;
	public String[] tmpsplit;
	public int numberofuser;
	public HashMap<Integer, Double> TopK = new HashMap<>();
	public Map<Integer, Map<Integer, Double>> supplementarylink = new HashMap<Integer, Map<Integer, Double>>();

	public ImplicitRelationStatistic() {
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
	}

	public Map<Integer, Map<Integer, Double>> readMatrixData(String dataName)
			throws Exception {
		Map<Integer, Map<Integer, Double>> wholeData = new HashMap<Integer, Map<Integer, Double>>();
		BufferedReader fis = new BufferedReader(new FileReader(dataName));
		String line = null;
		int linenumber = 0;
		numberofuser = 0;
		while ((line = fis.readLine()) != null) {
			linenumber++;
			String items[] = line.split(" ");
			int user = Integer.parseInt(items[0]);
			int item = Integer.parseInt(items[1]);
		//	double rating = Double.parseDouble(items[2]);
			// System.out.println(user+" "+item );
			if (numberofuser <= user) {
				numberofuser = user + 1;
			}
			if (numberofuser <= item) {
				numberofuser = item + 1;
			}
			Map<Integer, Double> list = new HashMap<Integer, Double>();
			if (wholeData.containsKey(user)) {
				list = wholeData.get(user);
			}
			list.put(item, 1.0);
			wholeData.put(user, list);
		}
		fis.close();
		System.out.println(numberofuser);
		System.out.println(linenumber);
		return wholeData;
	}

	public void CalculateReciprocity(
			Map<Integer, Map<Integer, Double>> relations) {
		int totallink = 0;
		int reciprocity = 0;
		int us1 = 0;
		int us2 = 0;
		int[] reciprocity4 = new int[8];
		for (int i = 0; i < reciprocity4.length; i++) {
			reciprocity4[i] = 0;
		}
		Iterator<Integer> userit = relations.keySet().iterator();
		while (userit.hasNext()) {
			int user1 = userit.next();
			if (TopK.containsKey(user1)) {
				us1 = 1;
			}
			Iterator<Integer> userit2 = relations.get(user1).keySet()
					.iterator();
			while (userit2.hasNext()) {
				int user2 = userit2.next();
				if (TopK.containsKey(user2)) {
					us2 = 1;
				}
				if (relations.containsKey(user2)
						&& relations.get(user2).containsKey(user1)) {
					reciprocity++;
					totallink++;
					reciprocity4[transform(us1, us2, 0)]++;
				} else {
					totallink++;
					reciprocity4[transform(us1, us2, 1)]++;
				}
/*
				if (relations.containsKey(user2)
						&& !relations.get(user2).containsKey(user1)
						&& transform(us1, us2, 0) == 0) {
					supplementarylink.put(user2, user1);
				}*/
				us2 = 0;
			}
			us1 = 0;
		}
		System.out.println(reciprocity);
		System.out.println(totallink);
		System.out.println("Total Reciprocity: " + (double) reciprocity
				/ totallink);
		for (int i = 0; i < reciprocity4.length; i++) {
			System.out.println((double) reciprocity4[i] / totallink);
		}
	}

	public void CalculateBalanceTriangle(
			Map<Integer, Map<Integer, Double>> relations) {
		int us1 = 0, us2 = 0, us3 = 0;
		int Totaltriangle = 0;
		int numberoftriangle = 0;
		int numberofbalance = 0;
		int numberofunbalance=0;
		int numberofequal = 0;
		int[] balancekind = new int[8];
		int[] eqkind = new int[16];
		int[] indegree = new int[numberofuser];
		for (int i = 0; i < numberofuser; i++) {
			indegree[i] = 0;
		}
		for (int i = 0; i < balancekind.length; i++) {
			balancekind[i] = 0;
			if (i < eqkind.length) {
				eqkind[i] = 0;
			}
		}
		Iterator<Integer> userit = relations.keySet().iterator();
		while (userit.hasNext()) {
			int user1 = userit.next();
			Iterator<Integer> userit2 = relations.get(user1).keySet()
					.iterator();
			while (userit2.hasNext()) {
				int user2 = userit2.next();
				indegree[user2]++;
			}
		}
		List<KVPair> r = new ArrayList<KVPair>();
		for (int i = 0; i < numberofuser; i++) {
			r.add(new KVPair(i, indegree[i]));
		}
		Collections.sort(r);
		System.out.println(r.get(0).value);
		System.out.println(r.get(99).value);
		System.out.println(r.get(100).value);
		
		for (int i = 0; i < 100; i++) {
			TopK.put(r.get(i).id, r.get(i).value);
			// System.out.println(r.get(i).value);
		}
		userit = relations.keySet().iterator();
		while (userit.hasNext()) {
			int user1 = userit.next();
			if (TopK.containsKey(user1)) {
				us1 = 1;
			}
			Iterator<Integer> userit2 = relations.get(user1).keySet()
					.iterator();
			while (userit2.hasNext()) {
				int user2 = userit2.next();
				if (TopK.containsKey(user2)) {
					us2 = 1;
				}
				if (relations.containsKey(user2)
						&& !relations.get(user2).containsKey(user1)) {
					Iterator<Integer> it3 = relations.get(user2).keySet()
							.iterator();
					while (it3.hasNext()) {
						int user3 = it3.next();
						if (TopK.containsKey(user3)) {
							us3 = 1;
						}
						if (relations.get(user1).containsKey(user3)
								&& (!relations.containsKey(user3) || !relations
										.get(user3).containsKey(user1))
								&& (!relations.containsKey(user3) || !relations
										.get(user3).containsKey(user2))) {

							balancekind[transform(us1, us2, us3)]++;
							Totaltriangle++;
							numberofbalance++;
						}
						if (!relations.get(user1).containsKey(user3)
								&& (!relations.containsKey(user3) || !relations
										.get(user3).containsKey(user1))
								&& (!relations.containsKey(user3) || !relations
										.get(user3).containsKey(user2))
								&& Math.random() < 0.2
								&& transform(us1, us2, us3) == 4) {
							numberofunbalance++;
							if (supplementarylink.containsKey(user1)) {
								supplementarylink.get(user1).put(user3, 1.0);
							} else {
								HashMap<Integer, Double> tmphash = new HashMap<>();
								tmphash.put(user3, 1.0);
								supplementarylink.put(user1, tmphash);
							}

						} else if (!relations.get(user1).containsKey(user3)
								&& (!relations.containsKey(user3) || !relations
										.get(user3).containsKey(user1))
								&& (relations.containsKey(user3) && relations
										.get(user3).containsKey(user2))
								&& Math.random() < 0.2
								&& transform(us1, us2, us3) == 6) {
							numberofunbalance++;
							if (supplementarylink.containsKey(user1)) {
								supplementarylink.get(user1).put(user3, 1.0);
							} else {
								HashMap<Integer, Double> tmphash = new HashMap<>();
								tmphash.put(user3, 1.0);
								supplementarylink.put(user1, tmphash);
							}
						}
						us3 = 0;
					}
				} else if (relations.containsKey(user2)
						&& relations.get(user2).containsKey(user1)) {
					Iterator<Integer> it3 = relations.get(user2).keySet()
							.iterator();
					while (it3.hasNext()) {
						int user3 = it3.next();
						if (TopK.containsKey(user3)) {
							us3 = 1;
						}
						if (relations.get(user1).containsKey(user3)
								&& (!relations.containsKey(user3) || !relations
										.get(user3).containsKey(user1))
								&& (!relations.containsKey(user3) || !relations
										.get(user3).containsKey(user2))) {

							balancekind[transform(us1, us2, us3)]++;
							Totaltriangle++;
							numberofbalance++;
						}
						if (!relations.get(user1).containsKey(user3)
								&& (!relations.containsKey(user3) || !relations
										.get(user3).containsKey(user1))
								&& (!relations.containsKey(user3) || !relations
										.get(user3).containsKey(user2))
								&& Math.random() < 0.2
								&& transform(us1, us2, us3) == 6) {
							numberofunbalance++;
							if (supplementarylink.containsKey(user1)) {
								supplementarylink.get(user1).put(user3, 1.0);
							} else {
								HashMap<Integer, Double> tmphash = new HashMap<>();
								tmphash.put(user3, 1.0);
								supplementarylink.put(user1, tmphash);
							}
						} else if (!relations.get(user1).containsKey(user3)
								&& (!relations.containsKey(user3) || !relations
										.get(user3).containsKey(user1))
								&& (relations.containsKey(user3) && relations
										.get(user3).containsKey(user2))
								&& Math.random() < 0.2
								&& transform(us1, us2, us3) == 6) {
							numberofunbalance++;
							if (supplementarylink.containsKey(user1)) {
								supplementarylink.get(user1).put(user3, 1.0);
							} else {
								HashMap<Integer, Double> tmphash = new HashMap<>();
								tmphash.put(user3, 1.0);
								supplementarylink.put(user1, tmphash);
							}
						}

						us3 = 0;
					}
				}
				us2 = 0;
			}
			us1 = 0;
		}
		System.out.println("Balance Status: " + numberofbalance);
		System.out.println("Unbalance: "+	numberofunbalance);
		System.out.println((double)numberofbalance/(numberofbalance+numberofunbalance));
		int totalstatus = 0;
		int totaleq = 0;
		for (int i = 0; i < balancekind.length; i++) {
			totalstatus += balancekind[i];
		}
		System.out.println("Balance 8 Status " + totalstatus + " "
				+ numberofbalance);
		for (int i = 0; i < balancekind.length; i++) {
			System.out.println((double) balancekind[i] / totalstatus);
		}
	}

	public int transform(int u1, int u2, int u3) {

		int value = u1 + u2 * 2 + u3 * 4;
		return value;
	}

	public int transform(int u1, int u2, int u3, int u4) {

		int value = u1 + u2 * 2 + u3 * 4 + u4 * 8;
		return value;
	}

	public void CalculateCoupling(Map<Integer, Map<Integer, Double>> relations) {
		int totalcoupling = 0;
		int[] coupling = new int[16];
		int us1 = 0, us2 = 0, us3 = 0, us4 = 0;
		for (int i = 0; i < coupling.length; i++) {
			coupling[i] = 0;
		}
		Iterator<Integer> it1 = relations.keySet().iterator();
		while (it1.hasNext()) {
			int user1 = it1.next();
			if (TopK.containsKey(user1)) {
				us1 = 1;
			}
			Iterator<Integer> it2 = relations.get(user1).keySet().iterator();
			while (it2.hasNext()) {
				int user2 = it2.next();
				if (!relations.containsKey(user2)
						|| !relations.get(user2).containsKey(user1)) {
					if (TopK.containsKey(user2)) {
						us2 = 1;
					}
					for (int user3 = 0; user3 < numberofuser && user3 != user1
							&& user3 != user2; user3++) {
						if (relations.containsKey(user3)
								&& !relations.get(user3).containsKey(user1)
								&& !relations.get(user1).containsKey(user3)
								&& relations.get(user3).containsKey(user2)
								&& (!relations.containsKey(user2) || !relations
										.get(user2).containsKey(user3))) {
							if (TopK.containsKey(user3)) {
								us3 = 1;
							}
							for (int user4 = 0; user4 < numberofuser
									&& user4 != user1 && user4 != user2
									&& user4 != user3; user4++) {
								if (TopK.containsKey(user4)) {
									us4 = 1;
								}
								if (relations.containsKey(user4)
										&& relations.get(user4).containsKey(
												user1)
										&& relations.get(user4).containsKey(
												user3)
										&& !relations.get(user1).containsKey(
												user4)
										&& !relations.get(user3).containsKey(
												user4)
										&& (!relations.containsKey(user2) || !relations
												.get(user2).containsKey(user4))
										&& (!relations.get(user4).containsKey(
												user2))) {
									coupling[transform(us1, us2, us3, us4)]++;
									totalcoupling++;
									if (relations.containsKey(user4)
											&& relations.get(user4)
													.containsKey(user1)
											&& !relations.get(user4)
													.containsKey(user3)
											&& !relations.get(user1)
													.containsKey(user4)
											&& !relations.get(user3)
													.containsKey(user4)
											&& (!relations.containsKey(user2) || !relations
													.get(user2).containsKey(
															user4))
											&& (!relations.get(user4)
													.containsKey(user2))
											&& Math.random() < 0.2
											&& transform(us1, us2, us3, us4) == 2) {
										if (supplementarylink
												.containsKey(user4)&&!supplementarylink.get(user4).containsKey(user3)) {
											//supplementarylink.get(user4).put(
											//		user3, 2.0);
										} else {
											HashMap<Integer, Double> tmphash = new HashMap<>();
											tmphash.put(user3, 2.0);
											//supplementarylink.put(user4,
											//		tmphash);
										}
									}
								}

								us4 = 0;
							}

							us3 = 0;
						}

					}
					us2 = 0;
				}

			}
			us1 = 0;
		}
		System.out.println("Number of Coupling: " + totalcoupling);
		int sum = 0;
		for (int i = 0; i < coupling.length; i++) {
			sum += coupling[i];
			System.out.println((double) coupling[i] / totalcoupling);
		}
		System.out.println("Number of Coupling: " + sum);
	}

	public void CalculateCocitation(Map<Integer, Map<Integer, Double>> relations) {
		int totalcoupling = 0;
		int[] coupling = new int[16];
		int us1 = 0, us2 = 0, us3 = 0, us4 = 0;
		for (int i = 0; i < coupling.length; i++) {
			coupling[i] = 0;
		}
		Iterator<Integer> it1 = relations.keySet().iterator();
		while (it1.hasNext()) {
			int user1 = it1.next();
			if (TopK.containsKey(user1)) {
				us1 = 1;
			}
			for (int user4 = 0; user4 < numberofuser; user4++) {
				if (!relations.get(user1).containsKey(user4)
						&& relations.containsKey(user4)
						&& !relations.get(user4).containsKey(user1)) {
					if (TopK.containsKey(user4)) {
						us4 = 1;
					}
					Iterator<Integer> it2 = relations.get(user1).keySet()
							.iterator();
					while (it2.hasNext()) {
						int user2 = it2.next();
						if (relations.get(user4).containsKey(user2)
								&& (!relations.containsKey(user2) || !relations
										.get(user2).containsKey(user4))
								&& (!relations.containsKey(user2) || !relations
										.get(user2).containsKey(user1))) {
							if (TopK.containsKey(user2)) {
								us2 = 1;
							}
							Iterator<Integer> it3 = relations.get(user1)
									.keySet().iterator();
							while (it3.hasNext()) {
								int user3 = it3.next();
								if ((!relations.containsKey(user2) || !relations
										.get(user2).containsKey(user3))
										&& (!relations.containsKey(user3) || !relations
												.get(user3).containsKey(user4))
										&& (!relations.containsKey(user3) || !relations
												.get(user3).containsKey(user1))
										&& (!relations.containsKey(user3) || !relations
												.get(user3).containsKey(user2))) {
									if (TopK.containsKey(user3)) {
										us3 = 1;
									}
									if (relations.get(user4).containsKey(user3)) {
										totalcoupling++;
										coupling[transform(us1, us2, us3, us4)]++;
									}
									if (!relations.get(user4)
											.containsKey(user3)
											&& Math.random() < 0.2
											&& (transform(us1, us2, us3, us4) == 6)) {
										if (supplementarylink
												.containsKey(user4)&&!supplementarylink.get(user4).containsKey(user3)) {
										//	supplementarylink.get(user4).put(
										//			user3, 3.0);
										} else {
											HashMap<Integer, Double> tmphash = new HashMap<>();
											tmphash.put(user3, 3.0);
										//	supplementarylink.put(user4,
										//			tmphash);
										}
									}
								}
								us3 = 0;
							}
							us2 = 0;
						}

					}
					us4 = 0;
				}
			}
			us1 = 0;
		}
		System.out.println("Number of Co-Citation: " + totalcoupling);
		int sum = 0;
		for (int i = 0; i < coupling.length; i++) {
			sum += coupling[i];
			System.out.println((double) coupling[i] / totalcoupling);
		}
		System.out.println("Number of Co-Citation: " + sum);
	}

	public int CalculateTotalLink(Map<Integer, Map<Integer, Double>> relations) {
		int totallink = 0;
		Iterator<Integer> it1 = relations.keySet().iterator();
		while (it1.hasNext()) {
			totallink += relations.get(it1.next()).size();
		}
		return totallink;
	}

	public void SupplementaryGraph(
			Map<Integer, Map<Integer, Double>> relations,
			Map<Integer, Map<Integer, Double>> sup) {
		Iterator<Integer> it1 = sup.keySet().iterator();
		while (it1.hasNext()) {
			int u1 = it1.next();
			Iterator<Integer> it2 = sup.get(u1).keySet().iterator();
			while (it2.hasNext()) {
				int u2 = it2.next();
				if (relations.containsKey(u1)) {
					relations.get(u1).put(u2, 1.0);
				} else {
					Map<Integer, Double> tmphash = new HashMap<Integer, Double>();
					tmphash.put(u2, 1.0);
					relations.put(u1, tmphash);
				}

			}
		}
	}

	public void SaveSupplementaryGraph(String path,
			Map<Integer, Map<Integer, Double>> relations) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					new File(path)));

			Iterator<Integer> it1 = relations.keySet().iterator();
			while (it1.hasNext()) {
				int u1 = it1.next();
				Iterator<Integer> it2 = relations.get(u1).keySet().iterator();
				while (it2.hasNext()) {
					int u2= it2.next();
					bw.write(u1 + " " +u2 + " "+relations.get(u1).get(u2));
					bw.newLine();
				}
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void SaveRandomSupplementaryGraph(String path,
			Map<Integer, Map<Integer, Double>> relations, int addedLinks) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					new File(path)));
			int cur = 0;
			int totallinks = 0;
			Iterator<Integer> it1 = relations.keySet().iterator();
			while (it1.hasNext()) {
				int u1 = it1.next();
				Iterator<Integer> it2 = relations.get(u1).keySet().iterator();
				while (it2.hasNext()) {
					int u2 = it2.next();
					totallinks++;
					bw.write(u1 + " " + u2 + " 1.0");
					bw.newLine();
				}
				for (int i = 0; i < numberofuser && i != u1; i++) {
					if (!relations.get(u1).containsKey(i)
							&& Math.random() < 0.3 && cur < addedLinks) {
						totallinks++;
						cur++;
						bw.write(u1 + " " + i + " 1.0");
						bw.newLine();
					}

				}
			}
			bw.close();
			System.out.println(cur + " " + addedLinks);
			System.out.println("Random Supplementary: " + totallinks);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		String Ciaopath = "D:\\Datasets\\ciao\\CIAO_Data\\ciao_with_rating_timestamp_txt";
		String Ciaotrain = "CIAO_Train.txt";
		String Ciaotest = "CIAO_Test.txt";
		String Ciaorelations = "CIAO_Relations.txt";

		String TJLEpinionspath = "D:\\Datasets\\Epinions\\epinions_with_timestamp_27_categories";
		String TJLEpinionsRating = "rating 27 categories.dat";
		String TJLEpinionsRelation = "TJLEpinions_Relations.txt";

		String Lastfmpath = "D:\\Datasets\\hetrec2011-lastfm-2k\\";
		String Lastfmtrain = "Lastfm_Train.txt";
		String Lastfmrelations="Lastfm_Relations.txt";
		String Lastfmtest = "Lastfm_Test.txt";
		
		String Deliciouspath = "D:\\Datasets\\hetrec2011-delicious-2k\\";
		String Delicioustrain = "Delicious_Train.txt";
		String Deliciousrelations="Delicious_Relations.txt";
		String Delicioustest = "Delicious_Test.txt";
		
		String Flixsterpath="D:\\Datasets\\Flixster\\flixster\\";
		String FlixsterRelations="links.txt";
	
		String Brightkitepath = "D:\\Datasets\\Brightkite\\";
		String BrightkiteRelation = "Brightkite_edges.txt";
		
		ImplicitRelationStatistic irs = new ImplicitRelationStatistic();
		Map<Integer, Map<Integer, Double>> relations = irs
				.readMatrixData(TJLEpinionspath + "\\" + TJLEpinionsRelation);
		System.out.println("From NOW, Total Link is: "
				+ irs.CalculateTotalLink(relations));
		int preLinks = irs.CalculateTotalLink(relations);
		irs.CalculateBalanceTriangle(relations);
		// System.out.println("From NOW, Total Link is: "+irs.CalculateTotalLink(relations));
		irs.CalculateReciprocity(relations);
		// System.out.println("From NOW, Total Link is: "+irs.CalculateTotalLink(relations));
		irs.CalculateCocitation(relations);
		// System.out.println("From NOW, Total Link is: "+irs.CalculateTotalLink(relations));
		irs.CalculateCoupling(relations);
		irs.SupplementaryGraph(relations, irs.supplementarylink);
	//	irs.SaveSupplementaryGraph(Ciaopath + "\\New_Tri_Supplementary_1_"
	//			+ Ciaorelations, relations);
	//	irs.SaveSupplementaryGraph(Ciaopath + "\\New_Tri_Implicit_1_"
	//			+ Ciaorelations, irs.supplementarylink);
		System.out.println("From NOW, Total Link is: "
				+ irs.CalculateTotalLink(relations));
		int postLinks = irs.CalculateTotalLink(relations);
		System.out.println("Link Distance:" + (postLinks - preLinks));
		System.out.println(irs.CalculateTotalLink(irs.supplementarylink));
		// irs.SaveRandomSupplementaryGraph(TJLEpinionspath+"\\New_Random_1_Supplementary_"+TJLEpinionsRelation,
		// relations, irs.CalculateTotalLink(irs.supplementarylink));
	}
}
