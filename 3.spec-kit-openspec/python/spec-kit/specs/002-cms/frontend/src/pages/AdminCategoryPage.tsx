import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useCategories } from '../contexts/CategoryContext';

export default function AdminCategoryPage() {
  const { categories, loading, createCategory, updateCategory, deleteCategory } = useCategories();
  const [showModal, setShowModal] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [name, setName] = useState('');
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    try {
      if (editingId) {
        await updateCategory(editingId, name);
      } else {
        await createCategory(name);
      }
      setShowModal(false);
      setName('');
      setEditingId(null);
    } catch (e) {
      setError(e instanceof Error ? e.message : '操作失败');
    }
  };

  const handleEdit = (id: number, catName: string) => {
    setEditingId(id);
    setName(catName);
    setShowModal(true);
  };

  const handleDelete = async (id: number) => {
    if (!confirm('确定要删除这个分类吗？')) return;
    try {
      await deleteCategory(id);
    } catch (e) {
      alert(e instanceof Error ? e.message : '删除失败');
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
          <h1>分类管理</h1>
          <button
            className="btn"
            onClick={() => { setEditingId(null); setName(''); setShowModal(true); }}
          >
            新增分类
          </button>
        </div>

        {categories.length === 0 ? (
          <div className="empty-state">
            <h3>暂无分类</h3>
            <p>点击上方按钮创建分类</p>
          </div>
        ) : (
          <table className="table">
            <thead>
              <tr>
                <th>ID</th>
                <th>名称</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              {categories.map((cat) => (
                <tr key={cat.id}>
                  <td>{cat.id}</td>
                  <td>{cat.name}</td>
                  <td className="actions">
                    <button className="btn btn-sm btn-secondary" onClick={() => handleEdit(cat.id, cat.name)}>
                      编辑
                    </button>
                    <button className="btn btn-sm btn-danger" onClick={() => handleDelete(cat.id)}>
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
            <div className="modal" onClick={(e) => e.stopPropagation()}>
              <h2>{editingId ? '编辑分类' : '新增分类'}</h2>
              <form onSubmit={handleSubmit}>
                <div className="form-group">
                  <label>分类名称</label>
                  <input
                    type="text"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    placeholder="请输入分类名称"
                    required
                  />
                </div>
                {error && <p style={{ color: 'red', marginBottom: '12px' }}>{error}</p>}
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