import { useEffect, useState, type FormEvent } from 'react'
import { createArticle, fetchArticles } from './api/articles'
import type { ArticleListItem, PagedArticlesResponse } from './api/types'
import './App.css'

const PAGE_SIZE = 20

function ArticleCard({ item }: { item: ArticleListItem }) {
  const title = item.title?.trim() || '(No Title)'
  const summary = item.summaryShort?.trim() || '—'

  return (
    <article className="card">
      <h2 className="card-title">
        <a
          href={item.url}
          target="_blank"
          rel="noopener noreferrer"
          className="card-link"
        >
          {title}
        </a>
      </h2>
      <p className="card-summary">{summary}</p>
      <p className="card-meta">
        <a
          href={item.url}
          target="_blank"
          rel="noopener noreferrer"
          className="card-url"
        >
          {item.url}
        </a>
      </p>
    </article>
  )
}

export default function App() {
  const [draftQuery, setDraftQuery] = useState('')
  const [submittedQuery, setSubmittedQuery] = useState('')
  const [data, setData] = useState<PagedArticlesResponse | null>(null)
  const [loading, setLoading] = useState(true)
  const [listError, setListError] = useState<string | null>(null)

  const [registerUrl, setRegisterUrl] = useState('')
  const [registerBusy, setRegisterBusy] = useState(false)
  const [registerMessage, setRegisterMessage] = useState<string | null>(null)

  useEffect(() => {
    let cancelled = false
    fetchArticles(submittedQuery, 0, PAGE_SIZE)
      .then((body) => {
        if (!cancelled) setData(body)
      })
      .catch((e: unknown) => {
        if (!cancelled)
          setListError(e instanceof Error ? e.message : 'Failed to fetch list of articles')
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })
    return () => {
      cancelled = true
    }
  }, [submittedQuery])

  function onSearchSubmit(e: FormEvent<HTMLFormElement>) {
    e.preventDefault()
    setLoading(true)
    setListError(null)
    setSubmittedQuery(draftQuery)
  }

  async function onRegisterSubmit(e: FormEvent<HTMLFormElement>) {
    e.preventDefault()
    setRegisterMessage(null)
    setRegisterBusy(true)
    try {
      await createArticle(registerUrl.trim())
      setRegisterUrl('')
      setRegisterMessage('Successfully registered.')
      const fresh = await fetchArticles(submittedQuery, 0, PAGE_SIZE)
      setData(fresh)
    } catch (e: unknown) {
      setRegisterMessage(e instanceof Error ? e.message : 'Failed to register')
    } finally {
      setRegisterBusy(false)
    }
  }

  return (
    <div className="app">
      <header className="header">
        <h1 className="title">News curator</h1>
      </header>

      <section className="panel" aria-label="Adding article by URL">
        <form className="row" onSubmit={onRegisterSubmit}>
          <label className="label" htmlFor="register-url">
            URL
          </label>
          <input
            id="register-url"
            className="input"
            type="url"
            placeholder="https://…"
            value={registerUrl}
            onChange={(e) => setRegisterUrl(e.target.value)}
            autoComplete="off"
          />
          <button type="submit" className="button" disabled={registerBusy}>
            {registerBusy ? 'Registering...' : 'Register'}
          </button>
        </form>
        {registerMessage ? (
          <p className={`hint ${registerMessage.includes('failed') || registerMessage.includes('already') ? 'hint-error' : ''}`}>
            {registerMessage}
          </p>
        ) : null}
      </section>

      <section className="panel" aria-label="search articles">
        <form className="row" onSubmit={onSearchSubmit}>
          <label className="label" htmlFor="search-q">
            Search
          </label>
          <input
            id="search-q"
            className="input input-grow"
            type="search"
            placeholder="Keyword (full list if empty)"
            value={draftQuery}
            onChange={(e) => setDraftQuery(e.target.value)}
          />
          <button type="submit" className="button">
            Search
          </button>
        </form>
      </section>

      <section className="results" aria-live="polite">
        {loading ? <p className="muted">Loading...</p> : null}
        {!loading && listError ? <p className="error">{listError}</p> : null}
        {!loading && !listError && data?.content.length === 0 ? (
          <p className="muted">Articles not found</p>
        ) : null}
        {!loading && !listError && data && data.content.length > 0 ? (
          <>
            <p className="muted meta-line">

              Showing {data.content.length} of {data.totalElements}, page {data.number + 1} of {Math.max(1, data.totalPages)}）
            </p>
            <ul className="list">
              {data.content.map((item) => (
                <li key={item.id}>
                  <ArticleCard item={item} />
                </li>
              ))}
            </ul>
          </>
        ) : null}
      </section>
    </div>
  )
}
