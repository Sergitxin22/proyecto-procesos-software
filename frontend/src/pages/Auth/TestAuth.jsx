import { useState } from "react"
import "./TestAuth.css"
import { authService } from "../../services/api.service"

export default function TestAuth() {
  const [token, setToken] = useState(null)
  const [message, setMessage] = useState("")

  const [regForm, setRegForm] = useState({ nombre: "", email: "", password: "" })
  const [loginForm, setLoginForm] = useState({ email: "", password: "" })
  const [userInfo, setUserInfo] = useState(null)
  const [userList, setUserList] = useState(null)

  const showMessage = (msg) => {
    setMessage(msg)
    setTimeout(() => setMessage(''), 5000)
  }

  const handleRegister = async (e) => {
    e.preventDefault()
    try {
      const data = await authService.register(regForm.nombre, regForm.email, regForm.password)
      showMessage(`Registro exitoso: ${data.mensaje || 'OK'}`)
      setRegForm({ nombre: "", email: "", password: "" })
    } catch (err) { showMessage(`Error en registro: ${err.message}`) }
  }

  const handleLogin = async (e) => {
    e.preventDefault()
    try {
      const data = await authService.login(loginForm.email, loginForm.password)
      setToken(data.token)
      localStorage.setItem('token', data.token); // Store token for authService internal usage
      showMessage(`Inicio de sesión exitoso`)
    } catch (err) { showMessage(`Error en login: ${err.message}`) }
  }

  const handleLogout = async () => {
    if (!token) return
    try {
      // Setup the token temporarily if missing in localStorage because authService uses localStorage
      localStorage.setItem('token', token);
      const data = await authService.logout()
      setToken(null)
      setUserInfo(null)
      localStorage.removeItem('token')
      showMessage(`Logout exitoso: ${data?.mensaje || 'OK'}`)
      setLoginForm({ email: "", password: "" })
    } catch (err) { showMessage(`Error en logout: ${err.message}`) }
  }

  const handleGetUser = async () => {
    if (!token) {
      showMessage("Error: No hay token activo")
      return
    }
    try {
      localStorage.setItem('token', token);
      const data = await authService.getUserProfile()
      setUserInfo(data)
      showMessage("Datos de usuario obtenidos correctamente")
    } catch (err) {
      setUserInfo(null)
      showMessage(`Error al obtener usuario: ${err.message}`)
    }
  }

  const handleGetAllUsers = async () => {
    try {
      if (token) localStorage.setItem('token', token);
      const data = await authService.getAllUsers()
      setUserList(data)
      showMessage(`Se obtuvieron ${data.length} usuarios`)
    } catch (err) {
      setUserList(null)
      showMessage(`Error al obtener usuarios: ${err.message}`)
    }
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
              <input placeholder="Ej. María García" value={regForm.nombre} onChange={e => setRegForm({ ...regForm, nombre: e.target.value })} required />
            </div>
            <div className="input-group">
              <label>Correo Electrónico</label>
              <input type="email" placeholder="correo@ejemplo.com" value={regForm.email} onChange={e => setRegForm({ ...regForm, email: e.target.value })} required />
            </div>
            <div className="input-group">
              <label>Contraseña</label>
              <input type="password" placeholder="••••••••" value={regForm.password} onChange={e => setRegForm({ ...regForm, password: e.target.value })} required />
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
              <input type="email" placeholder="correo@ejemplo.com" value={loginForm.email} onChange={e => setLoginForm({ ...loginForm, email: e.target.value })} required />
            </div>
            <div className="input-group">
              <label>Contraseña</label>
              <input type="password" placeholder="••••••••" value={loginForm.password} onChange={e => setLoginForm({ ...loginForm, password: e.target.value })} required />
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
                <textarea readOnly value={token} rows="4" className="token-display" />
              </div>
              <div style={{ display: 'flex', gap: '10px', marginTop: '10px' }}>
                <button onClick={handleGetUser} className="btn btn-primary" style={{ flex: 1 }}>Ver Datos</button>
                <button onClick={handleLogout} className="btn btn-danger" style={{ flex: 1 }}>Cerrar Sesión</button>
              </div>
              {userInfo && (
                <div style={{ marginTop: '15px', background: '#f5f5f5', padding: '10px', borderRadius: '5px' }}>
                  <p><strong>ID:</strong> {userInfo.id}</p>
                  <p><strong>Nombre:</strong> {userInfo.nombre}</p>
                  <p><strong>Email:</strong> {userInfo.email}</p>
                  <p><strong>Token Actual:</strong> <span style={{ wordBreak: 'break-all', fontSize: '0.85em', color: '#666' }}>{userInfo.token || "Sin token"}</span></p>
                </div>
              )}
            </div>
          ) : (
            <div className="status-inactive">
              <div className="icon-lock">🔒</div>
              <p>Actualmente no tienes un token válido. Inicia sesión para obtener uno.</p>
            </div>
          )}
        </section>

        <section className="card" style={{ gridColumn: '1 / -1' }}>
          <div className="card-header">
            <h2>Gestión de Usuarios</h2>
            <p>Lista de todos los usuarios registrados en el sistema.</p>
          </div>
          <div style={{ padding: '20px' }}>
            <button onClick={handleGetAllUsers} className="btn btn-primary" style={{ marginBottom: '15px' }}>
              Cargar Usuarios
            </button>

            {userList && (
              <div style={{ overflowX: 'auto' }}>
                <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
                  <thead>
                    <tr style={{ background: '#eee' }}>
                      <th style={{ padding: '10px', border: '1px solid #ddd' }}>ID</th>
                      <th style={{ padding: '10px', border: '1px solid #ddd' }}>Nombre</th>
                      <th style={{ padding: '10px', border: '1px solid #ddd' }}>Email</th>
                      <th style={{ padding: '10px', border: '1px solid #ddd' }}>Token</th>
                    </tr>
                  </thead>
                  <tbody>
                    {userList.map(user => (
                      <tr key={user.id}>
                        <td style={{ padding: '10px', border: '1px solid #ddd' }}>{user.id}</td>
                        <td style={{ padding: '10px', border: '1px solid #ddd' }}>{user.nombre}</td>
                        <td style={{ padding: '10px', border: '1px solid #ddd' }}>{user.email}</td>
                        <td style={{ padding: '10px', border: '1px solid #ddd', maxWidth: '200px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }} title={user.token || "Sin token activo"}>
                          {user.token ? user.token : <i style={{ color: '#999' }}>Sin token activo</i>}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
            {userList && userList.length === 0 && <p>No hay usuarios registrados.</p>}
          </div>
        </section>
      </div>
    </div>
  )
}




