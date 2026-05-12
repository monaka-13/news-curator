package com.example.demo.Article.search;

import com.example.demo.Article.dto.PagedArticlesResponse;

/**
 * Application boundary for paged article listing and keyword search.
 * <p>
 * HTTP layer will call this in a later step; swap the Spring bean that implements this
 * interface (e.g. to Elasticsearch) without changing controllers.
 */
public interface ArticleSearchPort {

  /**
   * @param q    optional keyword; blank or null means list all (no full-text filter)
   * @param page zero-based page index (must be {@code >= 0})
   * @param size page size; values {@code < 1} are treated as default; capped at a maximum in the implementation
   */
  PagedArticlesResponse search(String q, int page, int size);
}
