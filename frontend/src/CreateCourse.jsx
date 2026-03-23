import { useState, useEffect } from 'react';
import './CreateCourse.css';

export default function CreateCourse() {
    const [courseName, setCourseName] = useState('');
    const [courseCategory, setCourseCategory] = useState('');
    const [courseDescription, setCourseDescription] = useState('');
    const [courseDifficulty, setCourseDifficulty] = useState('');
    const token = localStorage.getItem('token');
    const API_URL = "http://localhost:8080/api/courses/";

    useEffect(() => {
        // Si no hay token, redirigir directo a autenticarse
        if (!token) {
            window.history.pushState({}, '', '/auth');
            window.dispatchEvent(new PopStateEvent('popstate'));
            return;
        }
    })

    const createCourse = async () => {
        try {
            const res = await fetch(`${API_URL}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({
                    nombre: courseName,
                    categoria: courseCategory,
                    descripcion: courseDescription,
                    dificultad: courseDifficulty,
                })
            });

            const data = await res.json();

            if (res.ok) {
                navigateToProfile();
            } else {
                alert(data.mensaje || "Error al crear el curso.");
            }
        } catch (err) {
            alert(`Error de conexión: ${err.message}`);
        }
    };

    const navigateToHome = () => {
        window.history.pushState({}, '', '/');
        window.dispatchEvent(new PopStateEvent('popstate'));
    };

    const navigateToProfile = () => {
        window.history.pushState({}, '', '/profile');
        window.dispatchEvent(new PopStateEvent('popstate'));
    };

    return (
        <div className="profile-layout">
            <nav className="navbar">
                <div className="navbar-logo" onClick={navigateToHome} style={{ cursor: 'pointer' }}>
                    🎓 Flexilearn
                </div>
                <div className="navbar-links">
                    <a href="#cursos">Mis Cursos</a>
                    <button onClick={navigateToProfile} className="btn-secondary">Volver al perfil</button>
                </div>
            </nav>

            <main className="profile-main">
                <div className="profile-card create-course-card">
                    <div className="profile-header-banner"></div>

                    <div className="profile-info">
                        <h1>Crear nuevo curso</h1>
                        <p className="profile-email">Completa los datos del nuevo curso</p>
                    </div>

                    <div className="create-course-form">
                        <div className="form-group">
                            <label className="form-label" htmlFor="course-name">
                                Nombre del curso
                            </label>
                            <input
                                id="course-name"
                                type="text"
                                className="form-input"
                                placeholder="Ej. Introducción a React"
                                value={courseName}
                                onChange={(e) => setCourseName(e.target.value)}
                            />
                        </div>

                        <div className="form-group">
                            <label className="form-label" htmlFor="course-category">
                                Categoría
                            </label>
                            <input
                                id="course-category"
                                type="text"
                                className="form-input"
                                placeholder="Ej. Programación, Diseño..."
                                value={courseCategory}
                                onChange={(e) => setCourseCategory(e.target.value)}
                            />
                        </div>

                        <div className="form-group">
                            <label className="form-label" htmlFor="course-description">
                                Descripción
                            </label>
                            <textarea
                                id="course-description"
                                className="form-input form-textarea"
                                placeholder="Describe de qué trata el curso..."
                                rows={4}
                                value={courseDescription}
                                onChange={(e) => setCourseDescription(e.target.value)}
                            />
                        </div>

                        <div className="form-group">
                            <label className="form-label" htmlFor="course-difficulty">
                                Dificultad
                            </label>
                            <select
                                id="course-difficulty"
                                className="form-input form-select"
                                value={courseDifficulty}
                                onChange={(e) => setCourseDifficulty(e.target.value)}
                            >
                                <option value="">Selecciona una dificultad</option>
                                <option value="facil">Fácil</option>
                                <option value="medio">Medio</option>
                                <option value="dificil">Difícil</option>
                            </select>
                        </div>

                        <div className="profile-actions">
                            <button className="btn-primary btn-full" onClick={createCourse}>
                                Crear curso
                            </button>
                            <button className="btn-secondary btn-full" onClick={navigateToProfile}>
                                Cancelar
                            </button>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    );
}