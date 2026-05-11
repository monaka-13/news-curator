package com.example.demo.scrape;

/**
 * In-memory result of fetching and parsing a page. Not persisted; use to update {@link com.example.demo.Article.Article}.
 */
public record FetchedPage(String title, String body) {

  public FetchedPage {
    title = title == null ? "" : title;
    body = body == null ? "" : body;
  }

  public String getTitle() {
    return title();
  }

  public String getBody() {
    return body();
  }
}
