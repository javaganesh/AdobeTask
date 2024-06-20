package com.google.in.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.in.exception.ApiNotAvailableException;
import com.google.in.exception.NewsDataNotFoundException;
import com.google.in.model.NewsArticle;
import com.google.in.service.NewsArticleService;

@RestController
@RequestMapping("/api/news")
public class NewsArticleController {

    private final NewsArticleService newsService;

    @Autowired
    public NewsArticleController(NewsArticleService newsService) {
        this.newsService = newsService;
    }

	/*
	 * @GetMapping public ResponseEntity<List<NewsArticle>> getNewsArticles() { try
	 * { List<NewsArticle> articles = newsService.fetchNewsArticles(); return
	 * ResponseEntity.ok(articles); } catch (NewsDataNotFoundException ex) { return
	 * ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); } catch
	 * (ApiNotAvailableException ex) { return
	 * ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null); } catch
	 * (Exception ex) { return
	 * ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); } }
	 */

    @GetMapping("/between")
    public ResponseEntity<List<NewsArticle>> getNewsArticlesBetweenDates(@RequestParam(required = false) String from,
                                                                         @RequestParam(required = false) String to) {
        if (from == null || to == null) {
            // Handle case where either from or to date is missing
            return ResponseEntity.badRequest().body(null);
        }

        try {
            List<NewsArticle> articles = newsService.fetchNewsArticlesBetweenDates(from, to);
            return ResponseEntity.ok(articles);
        } catch (NewsDataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (ApiNotAvailableException ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
