import { useState, useEffect } from 'react';
import Navbar from '../../components/layout/Navbar';
import { useNavigate, useParams } from 'react-router-dom';
import './Admin.css';
import './UserActivityDetail.css';
import { adminService, authService } from '../../services/api.service';

export default function UserActivityDetail() {
    const navigate = useNavigate();
    const { userId } = useParams();
    const [user, setUser] = useState(null);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);

    const token = localStorage.getItem('token');

    useEffect(() => {
        if (!token) {
            navigate('/auth');
            return;
        }

        const fetchStats = async () => {
            try {
                const data = await adminService.getUsersStats();
                const foundUser = data.find(u => u.id === parseInt(userId));
                if (!foundUser) {
                    setError('Usuario no encontrado');
                } else {
                    setUser(foundUser);
                }
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        fetchStats();
    }, [navigate, token, userId]);

    const handleLogout = async () => {
        try {
            await authService.logout();
        } catch (e) {
            console.error(e);
        }
        localStorage.removeItem('token');
        navigate('/');
    };

    if (loading) {
        return (
            <div className="profile-wrapper">
                <div className="loading-spinner">Cargando actividad...</div>
            </div>
        );
    }

    if (error || !user) {
        return (
            <div className="profile-wrapper">
                <div className="profile-card error-card">
                    <h2>Oops, hubo un problema</h2>
                    <p>{error || 'Usuario no encontrado'}</p>
                    <button onClick={() => navigate('/admin/users')} className="btn-primary">Volver a lista de usuarios</button>
                </div>
            </div>
        );
    }

    return (
        <div className="profile-layout">
            <Navbar>
                <a onClick={() => navigate('/admin/users')} style={{ cursor: 'pointer' }}>Lista de usuarios</a>
                <button onClick={handleLogout} className="btn-secondary">Cerrar sesión</button>
            </Navbar>

            <main className="profile-main">
                <div className="profile-card activity-detail-card">
                    <div className="profile-header-banner"></div>
                    <div className="profile-avatar">
                        {user.nombre.charAt(0).toUpperCase()}
                    </div>

                    <div className="profile-info">
                        <h1>{user.nombre}</h1>
                        <p className="profile-email">{user.email}</p>
                        <span className={`badge-role ${user.esAdmin ? 'badge-admin' : ''}`}>
                            {user.esAdmin ? 'Admin' : 'Usuario'}
                        </span>
                    </div>

                    <div className="activity-stats">
                        <div className="stat-item">
                            <span className="stat-label">Cursos creados</span>
                            <span className="stat-value">{user.cursosCreados}</span>
                        </div>
                        <div className="stat-item">
                            <span className="stat-label">Matriculaciones</span>
                            <span className="stat-value">{user.cursosMatriculados}</span>
                        </div>
                        <div className="stat-item">
                            <span className="stat-label">Ejercicios completados</span>
                            <span className="stat-value">{user.ejerciciosCompletados}</span>
                        </div>
                        <div className="stat-item">
                            <span className="stat-label">Puntos acumulados</span>
                            <span className="stat-value points">{user.puntosAcumulados}</span>
                        </div>
                    </div>

                    <div className="profile-actions">
                        <button className="btn-primary btn-full" onClick={() => navigate('/admin/users')}>
                            ← Volver a lista
                        </button>
                    </div>
                </div>
            </main>
        </div>
    );
}
