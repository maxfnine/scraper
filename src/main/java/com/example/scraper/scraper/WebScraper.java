package com.example.scraper.scraper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.PreDestroy;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class WebScraper {
	private String initialUrl;
	private int maximumUrls;
	private int depth;
	private boolean isUnique;
	private ExecutorService executorService;
	
	

	public String getInitialUrl() {
		return initialUrl;
	}

	public void setInitialUrl(String initialUrl) {
		this.initialUrl = initialUrl;
	}

	public int getMaximumUrls() {
		return maximumUrls;
	}

	public void setMaximumUrls(int maximumUrls) {
		this.maximumUrls = maximumUrls;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public boolean isUnique() {
		return isUnique;
	}

	public void setUnique(boolean isUnique) {
		this.isUnique = isUnique;
	}

	public WebScraper(String initialUrl, int maximumUrls, int depth, boolean isUnique) {
		this.initialUrl = initialUrl;
		this.maximumUrls = maximumUrls;
		this.depth = depth;
		this.isUnique = isUnique;
		this.executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
	}

	public WebScraper() {
	}
	
	@PreDestroy
	private void disposeExecutor() {
		executorService.shutdown();
	}

	public void scrapeLinks() {
		try {

			Map<Integer, List<String>> levelLinks = new HashMap<Integer, List<String>>();
			
			
			Set<String> storedLinks = new HashSet<String>();
			for (int i = 0; i <= depth; i++) {
				if (i == 0) {
					List<String> currentLevelLinks = new ArrayList<String>();
					levelLinks.put(i, currentLevelLinks);
					extractCurrentLevelLinks(initialUrl, storedLinks, i, currentLevelLinks);

				} 
				else{
					List<String> prevLevelLinks = levelLinks.get(i - 1);
					List<String> currentLevelLinks = new ArrayList<String>();
					levelLinks.put(i, currentLevelLinks);
					for (String prevLevelLink : prevLevelLinks) {
						extractCurrentLevelLinks(prevLevelLink, storedLinks, i, currentLevelLinks);

					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void extractCurrentLevelLinks(String urlToScrape, Set<String> storedLinks, int i,List<String> currentLevelLinks) throws IOException 
	{
		List<Runnable> tasks = new ArrayList<Runnable>();
		Document doc = Jsoup.connect(urlToScrape).get();
		List<Element> links = doc.getElementsByTag("a");
		tasks.add(new RunnableTask(i, urlToScrape, doc.toString()));
		extractcurrentLevelLinks(storedLinks, currentLevelLinks, links);
		storeUrlContents(tasks);
	}

	private void storeUrlContents(List<Runnable> tasks) {
		for(Runnable task:tasks) {
			executorService.execute(task);
		}
		
	}

	private void extractcurrentLevelLinks(Set<String> storedLinks, List<String> currentLevelLinks, List<Element> links) {
		int linksInserted=0;
		for (Element link : links) {
			String hrefLink = link.attr("href").trim();
			if (!hrefLink.isEmpty() && (hrefLink.startsWith("http") || hrefLink.startsWith("https"))) {
				if (isUnique) {
					if (!storedLinks.contains(hrefLink)) {
						storedLinks.add(hrefLink);
						currentLevelLinks.add(hrefLink);
						linksInserted++;
					}
				} else {
					currentLevelLinks.add(hrefLink);
					linksInserted++;
				}

				if (linksInserted == maximumUrls) {
					break;
				}

			}

		}
	}
	
	public class RunnableTask implements Runnable {
		private int depthLevel;
		private String url;
		private String urlContent;

		public RunnableTask(int depthLevel, String url, String urlContent) {
			this.depthLevel = depthLevel;
			this.url = url;
			this.urlContent = urlContent;
		}

		@Override
		public void run() {
			String cleanUrl=url.replaceAll("[^a-zA-Z0-9]", "_");
			String fileName=depthLevel+"_"+cleanUrl+".html";
			
		    BufferedWriter writer;
			try {
				writer = new BufferedWriter(new FileWriter(fileName));
				writer.write(urlContent);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}	    
			
		}

	}

}
