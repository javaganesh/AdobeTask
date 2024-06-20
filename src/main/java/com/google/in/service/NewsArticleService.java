package com.google.in.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import com.google.in.config.NewsApiProperties;
import com.google.in.exception.ApiNotAvailableException;
import com.google.in.exception.NewsDataNotFoundException;
import com.google.in.model.NewsArticle;

@Service
public class NewsArticleService {

	 private static final String API_URL = "https://api.spaceflightnewsapi.net/v3/articles";
	    
	  private final RestTemplate restTemplate;
	  private final NewsApiProperties newsApiProperties;
	  
	  public NewsArticleService(RestTemplate restTemplate,NewsApiProperties newsApiProperties) {
	        this.restTemplate = restTemplate;
	        this.newsApiProperties=newsApiProperties;
	    }
	  
	  public List<NewsArticle> fetchNewsArticles() {
	        String apiUrl = newsApiProperties.getUrl();
	        //String apiKey = newsApiProperties.getKey();

	        HttpHeaders headers = new HttpHeaders();
	       // headers.set("Authorization", apiKey);
	        HttpEntity<String> entity = new HttpEntity<>(headers);

	        try {
	            ResponseEntity<NewsArticle[]> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, NewsArticle[].class);
	            if (response.getBody() != null) {
	                return Arrays.asList(response.getBody());
	            } else {
	                throw new NewsDataNotFoundException("No news articles found from the API");
	            }
	        } catch (HttpClientErrorException.NotFound | NewsDataNotFoundException ex) {
	            throw new NewsDataNotFoundException("News data not found: " + ex.getMessage(), ex);
	        } catch (HttpClientErrorException.Forbidden ex) {
	            throw new ApiNotAvailableException("Access to API forbidden: " + ex.getMessage(), ex);
	        } catch (HttpClientErrorException | HttpServerErrorException ex) {
	            throw new ApiNotAvailableException("Error fetching news articles from API: " + ex.getMessage(), ex);
	        } catch (UnknownHttpStatusCodeException ex) {
	            throw new ApiNotAvailableException("Unknown HTTP status code returned from API: " + ex.getStatusCode(), ex);
	        } catch (Exception ex) {
	            throw new ApiNotAvailableException("An error occurred while fetching news articles: " + ex.getMessage(), ex);
	        }
	    }

	  public List<NewsArticle> fetchNewsArticlesBetweenDates(String from, String to) {
	        // Validate input parameters
	        if (from == null || to == null) {
	            throw new IllegalArgumentException("From and To dates must not be null");
	        }

	        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
	        LocalDateTime fromDate;
	        LocalDateTime toDate;

	        try {
	            fromDate = LocalDateTime.parse(from, formatter);
	            toDate = LocalDateTime.parse(to, formatter);
	        } catch (Exception e) {
	            throw new IllegalArgumentException("Invalid date format. Please use ISO date-time format (yyyy-MM-dd'T'HH:mm:ss)", e);
	        }

	        List<NewsArticle> articles = fetchNewsArticles();

	        return articles.stream()
	                .filter(article -> {
	                    LocalDateTime publishedDate;
	                    try {
	                        publishedDate = LocalDateTime.parse(article.getPublishedAt(), formatter);
	                    } catch (Exception e) {
	                        throw new IllegalArgumentException("Invalid published date format in news article: " + article.getPublishedAt(), e);
	                    }
	                    return (publishedDate.isEqual(fromDate) || publishedDate.isAfter(fromDate)) &&
	                            (publishedDate.isEqual(toDate) || publishedDate.isBefore(toDate));
	                })
	                .collect(Collectors.toList());
	    }
 
	  
}
