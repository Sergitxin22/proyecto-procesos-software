import { useState, useEffect } from 'react';
import Navbar from '../../components/layout/Navbar';
import { useNavigate } from 'react-router-dom';
import './CreateCourse.css';
import { courseService } from '../../services/api.service';

export default function CreateExercise() {
    const navigate = useNavigate();
    const [nombre, setNombre] = useState('');
    const [lenguaje, setLenguaje] = useState('');
    const [teoria, setTeoria] = useState('');
    const [enunciado, setEnunciado] = useState('');
    const [codigoInicial, setCodigoInicial] = useState('');
    const [puntos, setPuntos] = useState('');
    const [tests, setTests] = useState([]);

    const token = localStorage.getItem('token');

    // URL pattern: /created_courses/{courseId}/modules/{moduleId}/create_exercise
    const pathParts = window.location.pathname.split('/');
    const courseId = pathParts[2];
    const moduleId = pathParts[4];

    useEffect(() => {
        if (!token) {
            navigate('/auth');

        }
    }, [navigate, token]);

    const handleAddTest = () => {
        setTests((prev) => [...prev, { codigo: '', salidaEsperada: '' }]);
    };

    const handleRemoveTest = (index) => {
        setTests((prev) => prev.filter((_, i) => i !== index));
    };

    const handleTestChange = (index, field, value) => {
        setTests((prev) => prev.map((test, i) => (
            i === index ? { ...test, [field]: value } : test
        )));
    };

    const handleCreateExercise = async () => {
        try {
            const idExercise = await courseService.createExercise({
                nombre,
                lenguaje,
                teoria,
                enunciado,
                codigoInicial,
                puntos: parseInt(puntos),
                idModulo: moduleId,
            });

            const cleanTests = tests
                .map((test) => ({
                    codigo: (test.codigo || '').trim(),
                    salidaEsperada: (test.salidaEsperada || '').trim(),
                }))
                .filter((test) => test.codigo.length > 0 || test.salidaEsperada.length > 0);

            if (cleanTests.length > 0) {
                await courseService.createExerciseTests(idExercise, cleanTests);
            }

            navigate(`/created_courses/${courseId}`);

        } catch (err) {
            alert(`Error de conexión: ${err.message}`);
        }
    };

    const navigateBack = () => {
        navigate(`/created_courses/${courseId}`);

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

                        <div className="tests-section">
                            <div className="tests-section-header">
                                <label className="form-label">Tests del ejercicio</label>
                                <div className="tests-section-actions">
                                    <button type="button" className="btn-secondary tests-btn" onClick={handleAddTest}>
                                        + Añadir test
                                    </button>
                                </div>
                            </div>

                            {tests.length === 0 && (
                                <p className="tests-empty">No hay tests todavía. Puedes añadirlos manualmente.</p>
                            )}

                            {tests.map((test, index) => (
                                <div key={index} className="test-card">
                                    <div className="test-card-top">
                                        <span className="test-card-title">Test {index + 1}</span>
                                        <button
                                            type="button"
                                            className="test-remove-btn"
                                            onClick={() => handleRemoveTest(index)}
                                        >
                                            Eliminar
                                        </button>
                                    </div>

                                    <label className="form-label" htmlFor={`test-code-${index}`}>
                                        Código del test
                                    </label>
                                    <textarea
                                        id={`test-code-${index}`}
                                        className="form-input form-textarea form-code"
                                        rows={4}
                                        value={test.codigo}
                                        onChange={(e) => handleTestChange(index, 'codigo', e.target.value)}
                                    />

                                    <label className="form-label" htmlFor={`test-output-${index}`}>
                                        Salida esperada
                                    </label>
                                    <input
                                        id={`test-output-${index}`}
                                        type="text"
                                        className="form-input"
                                        value={test.salidaEsperada}
                                        onChange={(e) => handleTestChange(index, 'salidaEsperada', e.target.value)}
                                    />
                                </div>
                            ))}
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




