import { useEffect, useState, useRef } from 'react';
import { Link } from 'react-router-dom';
import MDEditor from '@uiw/react-md-editor';
import { useContents } from '../contexts/ContentContext';
import { useCategories } from '../contexts/CategoryContext';
import { Content } from '../services/api';
import { api } from '../services/api';

export default function AdminContentPage() {
  const { contents, loading, loadAllContents, deleteContent, createContent, updateContent } = useContents();
  const { categories } = useCategories();
  const [showModal, setShowModal] = useState(false);
  const [editingContent, setEditingContent] = useState<Content | null>(null);
  const [formData, setFormData] = useState({
    title: '',
    category_id: 0,
    markdown_content: '',
    status: 'draft',
  });
  const fileInputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    loadAllContents();
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingContent) {
        await updateContent(editingContent.id, formData);
      } else {
        await createContent(formData);
      }
      setShowModal(false);
      setEditingContent(null);
      setFormData({ title: '', category_id: 0, markdown_content: '', status: 'draft' });
      loadAllContents();
    } catch (e) {
      alert(e instanceof Error ? e.message : '操作失败');
    }
  };

  const handleEdit = (content: Content) => {
    setEditingContent(content);
    setFormData({
      title: content.title,
      category_id: content.category_id,
      markdown_content: content.markdown_content,
      status: content.status,
    });
    setShowModal(true);
  };

  const handleDelete = async (id: number) => {
    if (!confirm('确定要删除这篇内容吗？')) return;
    try {
      await deleteContent(id);
      loadAllContents();
    } catch (e) {
      alert(e instanceof Error ? e.message : '删除失败');
    }
  };

  const handleNew = () => {
    setEditingContent(null);
    setFormData({ title: '', category_id: categories[0]?.id || 0, markdown_content: '', status: 'draft' });
    setShowModal(true);
  };

  const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    try {
      const result = await api.contents.upload(file);
      setFormData((prev) => ({ ...prev, markdown_content: prev.markdown_content + result.content }));
    } catch (e) {
      alert('文件上传失败');
    }
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

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
        <div className="admin-header">
          <h1>内容管理</h1>
          <button className="btn" onClick={handleNew} disabled={categories.length === 0}>
            新建内容
          </button>
        </div>

        {categories.length === 0 && (
          <div className="empty-state" style={{ marginBottom: '20px' }}>
            <p>请先创建分类</p>
            <Link to="/admin/categories" className="btn">前往创建分类</Link>
          </div>
        )}

        {contents.length === 0 ? (
          <div className="empty-state">
            <h3>暂无内容</h3>
            <p>点击上方按钮创建内容</p>
          </div>
        ) : (
          <table className="table">
            <thead>
              <tr>
                <th>ID</th>
                <th>标题</th>
                <th>分类</th>
                <th>状态</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              {contents.map((content) => (
                <tr key={content.id}>
                  <td>{content.id}</td>
                  <td>{content.title}</td>
                  <td>{content.category_name}</td>
                  <td>
                    <span className={`card-category`} style={{ background: content.status === 'published' ? '#4caf50' : '#ff9800', color: '#fff' }}>
                      {content.status === 'published' ? '已发布' : '草稿'}
                    </span>
                  </td>
                  <td className="actions">
                    <button className="btn btn-sm btn-secondary" onClick={() => handleEdit(content)}>
                      编辑
                    </button>
                    <button className="btn btn-sm btn-danger" onClick={() => handleDelete(content.id)}>
                      删除
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}

        {showModal && (
          <div className="modal-overlay" onClick={() => setShowModal(false)}>
            <div className="modal" style={{ maxWidth: '800px', maxHeight: '90vh', overflow: 'auto' }} onClick={(e) => e.stopPropagation()}>
              <h2>{editingContent ? '编辑内容' : '新建内容'}</h2>
              <form onSubmit={handleSubmit}>
                <div className="form-group">
                  <label>标题</label>
                  <input
                    type="text"
                    value={formData.title}
                    onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                    placeholder="请输入标题"
                    required
                  />
                </div>
                <div className="form-group">
                  <label>分类</label>
                  <select
                    value={formData.category_id}
                    onChange={(e) => setFormData({ ...formData, category_id: parseInt(e.target.value) })}
                    required
                  >
                    <option value="">请选择分类</option>
                    {categories.map((cat) => (
                      <option key={cat.id} value={cat.id}>{cat.name}</option>
                    ))}
                  </select>
                </div>
                <div className="form-group">
                  <label>状态</label>
                  <select
                    value={formData.status}
                    onChange={(e) => setFormData({ ...formData, status: e.target.value })}
                  >
                    <option value="draft">草稿</option>
                    <option value="published">发布</option>
                  </select>
                </div>
                <div className="form-group">
                  <label>内容 (Markdown)</label>
                  <div style={{ display: 'flex', gap: '8px', marginBottom: '8px' }}>
                    <input
                      type="file"
                      ref={fileInputRef}
                      accept=".md"
                      onChange={handleFileUpload}
                      style={{ display: 'none' }}
                    />
                    <button type="button" className="btn btn-sm btn-secondary" onClick={() => fileInputRef.current?.click()}>
                      上传 MD 文件
                    </button>
                  </div>
                  <div data-color-mode="light">
                    <MDEditor
                      value={formData.markdown_content}
                      onChange={(value) => setFormData({ ...formData, markdown_content: value || '' })}
                      height={300}
                    />
                  </div>
                </div>
                <div className="modal-actions">
                  <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>
                    取消
                  </button>
                  <button type="submit" className="btn">
                    保存
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
      </main>
    </>
  );
}