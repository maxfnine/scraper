package com.example.scraper.scraper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:scraper.properties")
public class ScraperConfiguration {
	
	@Value("${scraper.initial.url}")
	private String initialUrl;
	
	@Value("${scraper.max.urls}")
	private int maxUrls;
	
	@Value("${scraper.depth.level}")
	private int maxDepth;
	
	@Value("${scraper.unique.url}")
	private boolean isUnique;
	
	@Bean
	public WebScraper webScraper() {
		WebScraper webScraper=new WebScraper(initialUrl,maxUrls,maxDepth,isUnique);
		return webScraper;
	}
	
}
