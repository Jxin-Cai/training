import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../api'

export function Login() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const navigate = useNavigate()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      const { access_token } = await api.login(username, password)
      localStorage.setItem('token', access_token)
      navigate('/admin')
    } catch (err) {
      alert('Login failed')
    }
  }

  return (
    <div className="container" style={{ maxWidth: 400, marginTop: 100 }}>
      <h1>Login</h1>
      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: 10 }}>
          <input 
            placeholder="Username" 
            value={username}
            onChange={e => setUsername(e.target.value)}
          />
        </div>
        <div style={{ marginBottom: 10 }}>
          <input 
            type="password" 
            placeholder="Password" 
            value={password}
            onChange={e => setPassword(e.target.value)}
          />
        </div>
        <button className="btn btn-primary" type="submit">Login</button>
      </form>
    </div>
  )
}