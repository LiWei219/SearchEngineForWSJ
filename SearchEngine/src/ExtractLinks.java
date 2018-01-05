/**
 * Created by WeiLi on 11/2/17.
 */
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExtractLinks {
    public static void main(String[] args) throws IOException{
        String csv = "/Users/WeiLi/Downloads/solr-7.1.0/WSJ/WSJ map.csv";
        String dirPath = "/Users/WeiLi/Downloads/solr-7.1.0/WSJ/WSJ/";
        BufferedReader br = new BufferedReader(new FileReader(csv));
        Map<String, String> id2Url = new HashMap<>();
        Map<String, String> url2Id = new HashMap<>();
        String line;
        while((line = br.readLine())!=null){
            String[] strs = line.split(",");
            id2Url.put(strs[0], strs[1]);
            url2Id.put(strs[1], strs[0]);
        }
        System.out.println(url2Id.size() + " " + id2Url.size());
        br.close();
        File dir = new File(dirPath);
        Set<String> edges = new HashSet<String>();
        System.out.println(dir.listFiles().length);

        for(File file : dir.listFiles()){
            if(file.getName().equals(".DS_Store"))
                continue;
            Document doc = Jsoup.parse(file, "UTF-8", id2Url.get(file.getName()));
            Elements links = doc.select("a[href]"); //a with jref
            for(Element link : links){
                String url = link.attr("abs:href").trim();
                if(url2Id.containsKey(url)){
                    edges.add(file.getName() + " " + url2Id.get(url));  //edges: "id1 id2"
                }
            }
        }

        try{
            PrintWriter writer = new PrintWriter("edgesList.txt", "UTF-8");
            for(String s : edges){
                writer.println(s);
            }
            writer.flush();
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
