import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api'

interface Content {
  id: number
  title: string
  slug: string
  status: string
  published_at: string | null
}

export function Home() {
  const [contents, setContents] = useState<Content[]>([])

  useEffect(() => {
    api.getPublishedContents().then(setContents).catch(console.error)
  }, [])

  return (
    <div className="container">
      <h1>Content List</h1>
      <div style={{ marginTop: 20 }}>
        {contents.map(content => (
          <div key={content.id} style={{ padding: 20, border: '1px solid #ddd', marginBottom: 10 }}>
            <h2><Link to={`/content/${content.slug}`}>{content.title}</Link></h2>
            <p style={{ color: '#666' }}>
              {content.published_at ? new Date(content.published_at).toLocaleDateString() : ''}
            </p>
          </div>
        ))}
      </div>
    </div>
  )
}