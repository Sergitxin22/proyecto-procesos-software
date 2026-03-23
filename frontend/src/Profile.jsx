import { useState, useEffect } from 'react';
import './Profile.css';
import { authService, userService } from './services/api.service';

export default function Profile() {
    const [courses, setCourses] = useState([]);
    const [user, setUser] = useState(null);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);

    const token = localStorage.getItem('token');

    useEffect(() => {
        if (!token) {
            window.history.pushState({}, '', '/auth');
            window.dispatchEvent(new PopStateEvent('popstate'));
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
        window.history.pushState({}, '', '/');
        window.dispatchEvent(new PopStateEvent('popstate'));
    };

    const navigateToHome = () => {
        window.history.pushState({}, '', '/');
        window.dispatchEvent(new PopStateEvent('popstate'));
    };

    const navigateToCreateCourse = () => {
        window.history.pushState({}, '', '/create_course');
        window.dispatchEvent(new PopStateEvent('popstate'));
    };

    const navigateToCreatedCourses = () => {
        window.history.pushState({}, '', '/created_courses');
        window.dispatchEvent(new PopStateEvent('popstate'));
    };

    const navigateToAdminPanel = () => {
        window.history.pushState({}, '', '/admin');
        window.dispatchEvent(new PopStateEvent('popstate'));
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
            <nav className="navbar">
                <div className="navbar-logo" onClick={navigateToHome} style={{ cursor: 'pointer' }}>
                    🎓 Flexilearn
                </div>
                <div className="navbar-links">
                    <a href="#cursos">Mis Cursos</a>
                    <button onClick={handleLogout} className="btn-secondary">Cerrar sesión</button>
                </div>
            </nav>

            <main className="profile-main">
                <div className="profile-card">
                    <div className="profile-header-banner"></div>
                    <div className="profile-avatar">
                        {user.nombre.charAt(0).toUpperCase()}
                    </div>

                    <div className="profile-info">
                        <h1>{user.nombre}</h1>
                        <p className="profile-email">{user.email}</p>
                        <span className="badge-role">{user.rol}</span>
                    </div>

                    <div className="profile-stats">
                        <div className="stat-box">
                            <span className="stat-number">0</span>
                            <span className="stat-label">Cursos completados</span>
                        </div>
                        <div className="stat-box">
                            <span className="stat-number">{courses.length}</span>
                            <span className="stat-label">Cursos creados</span>
                        </div>
                    </div>

                    <div className="profile-actions">
                        <button className="btn-primary btn-full" onClick={navigateToHome}>
                            Ir al Playground
                        </button>

                        <button className="btn-primary btn-full" onClick={navigateToCreateCourse}>
                            Crear curso
                        </button>

                        <button className="btn-primary btn-full" onClick={navigateToCreatedCourses}>
                            Cursos creados
                        </button>

                        {user.esAdmin && (
                            <button className="btn-primary btn-full" onClick={navigateToAdminPanel}>
                                Panel de administración
                            </button>
                        )}
                    </div>


                </div>
            </main>
        </div>
    );
}