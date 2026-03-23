import { useState, useEffect } from 'react';
import Navbar from '../components/Navbar';
import { useNavigate } from 'react-router-dom';
import './CreateCourse.css';
import { courseService } from '../services/api.service';

export default function CreateExercise() {
    const navigate = useNavigate();
    const [nombre, setNombre] = useState('');
    const [lenguaje, setLenguaje] = useState('');
    const [teoria, setTeoria] = useState('');
    const [enunciado, setEnunciado] = useState('');
    const [codigoInicial, setCodigoInicial] = useState('');
    const [puntos, setPuntos] = useState('');

    const token = localStorage.getItem('token');

    // URL pattern: /created_courses/{courseId}/modules/{moduleId}/create_exercise
    const pathParts = window.location.pathname.split('/');
    const courseId = pathParts[2];
    const moduleId = pathParts[4];

    useEffect(() => {
        if (!token) {
            navigate('/auth');

        }
    }, []);

    const handleCreateExercise = async () => {
        try {
            await courseService.createExercise({
                nombre,
                lenguaje,
                teoria,
                enunciado,
                codigoInicial,
                puntos: parseInt(puntos),
                idModulo: moduleId,
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
                        <h1>Crear ejercicio</h1>
                        <p className="profile-email">Completa los datos del nuevo ejercicio</p>
                    </div>

                    <div className="create-course-form">
                        <div className="form-group">
                            <label className="form-label" htmlFor="exercise-name">
                                Nombre
                            </label>
                            <input
                                id="exercise-name"
                                type="text"
                                className="form-input"
                                placeholder="Ej. Variables en Python"
                                value={nombre}
                                onChange={(e) => setNombre(e.target.value)}
                            />
                        </div>

                        <div className="form-group">
                            <label className="form-label" htmlFor="exercise-language">
                                Lenguaje
                            </label>
                            <input
                                id="exercise-language"
                                type="text"
                                className="form-input"
                                placeholder="Ej. Python, JavaScript, Java..."
                                value={lenguaje}
                                onChange={(e) => setLenguaje(e.target.value)}
                            />
                        </div>

                        <div className="form-group">
                            <label className="form-label" htmlFor="exercise-teoria">
                                Teoría
                            </label>
                            <textarea
                                id="exercise-teoria"
                                className="form-input form-textarea"
                                placeholder="Explica la teoría necesaria para el ejercicio..."
                                rows={4}
                                value={teoria}
                                onChange={(e) => setTeoria(e.target.value)}
                            />
                        </div>

                        <div className="form-group">
                            <label className="form-label" htmlFor="exercise-enunciado">
                                Enunciado
                            </label>
                            <textarea
                                id="exercise-enunciado"
                                className="form-input form-textarea"
                                placeholder="Describe qué debe hacer el alumno..."
                                rows={4}
                                value={enunciado}
                                onChange={(e) => setEnunciado(e.target.value)}
                            />
                        </div>

                        <div className="form-group">
                            <label className="form-label" htmlFor="exercise-codigo">
                                Código inicial
                            </label>
                            <textarea
                                id="exercise-codigo"
                                className="form-input form-textarea form-code"
                                placeholder="# Escribe aquí el código inicial..."
                                rows={6}
                                value={codigoInicial}
                                onChange={(e) => setCodigoInicial(e.target.value)}
                            />
                        </div>

                        <div className="form-group">
                            <label className="form-label" htmlFor="exercise-puntos">
                                Puntos
                            </label>
                            <input
                                id="exercise-puntos"
                                type="text"
                                inputMode="numeric"
                                className="form-input"
                                placeholder="Ej. 10"
                                value={puntos}
                                onChange={(e) => setPuntos(e.target.value)}
                            />
                        </div>

                        <div className="profile-actions">
                            <button className="btn-primary btn-full" onClick={handleCreateExercise}>
                                Crear ejercicio
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
