package com.example.demo.summarization;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.summarization")
public class SummarizationProperties {

  private boolean enabled = false;
  private String apiKey = "";
  private String baseUrl = "https://api.groq.com/openai/v1";
  private String model = "llama-3.1-8b-instant";
  private int maxBodyChars = 16_000;
  private int connectTimeoutMs = 10_000;
  private int readTimeoutMs = 120_000;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public int getMaxBodyChars() {
    return maxBodyChars;
  }

  public void setMaxBodyChars(int maxBodyChars) {
    this.maxBodyChars = maxBodyChars;
  }

  public int getConnectTimeoutMs() {
    return connectTimeoutMs;
  }

  public void setConnectTimeoutMs(int connectTimeoutMs) {
    this.connectTimeoutMs = connectTimeoutMs;
  }

  public int getReadTimeoutMs() {
    return readTimeoutMs;
  }

  public void setReadTimeoutMs(int readTimeoutMs) {
    this.readTimeoutMs = readTimeoutMs;
  }
}
