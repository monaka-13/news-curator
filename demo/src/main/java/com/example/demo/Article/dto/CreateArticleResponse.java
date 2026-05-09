package com.example.demo.Article.dto;

import java.time.Instant;

public class CreateArticleResponse {
  private Long id;
  private String url;
  private Instant fetchedAt;

  public CreateArticleResponse(Long id, String url, Instant fetchedAt) {
    this.id = id;
    this.url = url;
    this.fetchedAt = fetchedAt;
  }

  public Long getId() {
    return id;
  }

  public String getUrl() {
    return url;
  }

  public Instant getFetchedAt() {
    return fetchedAt;
  }
}