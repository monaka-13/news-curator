package com.example.demo.Article;

public class ArticleUrlConflictException extends RuntimeException {

  public ArticleUrlConflictException(String url) {
    super("An article with this URL already exists");
  }
}
