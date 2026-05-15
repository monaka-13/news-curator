package com.example.demo.summarization;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Article.Article;
import com.example.demo.Article.ArticleRepository;

@Service
public class SummarizationAsyncService {

  private static final Logger log = LoggerFactory.getLogger(SummarizationAsyncService.class);

  private final ArticleRepository articleRepository;
  private final SummarizationProperties properties;
  private final OpenAiCompatibleSummarizationClient summarizationClient;

  private final ConcurrentHashMap<Long, Object> locks = new ConcurrentHashMap<>();

  public SummarizationAsyncService(
      ArticleRepository articleRepository,
      SummarizationProperties properties,
      OpenAiCompatibleSummarizationClient summarizationClient) {
    this.articleRepository = articleRepository;
    this.properties = properties;
    this.summarizationClient = summarizationClient;
  }

  @Async("summarizationExecutor")
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void summarizeAsync(Long articleId) {
    if (articleId == null) {
      return;
    }
    if (!properties.isEnabled()) {
      log.debug("Summarization disabled; skipping articleId={}", articleId);
      return;
    }
    if (properties.getApiKey() == null || properties.getApiKey().isBlank()) {
      log.debug("Summarization API key missing; skipping articleId={}", articleId);
      return;
    }

    Object lock = locks.computeIfAbsent(articleId, id -> new Object());
    synchronized (lock) {
      try {
        Article article = articleRepository.findById(articleId).orElse(null);
        if (article == null) {
          return;
        }
        if (article.getBody() == null || article.getBody().isBlank()) {
          return;
        }
        if (article.getSummaryShort() != null && !article.getSummaryShort().isBlank()) {
          return;
        }

        String summary = summarizationClient.summarizeEnglish(article.getTitle(), article.getBody());
        if (summary == null || summary.isBlank()) {
          return;
        }

        article = articleRepository.findById(articleId).orElse(null);
        if (article == null) {
          return;
        }
        if (article.getSummaryShort() != null && !article.getSummaryShort().isBlank()) {
          return;
        }
        if (article.getBody() == null || article.getBody().isBlank()) {
          return;
        }

        article.applyAiSummary(summary);
        articleRepository.save(article);
      } catch (Exception e) {
        log.warn("Summarization failed for articleId={}", articleId, e);
      }
    }
  }
}
