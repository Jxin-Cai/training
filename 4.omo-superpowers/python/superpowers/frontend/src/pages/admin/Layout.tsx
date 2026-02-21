import { Link, useNavigate } from 'react-router-dom'

export function Layout({ children }: { children: React.ReactNode }) {
  const navigate = useNavigate()

  const handleLogout = () => {
    localStorage.removeItem('token')
    navigate('/admin/login')
  }

  return (
    <div>
      <nav style={{ background: '#333', padding: 15, display: 'flex', gap: 20 }}>
        <Link to="/admin" style={{ color: 'white', textDecoration: 'none' }}>Contents</Link>
        <Link to="/admin/categories" style={{ color: 'white', textDecoration: 'none' }}>Categories</Link>
        <button onClick={handleLogout} style={{ marginLeft: 'auto', background: 'none', border: 'none', color: 'white', cursor: 'pointer' }}>Logout</button>
      </nav>
      {children}
    </div>
  )
}