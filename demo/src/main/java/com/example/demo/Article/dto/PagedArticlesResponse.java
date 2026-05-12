package com.example.demo.Article.dto;

import java.util.List;

public record PagedArticlesResponse(
    List<ArticleListItem> content,
    long totalElements,
    int totalPages,
    int number,
    int size) {
}
