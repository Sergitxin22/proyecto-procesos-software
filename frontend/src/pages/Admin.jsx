import { useState, useEffect } from 'react';
import Navbar from '../components/Navbar';
import { useNavigate } from 'react-router-dom';
import './Admin.css';
import { authService, userService } from '../services/api.service';

export default function Admin() {
    const navigate = useNavigate();
    const [courses, setCourses] = useState([]);
    const [user, setUser] = useState(null);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);

    const token = localStorage.getItem('token');

    useEffect(() => {
        if (!token) {
            navigate('/auth');

            return;
        }

        const loadData = async () => {
            try {
                const [coursesData, userData] = await Promise.all([
                    userService.getCreatedCourses(),
                    authService.getUserProfile()
                ]);
                setCourses(coursesData);
                setUser(userData);
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        loadData();
    }, [token]);

    const handleLogout = async () => {
        try {
            await authService.logout();
        } catch (e) {
            console.error(e);
        }

        localStorage.removeItem('token');
        navigate('/');

    };

    const navigateToHome = () => {
        navigate('/');

    };

    const navigateToUserList = () => {
        navigate('/admin/users');

    };

    if (loading) {
        return (
            <div className="profile-wrapper">
                <div className="loading-spinner">Cargando perfil...</div>
            </div>
        );
    }

    if (error || !user) {
        return (
            <div className="profile-wrapper">
                <div className="profile-card error-card">
                    <h2>Oops, hubo un problema</h2>
                    <p>{error}</p>
                    <button onClick={handleLogout} className="btn-primary">Volver a intentar</button>
                </div>
            </div>
        );
    }

    return (
        <div className="profile-layout">
            <Navbar>
                <a href="#cursos">Mis Cursos</a>
                    <button onClick={handleLogout} className="btn-secondary">Cerrar sesión</button>
            </Navbar>

            <main className="profile-main">
                <div className="profile-card">
                    <div className="profile-header-banner"></div>
                    <div className="profile-avatar">
                        {user.nombre.charAt(0).toUpperCase()}
                    </div>


                    <div className="profile-info">
                        <h1>{user.nombre}</h1>
                        <p className="profile-email">Panel de administración</p>
                        <span className="badge-role">{user.rol}</span>
                    </div>

                    <div className="profile-actions">
                        <button className="btn-primary btn-full" onClick={navigateToUserList}>
                            Lista de usuarios
                        </button>
                    </div>
                </div>
            </main>
        </div>
    );
}
