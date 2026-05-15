package com.example.demo.summarization;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Component
public class OpenAiCompatibleSummarizationClient {

  private static final String SYSTEM_PROMPT =
      """
      You summarize news articles in English for a reading list.
      Rules:
      - Use only information explicitly stated in the article.
      - Do not invent facts, quotes, numbers, or sources.
      - Output plain text only (no markdown).
      - Maximum length: 1000 characters. Be concise.""";

  private final SummarizationProperties properties;
  private final JsonMapper jsonMapper;

  public OpenAiCompatibleSummarizationClient(
      SummarizationProperties properties, JsonMapper jsonMapper) {
    this.properties = properties;
    this.jsonMapper = jsonMapper;
  }

  public String summarizeEnglish(String title, String body) throws IOException, InterruptedException {
    String base = properties.getBaseUrl().trim();
    if (base.endsWith("/")) {
      base = base.substring(0, base.length() - 1);
    }
    URI uri = URI.create(base + "/chat/completions");

    String bodyForPrompt = truncateForPrompt(body, properties.getMaxBodyChars());
    String userContent =
        "Title:\n" + (title == null ? "" : title.trim()) + "\n\nArticle:\n" + bodyForPrompt;

    Map<String, Object> requestBody =
        Map.of(
            "model",
            properties.getModel(),
            "temperature",
            0.3,
            "max_tokens",
            512,
            "messages",
            List.of(
                Map.of("role", "system", "content", SYSTEM_PROMPT),
                Map.of("role", "user", "content", userContent)));

    String json = jsonMapper.writeValueAsString(requestBody);

    HttpClient httpClient =
        HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(properties.getConnectTimeoutMs()))
            .build();

    HttpRequest request =
        HttpRequest.newBuilder(uri)
            .timeout(Duration.ofMillis(properties.getReadTimeoutMs()))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + properties.getApiKey().trim())
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    if (response.statusCode() < 200 || response.statusCode() >= 300) {
      throw new IOException(
          "Summarization HTTP " + response.statusCode() + ": " + response.body());
    }

    JsonNode root = jsonMapper.readTree(response.body());
    JsonNode choices = root.path("choices");
    if (!choices.isArray() || choices.isEmpty()) {
      return null;
    }
    JsonNode content = choices.get(0).path("message").path("content");
    if (!content.isTextual()) {
      return null;
    }
    String text = content.asText().trim();
    return text.isEmpty() ? null : text;
  }

  private static String truncateForPrompt(String body, int maxChars) {
    if (body == null) {
      return "";
    }
    String t = body.trim();
    if (t.length() <= maxChars) {
      return t;
    }
    return t.substring(0, maxChars);
  }
}
