import { useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useContents } from '../contexts/ContentContext';

export default function HomePage() {
  const { contents, loading, loadContents } = useContents();

  useEffect(() => {
    loadContents();
  }, []);

  if (loading) {
    return (
      <div className="container">
        <div className="loading">加载中...</div>
      </div>
    );
  }

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
        <h1 style={{ marginBottom: '20px' }}>最新内容</h1>
        {contents.length === 0 ? (
          <div className="empty-state">
            <h3>暂无内容</h3>
            <p>请在后台创建内容并发布</p>
          </div>
        ) : (
          <div className="content-grid">
            {contents.map((content) => (
              <div key={content.id} className="card">
                <h2 className="card-title">
                  <Link to={`/content/${content.id}`}>{content.title}</Link>
                </h2>
                <div className="card-meta">
                  {content.category_name && (
                    <span className="card-category">{content.category_name}</span>
                  )}
                  {content.published_at && new Date(content.published_at).toLocaleDateString('zh-CN')}
                </div>
                <Link to={`/content/${content.id}`} className="btn btn-sm">
                  阅读全文
                </Link>
              </div>
            ))}
          </div>
        )}
      </main>
    </>
  );
}