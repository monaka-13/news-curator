CREATE INDEX idx_articles_title_body_fts
  ON articles
  USING gin (
    to_tsvector('simple', coalesce(title, '') || ' ' || coalesce(body, ''))
  );