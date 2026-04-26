import { useState, useEffect, useRef } from 'react';
import Navbar from '../../components/layout/Navbar';
import { useNavigate } from 'react-router-dom';
import Editor from '@monaco-editor/react';
import './Exercise.css';
import { courseService } from '../../services/api.service';

export default function Exercise() {
    const navigate = useNavigate();
    const [exercise, setExercise] = useState(null);
    const [code, setCode] = useState('');
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const [result, setResult] = useState(null); // null | 'success' | 'error'
    const [activeTab, setActiveTab] = useState('enunciado'); // 'enunciado' | 'teoria'

    const token = localStorage.getItem('token');

    // URL pattern: /exercise/:id
    const exerciseId = window.location.pathname.split('/').pop();

    useEffect(() => {
        if (!token) {
            navigate('/auth');
            return;
        }
        fetchExercise();
    }, []);

    const fetchExercise = async () => {
        try {
            // Replace with your actual endpoint
            const res = await fetch(`http://localhost:8080/api/courses/exercises/${exerciseId}`, {
                method: 'GET',
                headers: { 'Content-Type': 'application/json' },
            });
            const data = await res.json();
            if (res.ok) {
                setExercise(data);
                setCode(data.codigoInicial || '');
            }
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async () => {
        setSubmitting(true);
        setResult(null);
        try {
            // Replace with your actual submit endpoint
            const res = await courseService.submitSolution({idEjercicio: exercise.id, codigo: code})
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
                            {result === 'success' ? '✅ ¡Correcto! Ejercicio superado.' : '❌ Respuesta incorrecta, inténtalo de nuevo.'}
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