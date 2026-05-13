import { z } from 'zod'
import {
  CreateArticleResponseSchema,
  PagedArticlesResponseSchema,
  type CreateArticleResponse,
  type PagedArticlesResponse,
} from './schemas'

const API_BASE = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

function articlesUrl(q: string, page: number, size: number): string {
  const params = new URLSearchParams()
  if (q.trim()) params.set('q', q.trim())
  params.set('page', String(page))
  params.set('size', String(size))
  return `${API_BASE}/api/articles?${params}`
}

function parseJson<T>(schema: z.ZodType<T>, raw: unknown, label: string): T {
  const result = schema.safeParse(raw)
  if (!result.success) {
    throw new Error(`${label}: ${result.error.message}`)
  }
  return result.data
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
  const raw: unknown = await res.json()
  return parseJson(PagedArticlesResponseSchema, raw, 'Response for listing articles')
}

export async function createArticle(url: string): Promise<CreateArticleResponse> {
  const res = await fetch(`${API_BASE}/api/articles`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ url }),
  })
  if (res.status === 409) {
    throw new Error('URL already registered')
  }
  if (!res.ok) {
    const text = await res.text()
    throw new Error(text || `HTTP ${res.status}`)
  }
  const raw: unknown = await res.json()
  return parseJson(CreateArticleResponseSchema, raw, 'Response for creating an article')
}
