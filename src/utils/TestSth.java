package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class TestSth {

	public String tmp;
	public String[] tmpsplit;
	public TestSth()
	{
		
	}
	public void TestSame(String filename) throws Exception
	{
		HashMap<Integer, HashMap<Integer, Integer>> user2rating=new HashMap<>();
		BufferedReader br =new BufferedReader(new FileReader(new File(filename)));
		while((tmp=br.readLine())!=null)
		{
			tmpsplit=tmp.split(",");
			int user=Integer.parseInt(tmpsplit[0]);
			int item=Integer.parseInt(tmpsplit[1]);

			if (user2rating.containsKey(user)) {
				if(user2rating.get(user).containsKey(item))
				{
					System.out.println("Same occur");
				}
				user2rating.get(user).put(item,
						1);
			} else {
				HashMap<Integer, Integer> tmphash = new HashMap<Integer, Integer>();
				tmphash.put(item,
						1);
				user2rating.put(user, tmphash);
				tmphash.clear();
			}
			
		}
		br.close();
	}
	public static void main(String[] args) throws Exception
	{
		String TJLEpinionspath="D:\\Datasets\\Epinions\\epinions_with_timestamp_27_categories";
		String TJLEpinionsTrust="trust.txt";
		TestSth ts=new TestSth();
		ts.TestSame(TJLEpinionspath+"\\"+TJLEpinionsTrust);
		
	}
}
