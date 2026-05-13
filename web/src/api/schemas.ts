import { z } from 'zod'

export const ArticleListItemSchema = z.object({
  id: z.number(),
  url: z.string(),
  title: z.string().nullable(),
  summaryShort: z.string().nullable(),
  fetchedAt: z.string().nullable(),
})

export const PagedArticlesResponseSchema = z.object({
  content: z.array(ArticleListItemSchema),
  totalElements: z.number(),
  totalPages: z.number(),
  number: z.number(),
  size: z.number(),
})

export const CreateArticleResponseSchema = z.object({
  id: z.number(),
  url: z.string(),
  fetchedAt: z.string().nullable(),
})

export type ArticleListItem = z.infer<typeof ArticleListItemSchema>
export type PagedArticlesResponse = z.infer<typeof PagedArticlesResponseSchema>
export type CreateArticleResponse = z.infer<typeof CreateArticleResponseSchema>
