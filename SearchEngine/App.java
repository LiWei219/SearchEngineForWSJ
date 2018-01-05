package csci572.hw5;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import java.io.*;

public class App 
{
    public static void main( String[] args ) throws IOException{
    	String dirPath = "/Users/WeiLi/Downloads/solr-7.1.0/WSJ/WSJ/";
    	String dic = "big.txt";
    	File dir = new File(dirPath);
    	FileOutputStream dicOutput = new FileOutputStream(dic);
    	OutputStreamWriter dicWriter = new OutputStreamWriter(dicOutput, "UTF-8");
    	
    	Tika tika = new Tika();
    	String outputPath = "/Users/WeiLi/Downloads/solr-7.1.0/WSJ/plaintext/";
    	int i = 0;
    	for(File file : dir.listFiles()) {
    		System.out.println(++i + "finished.");
    		String filename = file.getName();
    		if(filename.equals(".DS_Store"))
    			continue;
    		String text = "";
    		try {
    			text = tika.parseToString(file);
    		}catch(TikaException te) {
    			te.printStackTrace();
    		}
    		
    		String[] tokens = text.trim().split("\\s+");
    		System.out.println(tokens.length);
    		for(String token : tokens) {
    			dicWriter.write(token + " ");
    		}
    		filename = outputPath + filename.substring(0, filename.lastIndexOf(".html")) + ".txt";
    		FileOutputStream fileOut = new FileOutputStream(filename);
    		OutputStreamWriter writer = new OutputStreamWriter(fileOut, "UTF-8");
    		writer.write(text);
    		writer.flush();
    		writer.close();
    	}
    	
    	dicWriter.flush();
    	dicWriter.close();  
    	System.out.println("Finish!");
    }
}
