package com.example.demo.Article;

import java.time.Instant;
import java.time.OffsetDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "articles", uniqueConstraints = {
    @UniqueConstraint(name = "uk_articles_url", columnNames = "url")
}, indexes = {
    @Index(name = "idx_articles_published_at", columnList = "published_at"),
    @Index(name = "idx_articles_source_name", columnList = "source_name")
})
public class Article {

  /** Placeholder until real title is fetched; keeps DB `title NOT NULL`. */
  public static final String PLACEHOLDER_TITLE = "(No Title)";

  private static final int TITLE_COLUMN_LEN = 255;
  private static final int SUMMARY_SHORT_COLUMN_LEN = 1000;

  protected Article() {
  }

  /**
   * Minimal row for “URL registered, content not fetched yet”.
   * Title uses {@link #PLACEHOLDER_TITLE}; other optional fields stay null.
   */
  public static Article newPendingFromUrl(String url) {
    Article article = new Article();
    Instant now = Instant.now();
    article.url = url;
    article.title = PLACEHOLDER_TITLE;
    article.fetchedAt = now;
    article.createdAt = now;
    article.updatedAt = now;
    return article;
  }

  /**
   * Applies scraped content: non-blank title replaces the placeholder; body and a truncated
   * {@code summary_short} are set; {@code fetchedAt} and {@code updatedAt} are refreshed.
   */
  public void applyFetchedContent(String title, String body, Instant fetchedAt) {
    Instant now = fetchedAt != null ? fetchedAt : Instant.now();
    if (title != null && !title.isBlank()) {
      this.title = truncate(title, TITLE_COLUMN_LEN);
    } else {
      this.title = PLACEHOLDER_TITLE;
    }
    if (body != null && !body.isBlank()) {
      this.body = body;
      this.summaryShort = truncate(body, SUMMARY_SHORT_COLUMN_LEN);
    } else {
      this.body = null;
      this.summaryShort = null;
    }
    this.fetchedAt = now;
    this.updatedAt = now;
  }

  public boolean isPending() {
    return PLACEHOLDER_TITLE.equals(this.title);
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "url", nullable = false, length = 2048)
  private String url;

  @Column(name = "title", nullable = false, length = 255)
  private String title;

  @Column(name = "body", columnDefinition = "TEXT")
  private String body;

  @Column(name = "summary_short", length = 1000)
  private String summaryShort;

  @Column(name = "source_name", length = 255)
  private String sourceName;

  @Column(name = "published_at")
  private OffsetDateTime publishedAt;

  @Column(name = "fetched_at", nullable = false)
  private Instant fetchedAt;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  public Long getId() {
    return id;
  }

  public String getUrl() {
    return url;
  }

  public Instant getFetchedAt() {
    return fetchedAt;
  }

  public String getTitle() {
    return title;
  }

  public String getBody() {
    return body;
  }

  public String getSummaryShort() {
    return summaryShort;
  }

  private static String truncate(String value, int maxLen) {
    if (value.length() <= maxLen) {
      return value;
    }
    return value.substring(0, maxLen);
  }
}
