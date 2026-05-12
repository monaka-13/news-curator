package com.example.demo.Article;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Article.dto.CreateArticleRequest;
import com.example.demo.Article.dto.CreateArticleResponse;
import com.example.demo.Article.dto.FetchArticleResponse;
import com.example.demo.Article.dto.PagedArticlesResponse;
import com.example.demo.Article.search.ArticleSearchPort;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@Validated
public class ArticleController {

  private final ArticleService articleService;
  private final ArticleSearchPort articleSearchPort;

  public ArticleController(ArticleService articleService, ArticleSearchPort articleSearchPort) {
    this.articleService = articleService;
    this.articleSearchPort = articleSearchPort;
  }

  @GetMapping("/api/articles")
  public PagedArticlesResponse listArticles(
      @RequestParam(required = false) String q,
      @RequestParam(defaultValue = "0") @Min(0) int page,
      @RequestParam(defaultValue = "20") @Min(0) @Max(100) int size) {
    return articleSearchPort.search(q, page, size);
  }

  @PostMapping("/api/articles")
  public ResponseEntity<CreateArticleResponse> create(@Valid @RequestBody CreateArticleRequest request) {
    CreateArticleResponse body = articleService.createFromUrl(request.getUrl());
    return ResponseEntity.status(HttpStatus.CREATED).body(body);
  }

  @PostMapping("/api/articles/{id}/fetch")
  public ResponseEntity<FetchArticleResponse> fetch(@PathVariable Long id) {
    FetchArticleResponse body = articleService.fetchById(id);
    return ResponseEntity.ok(body);
  }

}