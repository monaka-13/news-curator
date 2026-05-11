package com.example.demo.scrape;

import java.io.IOException;
import java.util.Comparator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class SimplePageFetcher {

  private static final String USER_AGENT = "NewsCurator/0.1 (personal portfolio; +https://github.com/)";
  private static final int TIMEOUT_MS = 15_000;
  private static final int MAX_TITLE_LEN = 255;
  private static final int MAX_BODY_LEN = 10_000;
  private static final int MIN_ROOT_TEXT_LEN = 40;

  /**
   * Fetches the URL, extracts title and body text, and returns a {@link FetchedPage}.
   *
   * @param url HTTP(S) URL; must not be null or blank
   * @throws IllegalArgumentException if {@code url} is null or blank
   * @throws IOException              if the request fails
   */
  public FetchedPage parsePage(String url) throws IOException {
    Document doc = parseURLToHTML(url);
    return new FetchedPage(pickTitle(doc), pickBody(doc));
  }

  /**
   * Fetches the URL and parses the response into a {@link Document}.
   *
   * @param url HTTP(S) URL; must not be null or blank
   * @throws IllegalArgumentException if {@code url} is null or blank
   * @throws IOException              if the request fails (network, HTTP error
   *                                  surfaced by Jsoup, etc.)
   */
  private Document parseURLToHTML(String url) throws IOException {
    if (url == null || url.isBlank()) {
      throw new IllegalArgumentException("url must not be blank");
    }
    return Jsoup.connect(url.trim())
        .userAgent(USER_AGENT)
        .timeout(TIMEOUT_MS)
        .followRedirects(true)
        .get();
  }

  /**
   * Resolves title in order: {@code og:title}, {@code twitter:title}, then {@code document.title()}.
   * Whitespace is normalized to a single line; result is truncated to {@link #MAX_TITLE_LEN}.
   */
  private String pickTitle(Document doc) {
    if (doc == null) {
      return "";
    }
    String raw = firstNonBlank(
        metaContentBySelector(doc, "meta[property='og:title']"),
        metaContentBySelector(doc, "meta[name='twitter:title']"),
        safeTrim(doc.title()));
    return truncateTitle(singleLine(raw));
  }

  private static String metaContentBySelector(Document doc, String cssQuery) {
    Element el = doc.selectFirst(cssQuery);
    if (el == null) {
      return "";
    }
    return safeTrim(el.attr("content"));
  }

  private static String firstNonBlank(String a, String b, String c) {
    if (a != null && !a.isBlank()) {
      return a;
    }
    if (b != null && !b.isBlank()) {
      return b;
    }
    return c == null ? "" : c;
  }

  private static String safeTrim(String s) {
    return s == null ? "" : s.trim();
  }

  private static String singleLine(String s) {
    return s.replaceAll("\\s+", " ").trim();
  }

  private static String truncateTitle(String s) {
    if (s.length() <= MAX_TITLE_LEN) {
      return s;
    }
    return s.substring(0, MAX_TITLE_LEN);
  }

  /**
   * Extracts article-like plain text: {@code article}, {@code main}, then {@code [role=main]};
   * joins {@code p} text inside that root. Falls back to the longest {@code div}/{@code section}
   * text, then {@code body} paragraphs.
   */
  private String pickBody(Document doc) {
    if (doc == null) {
      return "";
    }
    Element article = doc.selectFirst("article");
    if (hasSubstantialText(article)) {
      return formatBody(joinParagraphTexts(article));
    }
    Element main = doc.selectFirst("main");
    if (hasSubstantialText(main)) {
      return formatBody(joinParagraphTexts(main));
    }
    Element roleMain = doc.selectFirst("[role=main]");
    if (hasSubstantialText(roleMain)) {
      return formatBody(joinParagraphTexts(roleMain));
    }
    String fromBlocks = longestMeaningfulBlockText(doc);
    if (!fromBlocks.isBlank()) {
      return formatBody(fromBlocks);
    }
    Element body = doc.body();
    if (body != null) {
      return formatBody(joinParagraphTexts(body));
    }
    return "";
  }

  /**
   * Normalizes whitespace to a single line and truncates to {@link #MAX_BODY_LEN}.
   *
   * @param raw extracted text; may be null
   */
  private String formatBody(String raw) {
    if (raw == null || raw.isBlank()) {
      return "";
    }
    return truncateBody(singleLine(raw));
  }

  private static boolean hasSubstantialText(Element el) {
    return el != null && el.text().trim().length() >= MIN_ROOT_TEXT_LEN;
  }

  private static String joinParagraphTexts(Element root) {
    return root.select("p").stream()
        .map(Element::text)
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .reduce((a, b) -> a + " " + b)
        .orElse("");
  }

  private static String longestMeaningfulBlockText(Document doc) {
    return doc.select("div, section").stream()
        .map(Element::text)
        .map(String::trim)
        .filter(s -> s.length() > 80)
        .max(Comparator.comparingInt(String::length))
        .orElse("");
  }

  private static String truncateBody(String s) {
    if (s.length() <= MAX_BODY_LEN) {
      return s;
    }
    return s.substring(0, MAX_BODY_LEN);
  }

}
