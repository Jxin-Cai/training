import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api, Article } from '../api/client'

export default function AdminArticles() {
  const [articles, setArticles] = useState<Article[]>([])
  const navigate = useNavigate()

  const loadArticles = () => {
    api.getArticles().then(setArticles).catch(console.error)
  }

  useEffect(() => {
    const token = localStorage.getItem('token')
    if (!token) {
      navigate('/admin')
      return
    }
    loadArticles()
  }, [navigate])

  const handleDelete = async (id: number) => {
    if (!confirm('Delete this article?')) return
    try {
      await api.deleteArticle(id)
      loadArticles()
    } catch (err) {
      alert('Failed to delete article')
    }
  }

  const toggleStatus = async (article: Article) => {
    const newStatus = article.status === 'published' ? 'draft' : 'published'
    try {
      await api.updateArticle(article.id, { 
        title: article.title, 
        content_md: article.content_md, 
        content_html: article.content_html,
        category_id: article.category_id, 
        status: newStatus 
      })
      loadArticles()
    } catch (err) {
      alert('Failed to update status')
    }
  }

  return (
    <div className="container">
      <header style={{ padding: '20px 0', borderBottom: '1px solid #eee', display: 'flex', justifyContent: 'space-between' }}>
        <Link to="/">← Back</Link>
        <nav>
          <Link to="/admin/categories" style={{ marginRight: '20px' }}>Categories</Link>
          <Link to="/admin/articles/new">New Article</Link>
        </nav>
      </header>
      <main style={{ padding: '20px 0' }}>
        <h1>Articles</h1>
        <ul style={{ listStyle: 'none', padding: 0 }}>
          {articles.map((article) => (
            <li key={article.id} style={{ padding: '15px', borderBottom: '1px solid #eee' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <Link to={`/admin/articles/${article.id}/edit`} style={{ fontSize: '18px', fontWeight: 'bold' }}>
                    {article.title}
                  </Link>
                  <p style={{ color: '#666', fontSize: '14px' }}>
                    {article.status} | {new Date(article.created_at).toLocaleDateString()}
                  </p>
                </div>
                <div>
                  <button onClick={() => toggleStatus(article)} style={{ marginRight: '10px' }}>
                    {article.status === 'published' ? 'Unpublish' : 'Publish'}
                  </button>
                  <button onClick={() => handleDelete(article.id)} style={{ color: 'red' }}>Delete</button>
                </div>
              </div>
            </li>
          ))}
        </ul>
      </main>
    </div>
  )
}