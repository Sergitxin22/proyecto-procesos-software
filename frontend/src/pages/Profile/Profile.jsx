import { useState, useEffect } from 'react';
import Navbar from '../../components/layout/Navbar';
import { useNavigate } from 'react-router-dom';
import './Profile.css';
import { authService, userService } from '../../services/api.service';

export default function Profile() {
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
    }, [navigate, token]);

    const handleLogout = async () => {
        try {
            await authService.logout();
        } catch (e) {
            console.error(e);
        }

        localStorage.removeItem('token');
        navigate('/');

    };

    const navigateToCreateCourse = () => {
        navigate('/create_course');

    };

    const navigateToCreatedCourses = () => {
        navigate('/created_courses');

    };

    const navigateToAdminPanel = () => {
        navigate('/admin');

    };

    const deleteAccount = () => {
        const response = authService.delete()
        navigate("/");
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
                        <button className="btn-primary btn-full" onClick={() => navigate('/')}>
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

                        <button className="btn-primary btn-full delete-button" onClick={deleteAccount}>
                            Eliminar cuenta
                        </button>
                    </div>


                </div>
            </main>
        </div>
    );
}




