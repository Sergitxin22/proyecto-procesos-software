import { useState, useEffect } from 'react';
import './Admin.css';
import './UserList.css';

export default function UserList() {
    const [users, setUsers] = useState([]);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);
    const [search, setSearch] = useState('');
    const [deletingId, setDeletingId] = useState(null);

    const token = localStorage.getItem('token');
    const API_URL = "http://localhost:8080/api/auth";

    useEffect(() => {
        if (!token) {
            window.history.pushState({}, '', '/auth');
            window.dispatchEvent(new PopStateEvent('popstate'));
            return;
        }

        const fetchUsers = async () => {
            try {
                const res = await fetch(`http://localhost:8080/api/admin/users`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ token })
                });
                const data = await res.json();

                if (res.ok) {
                    setUsers(data);
                } else {
                    setError(data.mensaje || "Error al cargar los usuarios.");
                }
            } catch (err) {
                setError(`Error de conexión: ${err.message}`);
            } finally {
                setLoading(false);
            }
        };

        fetchUsers();
    }, [token]);

    const handleLogout = async () => {
        try {
            await fetch(`${API_URL}/logout`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ token })
            });
        } catch (e) {
            console.error(e);
        }

        localStorage.removeItem('token');
        window.history.pushState({}, '', '/');
        window.dispatchEvent(new PopStateEvent('popstate'));
    };

    const navigateToHome = () => {
        window.history.pushState({}, '', '/');
        window.dispatchEvent(new PopStateEvent('popstate'));
    };

    const navigateToAdmin = () => {
        window.history.pushState({}, '', '/admin');
        window.dispatchEvent(new PopStateEvent('popstate'));
    };

    const handleDelete = async (usuario) => {
        if (!window.confirm(`¿Seguro que quieres eliminar a "${usuario.nombre}"? Esta acción no se puede deshacer.`)) return;

        setDeletingId(usuario.id);
        try {
            const res = await fetch(`http://localhost:8080/api/admin/deleteUser`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ token, nombreUsuario: usuario.nombre })
            });
            const data = await res.json();

            if (res.ok) {
                setUsers(prev => prev.filter(u => u.id !== usuario.id));
            } else {
                alert(data.mensaje || "Error al eliminar el usuario.");
            }
        } catch (err) {
            alert(`Error de conexión: ${err.message}`);
        } finally {
            setDeletingId(null);
        }
    };

    const filteredUsers = users.filter(u =>
        u.nombre.toLowerCase().includes(search.toLowerCase()) ||
        u.email.toLowerCase().includes(search.toLowerCase())
    );

    if (loading) {
        return (
            <div className="profile-wrapper">
                <div className="loading-spinner">Cargando usuarios...</div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="profile-wrapper">
                <div className="profile-card error-card">
                    <h2>Oops, hubo un problema</h2>
                    <p>{error}</p>
                    <button onClick={navigateToAdmin} className="btn-primary">Volver al panel</button>
                </div>
            </div>
        );
    }

    return (
        <div className="profile-layout">
            <nav className="navbar">
                <div className="navbar-logo" onClick={navigateToHome} style={{ cursor: 'pointer' }}>
                    🎓 Flexilearn
                </div>
                <div className="navbar-links">
                    <a onClick={navigateToAdmin} style={{ cursor: 'pointer' }}>Panel Admin</a>
                    <button onClick={handleLogout} className="btn-secondary">Cerrar sesión</button>
                </div>
            </nav>

            <main className="profile-main">
                <div className="userlist-card" style={{
                    padding: '2rem',
                    maxWidth: '900px',
                    width: '100%',
                    boxSizing: 'border-box',
                    background: 'white',
                    borderRadius: '16px',
                    boxShadow: '0 10px 40px rgba(0,0,0,0.08)'
                }}>
                    <div className="profile-info" style={{ marginBottom: '1.5rem' }}>
                        <h1>Lista de usuarios</h1>
                        <p className="profile-email">{users.length} usuarios registrados</p>
                    </div>

                    <div style={{ marginBottom: '1.25rem' }}>
                        <input
                            type="text"
                            placeholder="Buscar por nombre o email..."
                            value={search}
                            onChange={e => setSearch(e.target.value)}
                            style={{
                                width: '100%',
                                padding: '0.6rem 1rem',
                                borderRadius: '8px',
                                border: '1px solid #ddd',
                                fontSize: '0.95rem',
                                boxSizing: 'border-box'
                            }}
                        />
                    </div>

                    {filteredUsers.length === 0 ? (
                        <p style={{ textAlign: 'center', color: '#888' }}>No se encontraron usuarios.</p>
                    ) : (
                        <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.93rem' }}>
                            <thead>
                                <tr style={{ borderBottom: '2px solid #eee', textAlign: 'left' }}>
                                    <th style={thStyle}>ID</th>
                                    <th style={thStyle}>Nombre</th>
                                    <th style={thStyle}>Email</th>
                                    <th style={thStyle}>Rol</th>
                                    <th style={thStyle}>Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                {filteredUsers.map(u => (
                                    <tr key={u.id} style={{ borderBottom: '1px solid #f0f0f0' }}>
                                        <td style={tdStyle}>{u.id}</td>
                                        <td style={tdStyle}>
                                            <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
                                                <span style={avatarStyle}>
                                                    {u.nombre.charAt(0).toUpperCase()}
                                                </span>
                                                {u.nombre}
                                            </div>
                                        </td>
                                        <td style={tdStyle}>{u.email}</td>
                                        <td style={tdStyle}>
                                            <span className={`badge-role ${u.esAdmin ? 'badge-admin' : ''}`}>
                                                {u.esAdmin ? 'Admin' : 'Usuario'}
                                            </span>
                                        </td>
                                        <td style={tdStyle}>
                                            <button
                                                onClick={() => handleDelete(u)}
                                                disabled={deletingId === u.id}
                                                style={deleteBtnStyle(deletingId === u.id)}
                                            >
                                                {deletingId === u.id ? 'Eliminando...' : 'Eliminar'}
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    )}

                    <div className="profile-actions" style={{ marginTop: '1.5rem' }}>
                        <button className="btn-primary btn-full" onClick={navigateToAdmin}>
                            ← Volver al panel
                        </button>
                    </div>
                </div>
            </main>
        </div>
    );
}

const deleteBtnStyle = (disabled) => ({
    padding: '0.35rem 0.8rem',
    borderRadius: '6px',
    border: 'none',
    background: disabled ? '#f5a5a5' : '#e53e3e',
    color: '#fff',
    fontWeight: '600',
    fontSize: '0.82rem',
    cursor: disabled ? 'not-allowed' : 'pointer',
    transition: 'background 0.2s',
});


const thStyle = {
    padding: '0.6rem 0.75rem',
    fontWeight: '600',
    color: '#555'
};

const tdStyle = {
    padding: '0.65rem 0.75rem',
    verticalAlign: 'middle'
};

const avatarStyle = {
    display: 'inline-flex',
    alignItems: 'center',
    justifyContent: 'center',
    width: '30px',
    height: '30px',
    borderRadius: '50%',
    background: 'linear-gradient(135deg, #667eea, #764ba2)',
    color: '#fff',
    fontWeight: 'bold',
    fontSize: '0.85rem',
    flexShrink: 0
};