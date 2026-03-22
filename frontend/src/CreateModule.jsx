import { useState, useEffect } from 'react';
import './CreateCourse.css';

export default function CreateModule() {
    const [moduleName, setModuleName] = useState('');
    const [moduleDescription, setModuleDescription] = useState('');

    const token = localStorage.getItem('token');
    const API_URL = "http://localhost:8080/api/courses/modules";

    // Get course ID from URL e.g. /created_courses/1/create_module
    const courseId = window.location.pathname.split('/')[2];

    useEffect(() => {
        if (!token) {
            window.history.pushState({}, '', '/auth');
            window.dispatchEvent(new PopStateEvent('popstate'));
        }
    }, []);

    const handleCreateModule = async () => {
        try {
            const res = await fetch(`${API_URL}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    nombre: moduleName,
                    descripcion: moduleDescription,
                    idCurso: courseId,
                })
            });

            const data = await res.json();

            if (res.ok) {
                window.history.pushState({}, '', `/created_courses/${courseId}`);
                window.dispatchEvent(new PopStateEvent('popstate'));
            } else {
                alert(data.mensaje || "Error al crear el módulo.");
            }
        } catch (err) {
            alert(`Error de conexión: ${err.message}`);
        }
    };

    const navigateBack = () => {
        window.history.pushState({}, '', `/created_courses/${courseId}`);
        window.dispatchEvent(new PopStateEvent('popstate'));
    };

    const navigateToHome = () => {
        window.history.pushState({}, '', '/');
        window.dispatchEvent(new PopStateEvent('popstate'));
    };

    return (
        <div className="profile-layout">
            <nav className="navbar">
                <div className="navbar-logo" onClick={navigateToHome} style={{ cursor: 'pointer' }}>
                    🎓 Flexilearn
                </div>
                <div className="navbar-links">
                    <button onClick={navigateBack} className="btn-secondary">Volver al curso</button>
                </div>
            </nav>

            <main className="profile-main">
                <div className="profile-card create-course-card">
                    <div className="profile-header-banner"></div>

                    <div className="profile-info">
                        <h1>Crear módulo</h1>
                        <p className="profile-email">Completa los datos del nuevo módulo</p>
                    </div>

                    <div className="create-course-form">
                        <div className="form-group">
                            <label className="form-label" htmlFor="module-name">
                                Nombre del módulo
                            </label>
                            <input
                                id="module-name"
                                type="text"
                                className="form-input"
                                placeholder="Ej. Introducción"
                                value={moduleName}
                                onChange={(e) => setModuleName(e.target.value)}
                            />
                        </div>

                        <div className="form-group">
                            <label className="form-label" htmlFor="module-description">
                                Descripción
                            </label>
                            <textarea
                                id="module-description"
                                className="form-input form-textarea"
                                placeholder="Describe el módulo..."
                                rows={4}
                                value={moduleDescription}
                                onChange={(e) => setModuleDescription(e.target.value)}
                            />
                        </div>

                        <div className="profile-actions">
                            <button className="btn-primary btn-full" onClick={handleCreateModule}>
                                Crear módulo
                            </button>
                            <button className="btn-secondary btn-full" onClick={navigateBack}>
                                Cancelar
                            </button>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    );
}