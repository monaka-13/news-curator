package com.example.demo.Article;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
  boolean existsByUrl(String url);

  Optional<Article> findByUrl(String url);
}
