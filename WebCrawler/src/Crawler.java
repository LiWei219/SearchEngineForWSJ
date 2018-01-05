import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class Crawler extends WebCrawler {
	//for fetched urls
	static int successful_urls = 0;
	static int failed_urls = 0;
	static int aborted_urls = 0;
	static HashSet<String> fetched= new HashSet<String>();  //url-status code  hash.Keysize = success + fail + abort
	
	//for all urls
	static int total_urls = 0;
	static HashSet<String> total_unique_urls = new HashSet<>();
	static HashSet<String> total_unique_news_urls = new HashSet<>();
	static HashSet<String> total_unique_non_news_urls = new HashSet<>();
	
	static int size1to9kb = 0;
	static int size10to99kb = 0;
	static int size100to1mb = 0;
	static int sizegreaterthan1mb = 0;
	static int lessthan1kb = 0;
	
	static HashMap<Integer, Integer> all_status = new HashMap<Integer, Integer>();
	static HashMap<String, Integer> all_content_types = new HashMap<String, Integer>();
	
	String crawlStorageFolder = "/Users/WeiLi/Documents/tools/Eclipse/workspace/MyCrawlerProject/data/crawl/";
	
	private final static String baseUrl1 = "https://www.wsj.com/";
	private final static String baseUrl2 = "http://www.wsj.com/";
	
	public static Set<String> visited = new HashSet<>();

	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|xml|ppt|php" + "|mp3|mp4|zip|gz))$");
	
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
		String str = url.getURL();
		
		total_unique_urls.add(str);
		total_urls++;
		Controller.all.append(str.replaceAll(",", "_")+",");
		if(href.contains("//www.wsj.com/")) {
			 Controller.all.append("OK\n");
			 total_unique_news_urls.add(str);
		}else {
			 Controller.all.append("N_OK\n");
			 total_unique_non_news_urls.add(str);
		}
		return (href.startsWith(baseUrl1) || href.startsWith(baseUrl2)) && !FILTERS.matcher(href).matches();
	 }
	
		
	@Override
	protected void handlePageStatusCode(WebURL weburl, int statusCode, String statusDescription){
		String url = weburl.getURL();
		if(url.startsWith("http://")) {
			if(!fetched.contains("https://"+url.substring(7))) {
				fetched.add("https://"+url.substring(7));
				Controller.fetch.append(url.replaceAll(",", "_")+",").append(statusCode+"\n");
				if(statusCode >= 200 && statusCode < 300){
					successful_urls++;
				}else if(statusCode >= 300 && statusCode <= 399){
					aborted_urls++;
				}else{
					failed_urls++;
				}
				if(all_status.containsKey(statusCode)){
					int temp = all_status.get(statusCode);
					temp = temp + 1;
					all_status.put(statusCode, temp);
				}else{
					all_status.put(statusCode, 1);
				}
			}
		}else {
			if(!fetched.contains(url)) {
				fetched.add(url);
				Controller.fetch.append(url.replaceAll(",", "_")+",").append(statusCode+"\n");
				if(statusCode >= 200 && statusCode < 300){
					successful_urls++;
				}else if(statusCode >= 300 && statusCode <= 399){
					aborted_urls++;
				}else{
					failed_urls++;
				}
				if(all_status.containsKey(statusCode)){
					int temp = all_status.get(statusCode);
					temp = temp + 1;
					all_status.put(statusCode, temp);
				//	System.out.print(all_status.get(statusCode));
				}else{
					all_status.put(statusCode, 1);
				}
			}
		} 		
	}
		
	@Override
	public void onBeforeExit(){
	
	try(FileWriter fw = new FileWriter(crawlStorageFolder + "CrawlReport_Wall_Street_Journal.txt", true);
		    BufferedWriter bw = new BufferedWriter(fw);
		    PrintWriter out = new PrintWriter(bw))
		{
		    out.println("Name: Wei Li");
		    out.println("USC ID: 2579566209");
		    out.println("News site crawled: https://www.wsj.com\n");
		    out.println("Fetch Statistics");
		    out.println("================");
		    out.println("# fetches attempted: " + fetched.size());
		    out.println("# fetches succeeded: " + successful_urls);
		    out.println("# fetches aborted: " + aborted_urls);
		    out.println("# fetches failed: " + failed_urls + "\n");
		   
		    out.println("Outgoing URLs:");
		    out.println("==============");
		    out.println("Total URLs extracted: "+total_urls);
		    out.println("# unique URLs extracted: "+total_unique_urls.size());
		    out.println("# unique URLs within News Site: "+total_unique_news_urls.size());
		    out.println("# unique URLs outside News Site: "+total_unique_non_news_urls.size()+"\n");
		    
		    out.println("Status Codes:");
		    out.println("===============");		    
		    for (HashMap.Entry<Integer,Integer> entry : all_status.entrySet()) {
		    	  out.println(entry.getKey()+": "+ entry.getValue());
		    }
		    
		    out.println("\nFile Sizes:");
		    out.println("===============");
		    out.println("< 1KB: " + lessthan1kb);
		    out.println("1KB ~ <10KB: "+ size1to9kb);
		    out.println("10KB ~ <100KB: "+ size10to99kb);
		    out.println("100KB ~ <1MB: "+ size100to1mb);
		    out.println(">= 1MB: "+ sizegreaterthan1mb);
		    
		    out.println("\nContent Types:");
		    out.println("==============");
		    for (HashMap.Entry<String,Integer> entry : all_content_types.entrySet()) {
		    	  out.println(entry.getKey()+": " + entry.getValue());
		    }
		    out.close();

		} catch (IOException e) {
		e.printStackTrace();    
		}
	}
	
	
	@Override
	 public void visit(Page page) {
		String url = page.getWebURL().getURL();
		Integer contentdata = page.getContentData().length; 
		int contentdatainmb = contentdata/1024;
		String contentType = "";
		if(page.getContentType().contains(";")) {
			contentType = page.getContentType().substring(0,page.getContentType().indexOf(';'));	
		}else
			contentType = page.getContentType();
	 
		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			Set<WebURL> links = htmlParseData.getOutgoingUrls();
			System.out.println("URL: " + url);
			
			if(url.startsWith("http://") && !visited.contains("https://"+url.substring(7))) {
				if(all_content_types.containsKey(contentType)){
					int count = all_content_types.get(contentType);
					count = count + 1;
					all_content_types.put(contentType, count);
				}else{
					all_content_types.put(contentType, 1);
				}

				if(contentdatainmb >0 && contentdatainmb < 1){
					lessthan1kb++;
				}else if(contentdatainmb >= 1 && contentdatainmb < 10){
					size1to9kb++;
				}else if(contentdatainmb >= 10 && contentdatainmb < 100){
					size10to99kb++;
				}else if(contentdatainmb >=100 && contentdatainmb < 1024){
					size100to1mb++;
				}else if(contentdatainmb >= 1024){
					sizegreaterthan1mb++;
				}
				 visited.add("https://"+url.substring(7));
				 Controller.visit.append(url.replaceAll(",", "_")+",").append(contentdatainmb+",").append(links.size()+",").append(contentType+"\n");
			}
			if(url.startsWith("https://") &&!visited.contains(url)) {
				   if(all_content_types.containsKey(contentType)){
						int count = all_content_types.get(contentType);
						count = count + 1;
						all_content_types.put(contentType, count);
					}else{
						all_content_types.put(contentType, 1);
					}

					if(contentdatainmb >0 && contentdatainmb < 1){
						lessthan1kb++;
					}else if(contentdatainmb >= 1 && contentdatainmb < 10){
						size1to9kb++;
					}else if(contentdatainmb >= 10 && contentdatainmb < 100){
						size10to99kb++;
					}else if(contentdatainmb >=100 && contentdatainmb < 1024){
						size100to1mb++;
					}else if(contentdatainmb >= 1024){
						sizegreaterthan1mb++;
					}
				  visited.add(url);
				  Controller.visit.append(url.replaceAll(",", "_")+",").append(contentdatainmb+",").append(links.size()+",").append(contentType+"\n");
			 }	 
		}
	}
}