import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { Home } from './pages/Home'
import { ContentDetail } from './pages/ContentDetail'
import { Login } from './pages/Login'
import { Register } from './pages/Register'
import { CategoryList } from './pages/admin/CategoryList'
import { CategoryForm } from './pages/admin/CategoryForm'
import { ContentList } from './pages/admin/ContentList'
import { ContentForm } from './pages/admin/ContentForm'
import { Layout } from './pages/admin/Layout'

function PrivateRoute({ children }: { children: React.ReactNode }) {
  const token = localStorage.getItem('token')
  return token ? <>{children}</> : <Navigate to="/admin/login" />
}

function AdminRoute({ children }: { children: React.ReactNode }) {
  return (
    <PrivateRoute>
      <Layout>{children}</Layout>
    </PrivateRoute>
  )
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/content/:slug" element={<ContentDetail />} />
        <Route path="/admin/login" element={<Login />} />
        <Route path="/admin/register" element={<Register />} />
        <Route path="/admin" element={<AdminRoute><ContentList /></AdminRoute>} />
        <Route path="/admin/contents" element={<AdminRoute><ContentList /></AdminRoute>} />
        <Route path="/admin/contents/:id" element={<AdminRoute><ContentForm /></AdminRoute>} />
        <Route path="/admin/categories" element={<AdminRoute><CategoryList /></AdminRoute>} />
        <Route path="/admin/categories/:id" element={<AdminRoute><CategoryForm /></AdminRoute>} />
      </Routes>
    </BrowserRouter>
  )
}

export default App