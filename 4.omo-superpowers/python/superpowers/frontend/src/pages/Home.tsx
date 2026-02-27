import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api, Article } from '../api/client'

export default function Home() {
  const [articles, setArticles] = useState<Article[]>([])

  useEffect(() => {
    api.getPublishedArticles().then(setArticles).catch(console.error)
  }, [])

  return (
    <div className="container">
      <header style={{ padding: '20px 0', borderBottom: '1px solid #eee' }}>
        <h1><Link to="/">CMS</Link></h1>
      </header>
      <main style={{ padding: '20px 0' }}>
        <h2>Articles</h2>
        <ul style={{ listStyle: 'none', padding: 0 }}>
          {articles.map((article) => (
            <li key={article.id} style={{ padding: '15px 0', borderBottom: '1px solid #eee' }}>
              <Link to={`/article/${article.id}`} style={{ fontSize: '18px', fontWeight: 'bold' }}>
                {article.title}
              </Link>
              <p style={{ color: '#666', fontSize: '14px', marginTop: '5px' }}>
                {new Date(article.created_at).toLocaleDateString()}
              </p>
            </li>
          ))}
        </ul>
      </main>
    </div>
  )
}