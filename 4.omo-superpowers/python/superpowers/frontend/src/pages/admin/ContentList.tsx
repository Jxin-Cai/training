import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../../api'

interface Content {
  id: number
  title: string
  slug: string
  status: string
}

export function ContentList() {
  const [contents, setContents] = useState<Content[]>([])

  useEffect(() => {
    loadContents()
  }, [])

  const loadContents = () => {
    api.getContents().then(setContents).catch(console.error)
  }

  const handleDelete = async (id: number) => {
    if (confirm('Delete this content?')) {
      await api.deleteContent(id)
      loadContents()
    }
  }

  const handlePublish = async (id: number) => {
    await api.publishContent(id)
    loadContents()
  }

  const handleUnpublish = async (id: number) => {
    await api.unpublishContent(id)
    loadContents()
  }

  return (
    <div className="container">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h1>Contents</h1>
        <Link to="/admin/contents/new" className="btn btn-primary">Add Content</Link>
      </div>
      <table style={{ marginTop: 20 }}>
        <thead>
          <tr>
            <th>Title</th>
            <th>Slug</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {contents.map(content => (
            <tr key={content.id}>
              <td>{content.title}</td>
              <td>{content.slug}</td>
              <td>
                <span style={{ 
                  padding: '4px 8px', 
                  borderRadius: 4,
                  background: content.status === 'published' ? '#28a745' : '#6c757d',
                  color: 'white'
                }}>
                  {content.status}
                </span>
              </td>
              <td>
                <Link to={`/admin/contents/${content.id}/edit`} className="btn" style={{ marginRight: 10 }}>Edit</Link>
                {content.status === 'draft' ? (
                  <button className="btn btn-primary" onClick={() => handlePublish(content.id)} style={{ marginRight: 10 }}>Publish</button>
                ) : (
                  <button className="btn" onClick={() => handleUnpublish(content.id)} style={{ marginRight: 10 }}>Unpublish</button>
                )}
                <button className="btn btn-danger" onClick={() => handleDelete(content.id)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}