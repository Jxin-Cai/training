import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { api, type Article } from '../api/client'
import DOMPurify from 'dompurify'

export default function Article() {
  const { id } = useParams<{ id: string }>()
  const [article, setArticle] = useState<Article | null>(null)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (id) {
      // Use getPublishedArticle to only fetch published content
      api.getPublishedArticle(parseInt(id))
        .then(setArticle)
        .catch((err) => {
          console.error(err)
          setError('Article not found or not published')
        })
    }
  }, [id])

  if (error) return (
    <div className="container">
      <header style={{ padding: '20px 0', borderBottom: '1px solid #eee' }}>
        <Link to="/">← Back</Link>
      </header>
      <main style={{ padding: '20px 0' }}>
        <p style={{ color: 'red' }}>{error}</p>
      </main>
    </div>
  )

  if (!article) return <div className="container">Loading...</div>

  // Sanitize HTML as defense-in-depth (backend also sanitizes)
  const safeHtml = DOMPurify.sanitize(article.content_html)

  return (
    <div className="container">
      <header style={{ padding: '20px 0', borderBottom: '1px solid #eee' }}>
        <Link to="/">← Back</Link>
      </header>
      <main style={{ padding: '20px 0' }}>
        <h1>{article.title}</h1>
        <p style={{ color: '#666', fontSize: '14px' }}>
          {article.published_at 
            ? `Published: ${new Date(article.published_at).toLocaleDateString()}`
            : `Created: ${new Date(article.created_at).toLocaleDateString()}`
          }
        </p>
        <article 
          style={{ marginTop: '20px' }}
          dangerouslySetInnerHTML={{ __html: safeHtml }}
        />
      </main>
    </div>
  )
}