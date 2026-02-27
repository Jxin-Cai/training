import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Home from './pages/Home'
import Article from './pages/Article'
import AdminLogin from './pages/AdminLogin'
import AdminCategories from './pages/AdminCategories'
import AdminArticles from './pages/AdminArticles'
import AdminArticleEdit from './pages/AdminArticleEdit'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/article/:id" element={<Article />} />
        <Route path="/admin" element={<AdminLogin />} />
        <Route path="/admin/categories" element={<AdminCategories />} />
        <Route path="/admin/articles" element={<AdminArticles />} />
        <Route path="/admin/articles/new" element={<AdminArticleEdit />} />
        <Route path="/admin/articles/:id/edit" element={<AdminArticleEdit />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App