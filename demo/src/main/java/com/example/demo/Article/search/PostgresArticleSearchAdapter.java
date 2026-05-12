package com.example.demo.Article.search;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.example.demo.Article.dto.ArticleListItem;
import com.example.demo.Article.dto.PagedArticlesResponse;

/**
 * {@link ArticleSearchPort} backed by PostgreSQL full-text search on {@code title} and {@code body}.
 * Uses the same {@code to_tsvector} expression as {@code V2__create_index_for_GIN.sql} so the GIN index applies.
 */
@Component
public class PostgresArticleSearchAdapter implements ArticleSearchPort {

  static final int DEFAULT_PAGE_SIZE = 20;
  static final int MAX_PAGE_SIZE = 100;

  private static final String TS_VECTOR_EXPR =
      "to_tsvector('simple', coalesce(title, '') || ' ' || coalesce(body, ''))";

  private static final RowMapper<ArticleListItem> ROW_MAPPER = new RowMapper<>() {
    @Override
    public ArticleListItem mapRow(ResultSet rs, int rowNum) throws SQLException {
      return new ArticleListItem(
          rs.getLong("id"),
          rs.getString("url"),
          rs.getString("title"),
          rs.getString("summary_short"),
          rs.getTimestamp("fetched_at").toInstant());
    }
  };

  private final NamedParameterJdbcTemplate jdbc;

  public PostgresArticleSearchAdapter(NamedParameterJdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Override
  public PagedArticlesResponse search(String q, int page, int size) {
    if (page < 0) {
      throw new IllegalArgumentException("page must be >= 0");
    }
    int pageSize = size < 1 ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);
    long offset = (long) page * pageSize;

    String trimmed = q == null ? "" : q.trim();
    if (trimmed.isEmpty()) {
      return listAll(page, pageSize, offset);
    }
    return listWithKeyword(trimmed, page, pageSize, offset);
  }

  private PagedArticlesResponse listAll(int page, int pageSize, long offset) {
    String orderBy = """
        ORDER BY COALESCE(published_at, fetched_at) DESC,
                 fetched_at DESC
        """;

    Long total = jdbc.queryForObject("SELECT COUNT(*) FROM articles", Map.of(), Long.class);
    long totalElements = total != null ? total : 0L;

    String select =
        """
        SELECT id, url, title, summary_short, fetched_at
        FROM articles
        """
            + orderBy
            + " LIMIT :limit OFFSET :offset";

    List<ArticleListItem> content =
        jdbc.query(
            select,
            new MapSqlParameterSource("limit", pageSize).addValue("offset", offset),
            ROW_MAPPER);

    return toResponse(content, totalElements, page, pageSize);
  }

  private PagedArticlesResponse listWithKeyword(String keyword, int page, int pageSize, long offset) {
    String where = "WHERE " + TS_VECTOR_EXPR + " @@ plainto_tsquery('simple', :q)";
    String orderBy =
        """
        ORDER BY ts_rank(
                   """
            + TS_VECTOR_EXPR
            + ", plainto_tsquery('simple', :q)) DESC,\n"
            + "         COALESCE(published_at, fetched_at) DESC,\n"
            + "         fetched_at DESC\n";

    MapSqlParameterSource params =
        new MapSqlParameterSource("q", keyword).addValue("limit", pageSize).addValue("offset", offset);

    Long total =
        jdbc.queryForObject("SELECT COUNT(*) FROM articles " + where, params, Long.class);
    long totalElements = total != null ? total : 0L;

    String select =
        """
        SELECT id, url, title, summary_short, fetched_at
        FROM articles
        """
            + where
            + "\n"
            + orderBy
            + "LIMIT :limit OFFSET :offset";

    List<ArticleListItem> content = jdbc.query(select, params, ROW_MAPPER);

    return toResponse(content, totalElements, page, pageSize);
  }

  private static PagedArticlesResponse toResponse(
      List<ArticleListItem> content, long totalElements, int page, int pageSize) {
    int totalPages =
        pageSize == 0 ? 0 : (int) Math.ceil((double) totalElements / (double) pageSize);
    return new PagedArticlesResponse(content, totalElements, totalPages, page, pageSize);
  }
}
