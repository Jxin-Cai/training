import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { api, type Article } from '../api/client'

export default function Article() {
  const { id } = useParams<{ id: string }>()
  const [article, setArticle] = useState<Article | null>(null)

  useEffect(() => {
    if (id) {
      api.getArticle(parseInt(id)).then(setArticle).catch(console.error)
    }
  }, [id])

  if (!article) return <div className="container">Loading...</div>

  return (
    <div className="container">
      <header style={{ padding: '20px 0', borderBottom: '1px solid #eee' }}>
        <Link to="/">← Back</Link>
      </header>
      <main style={{ padding: '20px 0' }}>
        <h1>{article.title}</h1>
        <p style={{ color: '#666', fontSize: '14px' }}>
          {new Date(article.created_at).toLocaleDateString()}
        </p>
        <article 
          style={{ marginTop: '20px' }}
          dangerouslySetInnerHTML={{ __html: article.content_html }}
        />
      </main>
    </div>
  )
}