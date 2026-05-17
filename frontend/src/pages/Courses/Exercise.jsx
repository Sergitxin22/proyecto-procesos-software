import { useState, useEffect } from 'react';
import Navbar from '../../components/layout/Navbar';
import { useNavigate, useParams } from 'react-router-dom';
import Editor from '@monaco-editor/react';
import './Exercise.css';
import { courseService } from '../../services/api.service';

export default function Exercise() {
    const navigate = useNavigate();
    const { id: exerciseId } = useParams();

    const [exercise, setExercise] = useState(null);
    const [code, setCode] = useState('');
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const [result, setResult] = useState(null); // null | 'success' | 'error'
    const [activeTab, setActiveTab] = useState('enunciado'); // 'enunciado' | 'teoria'
    const [moduleExercises, setModuleExercises] = useState([]);

    const token = localStorage.getItem('token');

    useEffect(() => {
        if (!token) {
            navigate('/auth');
            return;
        }
        // Resetear estado al cambiar de ejercicio
        setExercise(null);
        setCode('');
        setResult(null);
        setActiveTab('enunciado');
        setModuleExercises([]);
        setLoading(true);
        fetchExercise();
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [exerciseId]);

    const fetchExercise = async () => {
        try {
            const [exerciseData, siblings] = await Promise.all([
                fetch(`http://localhost:8080/api/courses/exercises/${exerciseId}`, {
                    method: 'GET',
                    headers: { 'Content-Type': 'application/json' },
                }).then(r => r.json()),
                courseService.getModuleExercises(exerciseId),
            ]);
            setExercise(exerciseData);
            setCode(exerciseData.codigoInicial || '');
            setModuleExercises(siblings || []);
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    // Calcular posición dentro del módulo
    const currentIndex = moduleExercises.findIndex(ex => String(ex.id) === String(exerciseId));
    const prevExercise = currentIndex > 0 ? moduleExercises[currentIndex - 1] : null;
    const nextExercise = currentIndex >= 0 && currentIndex < moduleExercises.length - 1
        ? moduleExercises[currentIndex + 1]
        : null;
    const isLastExercise = moduleExercises.length > 0 && currentIndex === moduleExercises.length - 1;

    const handleSubmit = async () => {
        setSubmitting(true);
        setResult(null);
        try {
            const res = await courseService.submitSolution({ idEjercicio: exercise.id, codigo: code });
            if (res) {
                setResult('success');
            } else {
                setResult('error');
            }
        } catch (err) {
            setResult('error');
        } finally {
            setSubmitting(false);
        }
    };

    const handleReset = () => {
        setCode(exercise?.codigoInicial || '');
        setResult(null);
    };

    const navigateBack = () => {
        navigate(-1);
    };

    if (loading) {
        return (
            <div className="profile-layout">
                <Navbar>
                    <button onClick={navigateBack} className="btn-secondary">Volver</button>
                </Navbar>
                <main className="exercise-main">
                    <div className="loading-spinner">Cargando ejercicio...</div>
                </main>
            </div>
        );
    }

    return (
        <div className="profile-layout">
            <Navbar>
                <button onClick={navigateBack} className="btn-secondary">Volver al curso</button>
            </Navbar>

            <main className="exercise-main">
                {/* Left panel — exercise info */}
                <aside className="exercise-panel">
                    <div className="exercise-meta">
                        <div className="exercise-meta-top">
                            <span className="exercise-language-badge">{exercise?.lenguaje}</span>
                            <span className="exercise-points-badge">⭐ {exercise?.puntos} pts</span>
                        </div>
                        <h1 className="exercise-title">{exercise?.nombre}</h1>
                        {moduleExercises.length > 1 && (
                            <p className="exercise-position-label">
                                Ejercicio {currentIndex + 1} de {moduleExercises.length}
                                {isLastExercise && (
                                    <span className="exercise-last-badge">Último del módulo</span>
                                )}
                            </p>
                        )}
                    </div>

                    <div className="exercise-tabs">
                        <button
                            className={`exercise-tab ${activeTab === 'enunciado' ? 'active' : ''}`}
                            onClick={() => setActiveTab('enunciado')}
                        >
                            Enunciado
                        </button>
                        <button
                            className={`exercise-tab ${activeTab === 'teoria' ? 'active' : ''}`}
                            onClick={() => setActiveTab('teoria')}
                        >
                            Teoría
                        </button>
                    </div>

                    <div className="exercise-tab-content">
                        {activeTab === 'enunciado' && (
                            <p className="exercise-text">{exercise?.enunciado}</p>
                        )}
                        {activeTab === 'teoria' && (
                            <p className="exercise-text">{exercise?.teoria}</p>
                        )}
                    </div>

                    {result && (
                        <div className={`exercise-result ${result}`}>
                            {result === 'success'
                                ? '✅ ¡Correcto! Ejercicio superado.'
                                : '❌ Respuesta incorrecta, inténtalo de nuevo.'}
                        </div>
                    )}

                    {/* Mensaje de fin de módulo */}
                    {isLastExercise && result === 'success' && (
                        <div className="exercise-module-complete">
                            <span className="exercise-module-complete-icon">🎉</span>
                            <strong>¡Módulo completado!</strong>
                            <p>Has terminado todos los ejercicios de este módulo.</p>
                        </div>
                    )}

                    {/* Navegación entre ejercicios */}
                    {moduleExercises.length > 1 && (
                        <div className="exercise-nav">
                            <button
                                className="exercise-nav-btn"
                                onClick={() => navigate(`/exercises/${prevExercise.id}`)}
                                disabled={!prevExercise}
                                title={prevExercise ? prevExercise.nombre : ''}
                            >
                                ← Anterior
                            </button>
                            <button
                                className="exercise-nav-btn exercise-nav-btn--next"
                                onClick={() => navigate(`/exercises/${nextExercise.id}`)}
                                disabled={!nextExercise}
                                title={nextExercise ? nextExercise.nombre : ''}
                            >
                                Siguiente →
                            </button>
                        </div>
                    )}
                </aside>

                {/* Right panel — editor */}
                <div className="exercise-editor-panel">
                    <div className="editor-header">
                        <span className="editor-filename">solución.{exercise?.lenguaje?.toLowerCase() || 'py'}</span>
                        <div className="editor-actions">
                            <button className="btn-secondary" onClick={handleReset}>
                                Resetear
                            </button>
                            <button
                                className="btn-primary"
                                onClick={handleSubmit}
                                disabled={submitting}
                            >
                                {submitting ? 'Enviando...' : 'Enviar solución →'}
                            </button>
                        </div>
                    </div>

                    <div className="editor-wrapper">
                        <Editor
                            height="100%"
                            language={exercise?.lenguaje?.toLowerCase() || 'python'}
                            value={code}
                            onChange={(val) => setCode(val || '')}
                            theme="vs-dark"
                            options={{
                                fontSize: 14,
                                minimap: { enabled: false },
                                scrollBeyondLastLine: false,
                                lineNumbers: 'on',
                                roundedSelection: true,
                                automaticLayout: true,
                                tabSize: 4,
                                wordWrap: 'on',
                            }}
                        />
                    </div>
                </div>
            </main>
        </div>
    );
}
