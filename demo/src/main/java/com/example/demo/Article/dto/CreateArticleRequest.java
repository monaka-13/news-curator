package com.example.demo.Article.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CreateArticleRequest {
  @NotBlank(message = "url is required")
  @Pattern(regexp = "^(https?://).+", message = "url must start with http:// or https://")
  private String url;

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
