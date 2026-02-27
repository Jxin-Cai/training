import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import DOMPurify from 'dompurify';
import { useContents } from '../contexts/ContentContext';
import { Content } from '../services/api';

export default function ContentDetailPage() {
  const { id } = useParams<{ id: string }>();
  const { getContent } = useContents();
  const [content, setContent] = useState<Content | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!id) return;
    
    const load = async () => {
      try {
        const data = await getContent(parseInt(id));
        setContent(data);
      } catch (e) {
        setError('内容不存在');
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [id, getContent]);

  if (loading) {
    return (
      <div className="container">
        <div className="loading">加载中...</div>
      </div>
    );
  }

  if (error || !content) {
    return (
      <>
        <header className="header">
          <div className="header-content">
            <Link to="/" className="logo">CMS 内容管理系统</Link>
            <nav className="nav">
              <Link to="/">首页</Link>
              <Link to="/admin/categories">分类管理</Link>
              <Link to="/admin/contents">内容管理</Link>
            </nav>
          </div>
        </header>
        <main className="container">
          <div className="empty-state">
            <h3>{error || '内容不存在'}</h3>
            <Link to="/" className="btn">返回首页</Link>
          </div>
        </main>
      </>
    );
  }

  const sanitizedHtml = DOMPurify.sanitize(content.html_content);

  return (
    <>
      <header className="header">
        <div className="header-content">
          <Link to="/" className="logo">CMS 内容管理系统</Link>
          <nav className="nav">
            <Link to="/">首页</Link>
            <Link to="/admin/categories">分类管理</Link>
            <Link to="/admin/contents">内容管理</Link>
          </nav>
        </div>
      </header>
      <main className="container">
        <article className="content-detail">
          <h1>{content.title}</h1>
          <div className="meta">
            {content.category_name && (
              <span className="card-category">{content.category_name}</span>
            )}
            {content.published_at && new Date(content.published_at).toLocaleDateString('zh-CN')}
          </div>
          <div className="content-body" dangerouslySetInnerHTML={{ __html: sanitizedHtml }} />
        </article>
      </main>
    </>
  );
}