package com.example.demo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.demo.Article.ArticleFetchException;
import com.example.demo.Article.ArticleNotFoundException;
import com.example.demo.Article.ArticleUrlConflictException;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(ArticleUrlConflictException.class)
  public ResponseEntity<Void> handleUrlConflict(ArticleUrlConflictException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).build();
  }

  @ExceptionHandler(ArticleNotFoundException.class)
  public ResponseEntity<String> handleArticleNotFound(ArticleNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ExceptionHandler(ArticleFetchException.class)
  public ResponseEntity<String> handleArticleFetch(ArticleFetchException ex) {
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(ex.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
    return ResponseEntity.badRequest().body(ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<String> handleValidation(MethodArgumentNotValidException ex) {
    String message = ex.getBindingResult().getFieldErrors().stream()
        .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
        .findFirst()
        .orElse("validation failed");
    return ResponseEntity.badRequest().body(message);
  }
}
