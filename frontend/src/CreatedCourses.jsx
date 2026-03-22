import { useState, useEffect } from 'react';
import './CreatedCourses.css';

const DIFFICULTY_LABELS = {
    FACIL: { label: 'Fácil', className: 'badge-easy' },
    MEDIO: { label: 'Medio', className: 'badge-medium' },
    DIFICIL: { label: 'Difícil', className: 'badge-hard' },
};

export default function CourseList() {
    const [courses, setCourses] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const token = localStorage.getItem('token');
    const API_URL = "http://localhost:8080/api/users/createdCourses";

    useEffect(() => {
        if (!token) {
            window.history.pushState({}, '', '/auth');
            window.dispatchEvent(new PopStateEvent('popstate'));
            return;
        }

        const fetchCourses = async () => {
            try {
                const res = await fetch(`${API_URL}`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({token})
                });
                const data = await res.json();

                if (res.ok) {
                    setCourses(data);
                } else {
                    setError(data.mensaje || "Error al cargar los cursos.");
                }
            } catch (err) {
                setError(`Error de conexión: ${err.message}`);
            } finally {
                setLoading(false);
            }
        };

        fetchCourses();
    }, []);

    const navigateToHome = () => {
        window.history.pushState({}, '', '/');
        window.dispatchEvent(new PopStateEvent('popstate'));
    };

    const navigateToProfile = () => {
        window.history.pushState({}, '', '/profile');
        window.dispatchEvent(new PopStateEvent('popstate'));
    };

    const navigateToCourse = (id) => {
        window.history.pushState({}, '', `/course/${id}`);
        window.dispatchEvent(new PopStateEvent('popstate'));
    };

    return (
        <div className="profile-layout">
            <nav className="navbar">
                <div className="navbar-logo" onClick={navigateToHome} style={{ cursor: 'pointer' }}>
                    🎓 Flexilearn
                </div>
                <div className="navbar-links">
                    <button onClick={navigateToProfile} className="btn-secondary">Volver al perfil</button>
                </div>
            </nav>

            <main className="profile-main">
                <div className="courses-container">
                    <div className="courses-header">
                        <h1>Cursos disponibles</h1>
                        <p className="profile-email">{courses.length} cursos encontrados</p>
                    </div>

                    {loading && (
                        <div className="loading-spinner">Cargando cursos...</div>
                    )}

                    {error && (
                        <div className="profile-card error-card">
                            <p>{error}</p>
                        </div>
                    )}

                    {!loading && !error && (
                        <div className="courses-grid">
                            {courses.map((course) => {
                                const diff = DIFFICULTY_LABELS[course.dificultad] || { label: course.dificultad, className: '' };
                                return (
                                    <div key={course.id} className="course-card" onClick={() => navigateToCourse(course.id)}>
                                        <div className="course-card-header">
                                            <span className="course-category">{course.categoria}</span>
                                            <span className={`badge-difficulty ${diff.className}`}>{diff.label}</span>
                                        </div>
                                        <h2 className="course-name">{course.nombre}</h2>
                                        <p className="course-description">{course.descripcion}</p>
                                        <div className="course-card-footer">
                                            <span className="course-modules">
                                                📦 {course.modulos.length} módulo{course.modulos.length !== 1 ? 's' : ''}
                                            </span>
                                            <button className="btn-primary">Ver curso →</button>
                                        </div>
                                    </div>
                                );
                            })}
                        </div>
                    )}
                </div>
            </main>
        </div>
    );
}