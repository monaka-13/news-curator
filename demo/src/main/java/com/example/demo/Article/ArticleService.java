package com.example.demo.Article;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Article.dto.CreateArticleResponse;

@Service
public class ArticleService {

  private final ArticleRepository articleRepository;

  public ArticleService(ArticleRepository articleRepository) {
    this.articleRepository = articleRepository;
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
}
