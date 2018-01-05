import java.io.PrintWriter;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {
	public static StringBuffer fetch;
	public static StringBuffer visit;
	public static StringBuffer all;
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		
		String crawlStorageFolder = "/Users/WeiLi/Documents/tools/Eclipse/workspace/MyCrawlerProject/data/crawl/";
		String seed = "https://www.wsj.com/";
		int numberOfCrawlers = 7; 
		int maxDepthOfCrawling = 16;
		int maxPagesToFetch = 20000;
		int politenessDelay = 100;
		
		//create file
		PrintWriter fetchFile = new PrintWriter(crawlStorageFolder + "fetch_Wall_Street_Journal.csv");
		PrintWriter visitFile = new PrintWriter(crawlStorageFolder + "visit_Wall_Street_Journal.csv");
		PrintWriter allFile = new PrintWriter(crawlStorageFolder + "urls_Wall_Street_Journal.csv");
		fetch = new StringBuffer();
		visit = new StringBuffer();
		all = new StringBuffer();
		fetch.append("URL,Http Status Code\n");
		visit.append("URL,Html length,Number of outlinks,Content-type\n");
		all.append("URL,IsOK\n");
			
		//crawl config
		CrawlConfig config = new CrawlConfig(); 
		config.setCrawlStorageFolder(crawlStorageFolder); 
		config.setIncludeHttpsPages(true);
		config.setFollowRedirects(true);
		
		//set crawl depth
		config.setMaxDepthOfCrawling(maxDepthOfCrawling);
		//set maximum number of pages to crawl
		config.setMaxPagesToFetch(maxPagesToFetch);
		//set politeness
		config.setPolitenessDelay(politenessDelay);
		//set user agent
		//config.setUserAgentString(userAgentString);
		//set binary content can be included in the crawl
		config.setIncludeBinaryContentInCrawling(true);
		/* 
		 * Instantiate the controller for this crawl. 
		 */ 
		
		PageFetcher pageFetcher = new PageFetcher(config); 
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig(); 
		robotstxtConfig.setEnabled(false);
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher); 
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer); 
		/* 
		 * For each crawl, you need to add some seed urls. These are the first 
		 * URLs that are fetched and then the crawler starts following links 
		 * which are found in these pages 
		 */ 
		controller.addSeed(seed);
		/* 
		 * Start the crawl. This is a blocking operation, meaning that your code 
		 * will reach the line after this only when crawling is finished. 
		 */ 
	//	controller.start(MyCrawler.class, numberOfCrawlers);
		controller.start(Crawler.class, numberOfCrawlers);
		
		fetchFile.write(fetch.toString());
		fetchFile.close();
		visitFile.write(visit.toString());
		visitFile.close();
		allFile.write(all.toString());
		allFile.close();
	}

}
