package pre;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class extractJson {
	public static String tmpString;
	public static String[] tmpsStrings;

	public extractJson() {
		tmpString=null;
		tmpsStrings=null;
	}

	public static void ParseJson(String path,String file) throws Exception {
		BufferedReader bReader =new BufferedReader(new FileReader(new File(path+file)));
		while((tmpString=bReader.readLine())!=null)
		{
			System.out.println(tmpString);
		}
		bReader.close();
	}

	public static void main(String[] args) throws Exception {
		extractJson eJson = new extractJson();
		String filepathString = "E:\\Datasets\\Amazon\\";
		String metafileString = "meta_Beauty.json.gz";
		String reviewfileString = "";
		eJson.ParseJson(filepathString, metafileString);
	}
}
