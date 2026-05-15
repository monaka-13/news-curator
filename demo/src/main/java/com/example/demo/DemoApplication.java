package com.example.demo;

import java.util.Map;

import io.github.cdimascio.dotenv.Dotenv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

  /**
   * Maps {@code .env} keys to Spring {@code application.properties} names. {@code APP_*}
   * system properties are not reliably bound to {@code @ConfigurationProperties} under
   * {@code app.summarization.*}.
   */
  private static final Map<String, String> DOTENV_TO_SPRING =
      Map.of(
          "APP_SUMMARIZATION_ENABLED", "app.summarization.enabled",
          "APP_SUMMARIZATION_API_KEY", "app.summarization.api-key",
          "APP_SUMMARIZATION_BASE_URL", "app.summarization.base-url",
          "APP_SUMMARIZATION_MODEL", "app.summarization.model");

  public static void main(String[] args) {
    Dotenv dotenv = Dotenv.configure().directory("./").ignoreIfMissing().load();
    dotenv
        .entries()
        .forEach(
            e -> {
              String key = DOTENV_TO_SPRING.getOrDefault(e.getKey(), e.getKey());
              if (System.getenv(key) == null && System.getProperty(key) == null) {
                System.setProperty(key, e.getValue());
              }
            });
    SpringApplication.run(DemoApplication.class, args);
  }
}
