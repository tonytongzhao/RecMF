package pre;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

public class TopicRepresentation {
	public String tmp;
	public String[] tmpsplit;
	public HashMap<Integer, String> id2word;
	public TopicRepresentation()
	{
		id2word=new HashMap<>();
	}
	public void ReadinWordMap(String wordmapfile) throws Exception
	{
		BufferedReader br =new BufferedReader(new FileReader(new File(wordmapfile)));
		while((tmp=br.readLine())!=null)
		{
			tmpsplit=tmp.split(";");
			id2word.put(Integer.parseInt(tmpsplit[0]), tmpsplit[1]);
		}
		br.close();
	}
	public void writeTopics(File ff, String output) throws Exception
	{
		BufferedWriter bw =new BufferedWriter(new FileWriter(new File(output),true));
		BufferedReader br =new BufferedReader(new FileReader(ff));
		bw.newLine();
		bw.write(ff.getName());
		bw.newLine();
		while((tmp=br.readLine())!=null)
		{
			bw.write(id2word.get(Integer.parseInt(tmp.split(" ")[0])));
			bw.newLine();
		}
		bw.close();
		br.close();
	}
	public static void main(String[] args) throws Exception
	{
		String filename="D:\\Tsinghua\\Paper\\发表筹备\\Soc+Short Text\\Matlab Code\\Lastfm\\Lastfm Result";
		File f=new File(filename);
		String wordmapfile="D:\\Datasets\\hetrec2011-lastfm-2k\\LastfmCTR_wordmap.txt";
		String output="D:\\Tsinghua\\Paper\\发表筹备\\Soc+Short Text\\Matlab Code\\Lastfm\\Lastfm Result\\TopicRepresentation.txt";
		TopicRepresentation tr=new TopicRepresentation();
		tr.ReadinWordMap(wordmapfile);
		for(File ff : f.listFiles())
		{
			tr.writeTopics(ff, output);
		}
	}
}
