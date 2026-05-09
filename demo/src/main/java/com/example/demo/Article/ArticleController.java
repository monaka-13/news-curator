package com.example.demo.Article;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Article.dto.CreateArticleRequest;
import com.example.demo.Article.dto.CreateArticleResponse;

import jakarta.validation.Valid;

@RestController
public class ArticleController {

  private final ArticleService articleService;

  public ArticleController(ArticleService articleService) {
    this.articleService = articleService;
  }

  @PostMapping("/api/articles")
  public ResponseEntity<CreateArticleResponse> create(@Valid @RequestBody CreateArticleRequest request) {
    CreateArticleResponse body = articleService.createFromUrl(request.getUrl());
    return ResponseEntity.status(HttpStatus.CREATED).body(body);
  }

}