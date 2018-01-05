import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import com.google.common.io.Files;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class MyCrawler extends WebCrawler {
//	private final static Pattern FILTERS = Pattern.compile(".*(\\.(html|doc|docx|pdf))$");
//	private final static Pattern imgPattern = Pattern.compile(".*(\\.(png|bmp|jpg|jpeg|gif|tiff?))$");			
//	private final static Pattern Extension = Pattern.compile(".*\\..*");
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|mp3|zip|gz|vcf|xml))$");
	private final static String baseUrl1 = "https://www.wsj.com/";
	private final static String baseUrl2 = "http://www.wsj.com/";
	
	public static Set<String> visited = new HashSet<>();
	public static Set<String> fetched = new HashSet<>();
	
	/* 
	 * This method receives two parameters. The first parameter is the page 
	 * in which we have discovered this new url and the second parameter is 
	 * the new url. You should implement this function to specify whether 
	 * the given url should be crawled or not (based on your crawling logic). 
	 * In this example, we are instructing the crawler to ignore urls that 
	 * have css, js, git, ... extensions and to only accept urls that start 
	 * with "http://www.viterbi.usc.edu/". In this case, we didn't need the 
	 * referringPage parameter to make the decision. */ 
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) { 
		String href = url.getURL().toLowerCase(); 
		String str = url.getURL();
		Controller.all.append(str.replaceAll(",", "_")+",");
		if(str.contains("//www.wsj.com/")) {
			 Controller.all.append("OK\n");
		}else {
			 Controller.all.append("N_OK\n");
		}
	//	return FILTERS.matcher(href).matches() && (href.startsWith("https://www.wsj.com/")||href.startsWith("http://www.wsj.com/")) ; 
	//	return (FILTERS.matcher(href).matches()||imgPattern.matcher(href).matches()||!Extension.matcher(href).matches()) && href.contains("//www.wsj.com/");
		return (href.startsWith(baseUrl1) || href.startsWith(baseUrl2)) && !FILTERS.matcher(href).matches();
	}
	
	@Override
	protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
		String url = webUrl.getURL();
		if(url.startsWith("http://")) {
			if(!fetched.contains("https://"+url.substring(7))) {
				fetched.add("https://"+url.substring(7));
				Controller.fetch.append(url.replaceAll(",", "_")+",").append(statusCode+"\n");
			}
		}else {
			if(!fetched.contains(url)) {
				fetched.add(url);
				Controller.fetch.append(url.replaceAll(",", "_")+",").append(statusCode+"\n");
			}
		} 
    }
	
	/* 
	 * This function is called when a page is fetched and ready 
	 * to be processed by your program. 
	 */ 
	 @Override 
	 public void visit(Page page) { 
		 String url = page.getWebURL().getURL(); 
		 int statusCode = page.getStatusCode();
		 String contentType = page.getContentType().substring(0,page.getContentType().indexOf(';'));
		 int numBytes = page.getContentData().length;
		 
		 Set<String> type = new HashSet<>();
		 type.add("text/html");
		 type.add("image/gif");
		 type.add("image/jpeg");
		 type.add("image/png");
		 type.add("application/pdf");
		 type.add("application/msword");
		 
		 //for fetched urls
	/*	 if(statusCode == 200 && type.contains(contentType)) {
			 if(url.startsWith("http://")){
				url = "https://"+url.substring(7);
			 }
			 if(!fetched.contains(url)) {
				fetched.add(url);
				Controller.fetch.append(url.replaceAll(",", "_")+",").append(statusCode+"\n");
			 }
		 }   */
		 
		 if (page.getParseData() instanceof HtmlParseData) {
			 HtmlParseData htmlParseData = (HtmlParseData) page.getParseData(); 
			 Set<WebURL> links = htmlParseData.getOutgoingUrls(); 
			 System.out.println("URL: " + url);
			 System.out.println("Status code: " + statusCode);
			 System.out.println("Length: " + numBytes);
			 System.out.println("Content type: " + contentType);
			 System.out.println("Number of outgoing links: " + links.size());			 
			 
			 if(url.startsWith("http://") && !visited.contains(url)) {
				 visited.add("https://"+url.substring(7));
				 Controller.visit.append(url.replaceAll(",", "_")+",").append(numBytes+",").append(links.size()+",").append(contentType+"\n");
			 }else {
				 if(!visited.contains(url)) {
					 visited.add(url);
					 Controller.visit.append(url.replaceAll(",", "_")+",").append(numBytes+",").append(links.size()+",").append(contentType+"\n");
				 }
			 }
		} 	
	}	
}
