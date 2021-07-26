package com.example.scraper.scraper;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
public class ScraperApplication {
	
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context=new AnnotationConfigApplicationContext(ScraperConfiguration.class);
		WebScraper scraper=context.getBean("webScraper",WebScraper.class);
		scraper.scrapeLinks();
		context.close();
	}

}
