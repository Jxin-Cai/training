import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { CategoryProvider } from './contexts/CategoryContext'
import { ContentProvider } from './contexts/ContentContext'
import HomePage from './pages/HomePage'
import ContentDetailPage from './pages/ContentDetailPage'
import AdminCategoryPage from './pages/AdminCategoryPage'
import AdminContentPage from './pages/AdminContentPage'

function App() {
  return (
    <BrowserRouter>
      <CategoryProvider>
        <ContentProvider>
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/content/:id" element={<ContentDetailPage />} />
            <Route path="/admin/categories" element={<AdminCategoryPage />} />
            <Route path="/admin/contents" element={<AdminContentPage />} />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </ContentProvider>
      </CategoryProvider>
    </BrowserRouter>
  )
}

export default App