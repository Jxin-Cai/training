import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api, Category } from '../api/client'

export default function AdminCategories() {
  const [categories, setCategories] = useState<Category[]>([])
  const [newName, setNewName] = useState('')
  const [editingId, setEditingId] = useState<number | null>(null)
  const [editName, setEditName] = useState('')
  const navigate = useNavigate()

  const loadCategories = () => {
    api.getCategories().then(setCategories).catch(console.error)
  }

  useEffect(() => {
    const token = localStorage.getItem('token')
    if (!token) {
      navigate('/admin')
      return
    }
    loadCategories()
  }, [navigate])

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      await api.createCategory(newName)
      setNewName('')
      loadCategories()
    } catch (err) {
      alert('Failed to create category')
    }
  }

  const handleDelete = async (id: number) => {
    if (!confirm('Delete this category?')) return
    try {
      await api.deleteCategory(id)
      loadCategories()
    } catch (err) {
      alert('Failed to delete category')
    }
  }

  const handleEdit = (cat: Category) => {
    setEditingId(cat.id)
    setEditName(cat.name)
  }

  const handleSaveEdit = async (id: number) => {
    try {
      await api.updateCategory(id, editName)
      setEditingId(null)
      setEditName('')
      loadCategories()
    } catch (err) {
      alert('Failed to update category')
    }
  }

  const handleCancelEdit = () => {
    setEditingId(null)
    setEditName('')
  }

  return (
    <div className="container">
      <header style={{ padding: '20px 0', borderBottom: '1px solid #eee' }}>
        <Link to="/admin/articles">← Back to Articles</Link>
      </header>
      <main style={{ padding: '20px 0' }}>
        <h1>Categories</h1>
        <form onSubmit={handleCreate} style={{ margin: '20px 0' }}>
          <input
            type="text"
            value={newName}
            onChange={(e) => setNewName(e.target.value)}
            placeholder="Category name"
            style={{ padding: '8px', marginRight: '10px' }}
          />
          <button type="submit" style={{ padding: '8px 16px' }}>Add</button>
        </form>
        <ul style={{ listStyle: 'none', padding: 0 }}>
          {categories.map((cat) => (
            <li key={cat.id} style={{ padding: '10px', borderBottom: '1px solid #eee', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              {editingId === cat.id ? (
                <>
                  <input
                    type="text"
                    value={editName}
                    onChange={(e) => setEditName(e.target.value)}
                    style={{ padding: '4px', marginRight: '10px' }}
                  />
                  <div>
                    <button onClick={() => handleSaveEdit(cat.id)} style={{ marginRight: '5px' }}>Save</button>
                    <button onClick={handleCancelEdit}>Cancel</button>
                  </div>
                </>
              ) : (
                <>
                  <span>{cat.name}</span>
                  <div>
                    <button onClick={() => handleEdit(cat)} style={{ marginRight: '5px' }}>Edit</button>
                    <button onClick={() => handleDelete(cat.id)} style={{ color: 'red' }}>Delete</button>
                  </div>
                </>
              )}
            </li>
          ))}
        </ul>
      </main>
    </div>
  )
}