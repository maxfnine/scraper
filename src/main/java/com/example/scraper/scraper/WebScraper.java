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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class WebScraper {
	private String initialUrl;
	private int maximumUrls;
	private int depth;
	private boolean isUnique;
	
	

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
	}

	public WebScraper() {
		
	}

	public void scrapeLinks() {
		try {

			Map<Integer, List<String>> levelLinks = new HashMap<Integer, List<String>>();
			List<Runnable> tasks = new ArrayList<Runnable>();
			
			Set<String> storedLinks = new HashSet<String>();
			for (int i = 0; i < depth; i++) {
				if (i == 0) {
					Document doc = Jsoup.connect(initialUrl).get();
					doc.getElementsByTag("a");
					List<Element> links = doc.getElementsByTag("a");
					List<String> currentLevelLinks = new ArrayList<String>();
					levelLinks.put(i, currentLevelLinks);
					tasks.add(new RunnableTask(i, initialUrl, doc.toString()));
					extractcurrentLevelLinks(storedLinks, currentLevelLinks, links);

				} else {
					List<String> prevLevelLinks = levelLinks.get(i - 1);
					List<String> currentLevelLinks = new ArrayList<String>();
					levelLinks.put(i, currentLevelLinks);
					for (String prevLevelLink : prevLevelLinks) {
						Document doc = Jsoup.connect(prevLevelLink).get();
						doc.getElementsByTag("a");
						List<Element> links = doc.getElementsByTag("a");
						tasks.add(new RunnableTask(i, prevLevelLink, doc.toString()));
						extractcurrentLevelLinks(storedLinks, currentLevelLinks, links);

					}
				}
				System.out.println(levelLinks);
				tasks.forEach(task -> new Thread(task).start());
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void extractcurrentLevelLinks(Set<String> storedLinks, List<String> currentLevelLinks, List<Element> links) {
		for (Element link : links) {
			String hrefLink = link.attr("href").trim();
			if (!hrefLink.isEmpty() && (hrefLink.startsWith("http") || hrefLink.startsWith("https"))) {
				if (isUnique) {
					if (!storedLinks.contains(hrefLink)) {
						storedLinks.add(hrefLink);
						currentLevelLinks.add(hrefLink);
					}
				} else {
					currentLevelLinks.add(hrefLink);
				}

				if (currentLevelLinks.size() == maximumUrls) {
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	    
			
		}

	}

}
