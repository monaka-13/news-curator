package com.example.demo.Article.dto;

import java.time.Instant;

public class FetchArticleResponse {

  private final Long id;
  private final String url;
  private final String title;
  private final String body;
  private final String summaryShort;
  private final Instant fetchedAt;

  public FetchArticleResponse(Long id, String url, String title, String body, String summaryShort,
      Instant fetchedAt) {
    this.id = id;
    this.url = url;
    this.title = title;
    this.body = body;
    this.summaryShort = summaryShort;
    this.fetchedAt = fetchedAt;
  }

  public Long getId() {
    return id;
  }

  public String getUrl() {
    return url;
  }

  public String getTitle() {
    return title;
  }

  public String getBody() {
    return body;
  }

  public String getSummaryShort() {
    return summaryShort;
  }

  public Instant getFetchedAt() {
    return fetchedAt;
  }
}
