/** Matches {@code ArticleListItem} JSON from the backend. */
export type ArticleListItem = {
  id: number
  url: string
  title: string | null
  summaryShort: string | null
  fetchedAt: string | null
}

/** Matches {@code PagedArticlesResponse} JSON from the backend. */
export type PagedArticlesResponse = {
  content: ArticleListItem[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

export type CreateArticleResponse = {
  id: number
  url: string
  fetchedAt: string | null
}
