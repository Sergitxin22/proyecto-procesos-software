import { useState, useEffect } from 'react';
import Navbar from '../components/Navbar';
import { useNavigate } from 'react-router-dom';
import './CreateCourse.css';
import { courseService } from '../services/api.service';

export default function CreateModule() {
    const navigate = useNavigate();
    const [moduleName, setModuleName] = useState('');
    const [moduleDescription, setModuleDescription] = useState('');

    const token = localStorage.getItem('token');

    // Get course ID from URL e.g. /created_courses/1/create_module
    const courseId = window.location.pathname.split('/')[2];

    useEffect(() => {
        if (!token) {
            navigate('/auth');

        }
    }, []);

    const handleCreateModule = async () => {
        try {
            await courseService.createModule({
                nombre: moduleName,
                descripcion: moduleDescription,
                idCurso: courseId,
            });

            navigate(`/created_courses/${courseId}`);

        } catch (err) {
            alert(`Error de conexión: ${err.message}`);
        }
    };

    const navigateBack = () => {
        navigate(`/created_courses/${courseId}`);

    };

    const navigateToHome = () => {
        navigate('/');

    };

    return (
        <div className="profile-layout">
            <Navbar>
                <button onClick={navigateBack} className="btn-secondary">Volver al curso</button>
            </Navbar>

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