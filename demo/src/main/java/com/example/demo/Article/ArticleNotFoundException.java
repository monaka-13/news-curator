package com.example.demo.Article;

public class ArticleNotFoundException extends RuntimeException {

  private final Long id;

  public ArticleNotFoundException(Long id) {
    super("Article not found: id=" + id);
    this.id = id;
  }

  public Long getId() {
    return id;
  }
}
