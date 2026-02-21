import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { api } from '../api'

interface Content {
  id: number
  title: string
  html_content: string | null
  created_at: string
}

export function ContentDetail() {
  const { slug } = useParams<{ slug: string }>()
  const [content, setContent] = useState<Content | null>(null)

  useEffect(() => {
    if (slug) {
      api.getContentBySlug(slug).then(setContent).catch(console.error)
    }
  }, [slug])

  if (!content) return <div className="container">Loading...</div>

  return (
    <div className="container">
      <Link to="/">← Back</Link>
      <h1>{content.title}</h1>
      <p style={{ color: '#666' }}>{new Date(content.created_at).toLocaleDateString()}</p>
      <div 
        style={{ marginTop: 20 }}
        dangerouslySetInnerHTML={{ __html: content.html_content || '' }}
      />
    </div>
  )
}