package driver;

import java.util.Map;

import recmodel.AbstractMFModel;
import utils.DataManager;

public class MFExcuter {

	public static void runRecModel(String filename, String train, String test,
			int numD, String modelName) throws Exception {
		DataManager dmanager = new DataManager();
		AbstractMFModel mfModel = (AbstractMFModel) Class.forName(
				"recmodel." + modelName).newInstance();
		double spmae=0;
		double mae=0;
		double sprmse=0;
		double rmse=0;
		
		//int spuser=1834;//1302;//807;//408;//209;
		for(int numModel=0;numModel<1;numModel++)
		{
			System.out.println(System.currentTimeMillis());
			
			Map<Integer, Map<Integer, Double>> trainData = dmanager
					.readMatrixData(filename + "\\" + train);
			Map<Integer, Map<Integer, Double>> testData = dmanager
					.readTestMatrixData(filename + "\\" + test);
			System.out.println(System.currentTimeMillis());
			
			mfModel.init(trainData, dmanager.numberofuser, dmanager.numberofitem,
					numD);
			System.out.println(filename + " " + modelName + " " + numD);
			mfModel.buildModel();
			System.out.println("Training Complete! ");
			System.out.println("RMSE " + mfModel.getError(testData));
			System.out.println("MAE " + mfModel.getMAE(testData));
		/*	double[] precision=new double[6];
			double[] recall=new double[6];
			precision[0]=mfModel.getPrecision(testData, trainData, 5);
			precision[1]=mfModel.getPrecision(testData, trainData, 10);
			precision[2]=mfModel.getPrecision(testData, trainData, 15);
			precision[3]=mfModel.getPrecision(testData, trainData, 20);
			precision[4]=mfModel.getPrecision(testData, trainData, 50);
			precision[5]=mfModel.getPrecision(testData, trainData, 100);
			recall[0]=mfModel.getRecall(testData, trainData, 5);
			recall[1]=mfModel.getRecall(testData, trainData, 10);
			recall[2]=mfModel.getRecall(testData, trainData, 15);
			recall[3]=mfModel.getRecall(testData, trainData, 20);
			recall[4]=mfModel.getRecall(testData, trainData, 50);
			recall[5]=mfModel.getRecall(testData, trainData, 100);
	
			System.out.println("Precision 5 " + precision[0]);
			System.out.println("Precision 10 " + precision[1]);
			System.out.println("Precision 15 " + precision[2]);
			System.out.println("Precision 20 " + precision[3]);
			System.out.println("Precision 50 " + precision[4]);
			System.out.println("Precision 100 " + precision[5]);
			System.out.println("Recall 5 "+ recall[0]);
			System.out.println("Recall 10 "+ recall[1]);
			System.out.println("Recall 15 "+ recall[2]);
			System.out.println("Recall 20 "+ recall[3]);
			System.out.println("Recall 50 "+ recall[4]);
			System.out.println("Recall 100 "+ recall[5]);
			for(int f=0;f<precision.length;f++)
			{
				System.out.println("F-Measure: "+(2*precision[f]*recall[f])/(precision[f]+recall[f]));
			}
			
*/		//System.out.println("MAP " + mfModel.getMAP(testData, trainData));
		//spmae+=mfModel.getSpecificMAE(testData, spuser);
	//	mae+=mfModel.getMAE(testData);
	//	//sprmse+=mfModel.getSpecificError(testData, spuser);
	//	rmse+=mfModel.getError(testData);
		}
		/*System.out.println("Specific user: "+spuser+" MAE " + spmae/20);
		System.out.println("Specific user: "+spuser+" RMSE " + sprmse/20);
		System.out.println("MAE " +mae/20);
		System.out.println("RMSE " +rmse/20);*/
				// mfModel.writePrediction(fileName+"_"+numD+"_"+i+".pre",testData);
		// Map<Integer, Map<Integer, Double>> trainData =
		// DataManager.readMatrixData(fileName+"_"+i+".train");
		// System.out.println("MAP "+mfModel.getMAP(trainData,testData));
		// fw.write("RMSE "+mfModel.getError(testData)+"\n");
		// fw.write("MAE "+mfModel.getMAE(testData)+"\n");
		// fw.flush();

		// fw.close();
	}
	public static void runSoRecModel(String filename, String train, String test,String relations,
			int numD, String modelName) throws Exception {
		DataManager dmanager = new DataManager();
		AbstractMFModel mfModel = (AbstractMFModel) Class.forName(
				"sorecmf." + modelName).newInstance();

		
		Map<Integer, Map<Integer, Double>> trainData = dmanager
				.readMatrixData(filename + "\\" + train);
		Map<Integer, Map<Integer, Double>> testData = dmanager
				.readTestMatrixData(filename + "\\" + test);
		Map<Integer, Map<Integer, Double>> relationData = dmanager
				.readRelationMatrixData(filename + "\\" + relations);
		mfModel.init(trainData,relationData, dmanager.numberofuser, dmanager.numberofitem,
				numD);
		System.out.println(filename + " "+relations+" " + modelName + " " + numD);
		mfModel.buildModel();
		System.out.println("Training Complete! ");
		System.out.println("RMSE " + mfModel.getError(testData));
		System.out.println("MAE " + mfModel.getMAE(testData));
		/*System.out.println("Precision 5 " + mfModel.getPrecision(testData, trainData, 5));
		System.out.println("Precision 10 " + mfModel.getPrecision(testData, trainData, 10));
		System.out.println("Precision 15 " + mfModel.getPrecision(testData, trainData, 15));
		System.out.println("Precision 20 " + mfModel.getPrecision(testData, trainData, 20));
		System.out.println("Precision 50 " + mfModel.getPrecision(testData, trainData, 50));
		System.out.println("Precision 100 " + mfModel.getPrecision(testData, trainData, 100));
		System.out.println("Recall 5 "+ mfModel.getRecall(testData, trainData, 5));
		System.out.println("Recall 10 "+ mfModel.getRecall(testData, trainData, 10));
		System.out.println("Recall 15 "+ mfModel.getRecall(testData, trainData, 15));
		System.out.println("Recall 20 "+ mfModel.getRecall(testData, trainData, 20));
		System.out.println("Recall 50 "+ mfModel.getRecall(testData, trainData, 50));
		System.out.println("Recall 100 "+ mfModel.getRecall(testData, trainData,100));*/
		//System.out.println("MAP " + mfModel.getMAP(testData, trainData));
		// mfModel.writePrediction(fileName+"_"+numD+"_"+i+".pre",testData);
		// Map<Integer, Map<Integer, Double>> trainData =
		// DataManager.readMatrixData(fileName+"_"+i+".train");
		// System.out.println("MAP "+mfModel.getMAP(trainData,testData));
		// fw.write("RMSE "+mfModel.getError(testData)+"\n");
		// fw.write("MAE "+mfModel.getMAE(testData)+"\n");
		// fw.flush();

		// fw.close();
	}
	public static void runSoContextRecModel(String filename, String train, String test,String relations,
			int numD, String modelName) throws Exception {
		DataManager dmanager = new DataManager();
		AbstractMFModel mfModel = (AbstractMFModel) Class.forName(
				"socontextrec." + modelName).newInstance();

		Map<Integer, Map<Integer, Double>> relationData = dmanager
				.readMatrixData(filename + "\\" + relations);
		Map<Integer, Map<Integer, Double>> trainData = dmanager
				.readMatrixData(filename + "\\" + train);
		Map<Integer,Integer> CategoryData = dmanager
				.readCategoryData(filename + "\\" + train);
		Map<Integer, Map<Integer, Double>> testData = dmanager
				.readTestMatrixData(filename + "\\" + test);
		numD=dmanager.numberofcategory;
		mfModel.init(trainData,relationData,CategoryData, dmanager.numberofuser, dmanager.numberofitem,
				numD, dmanager.numberofcategory);
		System.out.println(filename + " " + modelName + " " + numD);
		mfModel.buildModel();
		System.out.println("Training Complete! ");
		System.out.println("RMSE " + mfModel.getError(trainData));
		System.out.println("MAE " + mfModel.getMAE(testData));
	/*	System.out.println("Precision 5 " + mfModel.getPrecision(testData, trainData, 5));
		System.out.println("Precision 10 " + mfModel.getPrecision(testData, trainData, 10));
		System.out.println("Precision 15 " + mfModel.getPrecision(testData, trainData, 15));
		System.out.println("Precision 20 " + mfModel.getPrecision(testData, trainData, 20));
		System.out.println("Precision 50 " + mfModel.getPrecision(testData, trainData, 50));
		System.out.println("Precision 100 " + mfModel.getPrecision(testData, trainData, 100));
		System.out.println("Recall 5 "+ mfModel.getRecall(testData, trainData, 5));
		System.out.println("Recall 10 "+ mfModel.getRecall(testData, trainData, 10));
		System.out.println("Recall 15 "+ mfModel.getRecall(testData, trainData, 15));
		System.out.println("Recall 20 "+ mfModel.getRecall(testData, trainData, 20));
		System.out.println("Recall 50 "+ mfModel.getRecall(testData, trainData, 50));
		System.out.println("Recall 100 "+ mfModel.getRecall(testData, trainData,100));
		System.out.println("MAP " + mfModel.getMAP(testData, trainData));
	*/	// mfModel.writePrediction(fileName+"_"+numD+"_"+i+".pre",testData);
		// Map<Integer, Map<Integer, Double>> trainData =
		// DataManager.readMatrixData(fileName+"_"+i+".train");
		// System.out.println("MAP "+mfModel.getMAP(trainData,testData));
		// fw.write("RMSE "+mfModel.getError(testData)+"\n");
		// fw.write("MAE "+mfModel.getMAE(testData)+"\n");
		// fw.flush();

		// fw.close();
	}
	/*
	 * public static void runParameters() throws Exception{ String fileName =
	 * "../data/movielens/100k/u"; //Different depth for(int d=0;d<=4;d++){
	 * FileWriter fw = new FileWriter(d+"_depth.txt",true); for(int
	 * i=1;i<=5;i++){ DTMF model = new DTMF(); model.depth = d; model.numTrees =
	 * 5; model.avg = true; model.init(fileName+i+".base.txt", 943, 1682, 1);
	 * model.buildModel(); Map<Integer, Map<Integer, Double>> testData =
	 * DataManager.readMatrixData(fileName+i+".test.txt");
	 * fw.write("RMSE "+model.getError(testData)+"\n");
	 * fw.write("MAE "+model.getMAE(testData)+"\n"); fw.flush(); } fw.close(); }
	 * //Different trees for(int d=20;d<=20;d+=5){ if(d==0) d=1; FileWriter fw =
	 * new FileWriter(d+"_trees.txt",true); for(int i=1;i<=5;i++){ DTMF model =
	 * new DTMF(); DataManager dmanage=new DataManager(); model.avg = true;
	 * model.depth = 2; model.numTrees = d; model.init(fileName+i+".base.txt",
	 * 943, 1682, 1); model.buildModel(); Map<Integer, Map<Integer, Double>>
	 * testData = dmanage.readMatrixData(fileName+i+".test.txt");
	 * fw.write("RMSE "+model.getError(testData)+"\n");
	 * fw.write("MAE "+model.getMAE(testData)+"\n"); fw.flush(); } fw.close();
	 * if(d==1) d=0; } }
	 */
	public static void main(String[] args) throws Exception {
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		long startTime = System.currentTimeMillis();
		String Lastfmpath = "D:\\Tsinghua\\Paper\\发表筹备\\Soc+Short Text\\Matlab Code\\Lastfm\\Lastfm v1 sigmoid\\";
		String Lastfmtrain = "Train_80.txt";
		String Lastfmrelations="F.txt";
		String Lastfmtest = "Test_20.txt";
		
		String Deliciouspath = "D:\\Tsinghua\\Paper\\发表筹备\\Soc+Short Text\\Matlab Code\\Delicious\\Delicious v1 sigmoid\\";
		String Delicioustrain = "Train.txt";
		String Deliciousrelations="F.txt";
		String Delicioustest = "Test.txt";
		
		String doubanpath="D:\\Datasets\\Douban_king";
		String doubanrating="uir.index";
		String doubantr="Train_90.txt";
		String doubante="Test_10.txt";
		String doubansocial="social.index";
		
		
		String Ciaopath="E:\\Datasets\\ciao\\CIAO_Data\\ciao_with_rating_timestamp_txt";
		String Ciaotrain="CIAO_Train.txt";
		String Ciaotest="CIAO_Test.txt";
		String Ciaorelations="CIAO_Relations.txt";
		String Ciaosupplementaryrelations="Supplementary_1_CIAO_Relations.txt";
		String Ciaorandomsupplementaryrelations="Random_1_Supplementary_CIAO_Relations.txt";
		String Ciaoimplicitsupplementaryrelations="New_Cocitation_Supplementary_1_CIAO_Relations.txt";
		
		
		String Epinionspath="E:\\Datasets\\Epinions\\epinions_with_timestamp_27_categories";
		String Epinionstrain="TJLEpinions_Train.txt";
		String Epinionstest="TJLEpinions_Test.txt";
		String Epinionsrelations="TJLEpinions_Relations.txt";
		String Epinionssupplementaryrelations="Supplementary_1_TJLEpinions_Relations.txt";
		String Epinionsrandomsupplementaryrelations="Random_1_Supplementary_TJLEpinions_Relations.txt";
		String Epinionsimplicitsupplementaryrelations="New_Cocitation_Implicit_1_TJLEpinions_Relations.txt";
		
		String Slashdotpath="D:\\Datasets\\Slashdot\\slashdot_content\\slashdot2009-2012\\test";
		String SlashdotTrain = "Train_70.txt";
		String SlashdotTest="Test_30.txt";
		String SlashdotRelatin="F.txt";
		
		String brightkitePath="D:\\Datasets\\Brightkite";
		String brightkiteTrain="Brightkite_Train2010.txt";		
		String brightkiteTest="Brightkite_Test2010.txt";
		String brightkiteRelation="Brightkite_Relations.txt";
		
		String p="D:\\CUHK\\Lectures\\Big_Data\\Project";
		String tr="Train_90.txt";
		String te="Test_10.txt";
		String commonrated_re="sampleCommonUser.txt";
		
		String BApathString="E:\\Datasets\\JulianSharing\\Beeradvocate.txt";
		
		String RBpathString="E:\\Datasets\\JulianSharing\\RateBeer.txt";
		//String BAtrString="";
		//	
	//for(int k=5;k<=80;)
	//{
		runRecModel(RBpathString, tr, te, 10, "MF");
		//runSoRecModel(Epinionspath, Epinionstrain, Epinionstest,Epinionsrelations,10, "SoRec");	
	//	k+=5;C
	//}
		//runSoContextRecModel(Ciaopath, Ciaotrain, Ciaotest, Ciaorelations, 6, "SoCategoryRec");	
		long endTime = System.currentTimeMillis();
		System.out.println("Time： " + (endTime - startTime) + "ms");
	}
}
