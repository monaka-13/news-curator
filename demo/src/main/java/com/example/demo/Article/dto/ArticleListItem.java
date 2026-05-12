package com.example.demo.Article.dto;

import java.time.Instant;

/** One row in a search or list result (no {@code body}). */
public record ArticleListItem(
    Long id,
    String url,
    String title,
    String summaryShort,
    Instant fetchedAt) {
}
