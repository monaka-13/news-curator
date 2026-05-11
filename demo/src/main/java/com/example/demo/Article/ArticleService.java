package com.example.demo.Article;

import java.io.IOException;
import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Article.dto.CreateArticleResponse;
import com.example.demo.Article.dto.FetchArticleResponse;
import com.example.demo.scrape.FetchedPage;
import com.example.demo.scrape.SimplePageFetcher;

@Service
public class ArticleService {

  private final ArticleRepository articleRepository;
  private final SimplePageFetcher simplePageFetcher;

  public ArticleService(ArticleRepository articleRepository, SimplePageFetcher simplePageFetcher) {
    this.articleRepository = articleRepository;
    this.simplePageFetcher = simplePageFetcher;
  }

  /**
   * Persists a new article for the given URL. URL format is validated on the controller/DTO;
   * here we only normalize whitespace and enforce uniqueness.
   */
  @Transactional
  public CreateArticleResponse createFromUrl(String url) {
    String normalized = url == null ? "" : url.trim();
    if (normalized.isEmpty()) {
      throw new IllegalArgumentException("url must not be blank");
    }
    if (articleRepository.existsByUrl(normalized)) {
      throw new ArticleUrlConflictException(normalized);
    }
    Article saved = articleRepository.save(Article.newPendingFromUrl(normalized));
    return new CreateArticleResponse(saved.getId(), saved.getUrl(), saved.getFetchedAt());
  }

  /**
   * Loads the article by id, fetches remote HTML, updates persisted fields, and returns the new state.
   * Intentionally not {@code @Transactional} across the HTTP call so DB work stays in short repository transactions.
   */
  public FetchArticleResponse fetchById(Long id) {
    if (id == null || id < 1) {
      throw new IllegalArgumentException("id must be a positive number");
    }
    Article article = articleRepository.findById(id).orElseThrow(() -> new ArticleNotFoundException(id));
    String url = article.getUrl();
    FetchedPage page;
    try {
      page = simplePageFetcher.parsePage(url);
    } catch (IOException e) {
      throw new ArticleFetchException("Failed to fetch content for url=" + url, e);
    }
    article.applyFetchedContent(page.getTitle(), page.getBody(), Instant.now());
    Article saved = articleRepository.save(article);
    return new FetchArticleResponse(
        saved.getId(),
        saved.getUrl(),
        saved.getTitle(),
        saved.getBody(),
        saved.getSummaryShort(),
        saved.getFetchedAt());
  }
}
