import type { CreateArticleResponse, PagedArticlesResponse } from './types'

const API_BASE = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

function articlesUrl(q: string, page: number, size: number): string {
  const params = new URLSearchParams()
  if (q.trim()) params.set('q', q.trim())
  params.set('page', String(page))
  params.set('size', String(size))
  return `${API_BASE}/api/articles?${params}`
}

export async function fetchArticles(
  q: string,
  page: number,
  size: number,
): Promise<PagedArticlesResponse> {
  const res = await fetch(articlesUrl(q, page, size))
  if (!res.ok) {
    const text = await res.text()
    throw new Error(text || `HTTP ${res.status}`)
  }
  return res.json() as Promise<PagedArticlesResponse>
}

export async function createArticle(url: string): Promise<CreateArticleResponse> {
  const res = await fetch(`${API_BASE}/api/articles`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ url }),
  })
  if (res.status === 409) {
    throw new Error('この URL はすでに登録されています。')
  }
  if (!res.ok) {
    const text = await res.text()
    throw new Error(text || `HTTP ${res.status}`)
  }
  return res.json() as Promise<CreateArticleResponse>
}
