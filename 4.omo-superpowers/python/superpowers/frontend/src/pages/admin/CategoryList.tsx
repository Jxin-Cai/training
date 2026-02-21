import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../../api'

interface Category {
  id: number
  name: string
  slug: string
  sort_order: number
}

export function CategoryList() {
  const [categories, setCategories] = useState<Category[]>([])

  useEffect(() => {
    loadCategories()
  }, [])

  const loadCategories = () => {
    api.getCategories().then(setCategories).catch(console.error)
  }

  const handleDelete = async (id: number) => {
    if (confirm('Delete this category?')) {
      await api.deleteCategory(id)
      loadCategories()
    }
  }

  return (
    <div className="container">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h1>Categories</h1>
        <Link to="/admin/categories/new" className="btn btn-primary">Add Category</Link>
      </div>
      <table style={{ marginTop: 20 }}>
        <thead>
          <tr>
            <th>Name</th>
            <th>Slug</th>
            <th>Sort Order</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {categories.map(cat => (
            <tr key={cat.id}>
              <td>{cat.name}</td>
              <td>{cat.slug}</td>
              <td>{cat.sort_order}</td>
              <td>
                <Link to={`/admin/categories/${cat.id}/edit`} className="btn" style={{ marginRight: 10 }}>Edit</Link>
                <button className="btn btn-danger" onClick={() => handleDelete(cat.id)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}