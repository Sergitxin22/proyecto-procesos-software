import { useState } from "react"
import "./App.css"

function App() {
  const [token, setToken] = useState(null)
  const [message, setMessage] = useState("")

  const [regForm, setRegForm] = useState({ nombre: "", email: "", password: "" })
  const [loginForm, setLoginForm] = useState({ email: "", password: "" })

  const API_URL = "http://localhost:8080/api/auth"

  const showMessage = (msg) => {
    setMessage(msg)
    setTimeout(() => setMessage(''), 5000)
  }

  const handleRegister = async (e) => {
    e.preventDefault()
    try {
      const res = await fetch(`${API_URL}/registro`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(regForm)
      })
      const data = await res.json()
      if (res.ok) {
        showMessage(`Registro exitoso: ${data.mensaje}`)
        setRegForm({ nombre: "", email: "", password: "" })
      } else showMessage(`Error en registro: ${data.mensaje || "Error"}`)
    } catch (err) { showMessage(`Error de conexión: ${err.message}`) }
  }

  const handleLogin = async (e) => {
    e.preventDefault()
    try {
      const res = await fetch(`${API_URL}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(loginForm)
      })
      const data = await res.json()
      if (res.ok) {
        setToken(data.token)
        showMessage(`Inicio de sesión exitoso`)
      } else showMessage(`Error en login: ${data.mensaje || "Error"}`)
    } catch (err) { showMessage(`Error de conexión: ${err.message}`) }
  }

  const handleLogout = async () => {
    if (!token) return
    try {
      const res = await fetch(`${API_URL}/logout`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ token })
      })
      const data = await res.json()
      if (res.ok) {
        setToken(null)
        showMessage(`Logout exitoso: ${data.mensaje}`)
        setLoginForm({ email: "", password: "" })
      } else showMessage(`Error en logout: ${data.mensaje || "Error"}`)
    } catch (err) { showMessage(`Error de conexión: ${err.message}`) }
  }

  return (
    <div className="layout-container">
      <header className="header">
        <div className="logo">🎓 Flexilearn</div>
        <h1>Panel de Autenticación</h1>
        <p>Herramienta para probar el API de Spring Boot</p>
      </header>
      
      {message && (
        <div className={`alert ${message.toLowerCase().includes("error") ? "alert-error" : "alert-success"}`}>
          {message}
        </div>
      )}
      
      <div className="card-grid">
        <section className="card">
          <div className="card-header">
            <h2>Nuevo Usuario</h2>
            <p>Crea una cuenta en el sistema.</p>
          </div>
          <form onSubmit={handleRegister} className="form">
            <div className="input-group">
              <label>Nombre completo</label>
              <input placeholder="Ej. María García" value={regForm.nombre} onChange={e => setRegForm({...regForm, nombre: e.target.value})} required />
            </div>
            <div className="input-group">
              <label>Correo Electrónico</label>
              <input type="email" placeholder="correo@ejemplo.com" value={regForm.email} onChange={e => setRegForm({...regForm, email: e.target.value})} required />
            </div>
            <div className="input-group">
              <label>Contraseña</label>
              <input type="password" placeholder="••••••••" value={regForm.password} onChange={e => setRegForm({...regForm, password: e.target.value})} required />
            </div>
            <button type="submit" className="btn btn-primary">Crear cuenta</button>
          </form>
        </section>

        <section className="card">
          <div className="card-header">
            <h2>Iniciar Sesión</h2>
            <p>Accede con tus credenciales.</p>
          </div>
          <form onSubmit={handleLogin} className="form">
            <div className="input-group">
              <label>Correo Electrónico</label>
              <input type="email" placeholder="correo@ejemplo.com" value={loginForm.email} onChange={e => setLoginForm({...loginForm, email: e.target.value})} required />
            </div>
            <div className="input-group">
              <label>Contraseña</label>
              <input type="password" placeholder="••••••••" value={loginForm.password} onChange={e => setLoginForm({...loginForm, password: e.target.value})} required />
            </div>
            <button type="submit" className="btn btn-primary">Iniciar sesión</button>
          </form>
        </section>

        <section className="card status-card">
          <div className="card-header">
            <h2>Estado de Sesión</h2>
            <p>Verifica si estás autenticado.</p>
          </div>
          {token ? (
            <div className="status-active">
              <div className="badge-success">
                <span className="dot"></span> Sesión Activa
              </div>
              <div className="token-container">
                <label>Tu Token de Acceso:</label>
                <textarea readOnly value={token} rows="4" className="token-display"/>
              </div>
              <button onClick={handleLogout} className="btn btn-danger">Cerrar Sesión Segura</button>
            </div>
          ) : (
            <div className="status-inactive">
              <div className="icon-lock">🔒</div>
              <p>Actualmente no tienes un token válido. Inicia sesión para obtener uno.</p>
            </div>
          )}
        </section>
      </div>
    </div>
  )
}

export default App
