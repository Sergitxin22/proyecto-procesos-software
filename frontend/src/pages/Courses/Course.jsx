import { useState, useEffect } from 'react';
import Navbar from '../../components/layout/Navbar';
import { useNavigate } from 'react-router-dom';
import './Course.css';
import { courseService } from '../../services/api.service';

const DIFFICULTY_LABELS = {
    FACIL: { label: 'Fácil', className: 'badge-easy' },
    MEDIO: { label: 'Medio', className: 'badge-medium' },
    DIFICIL: { label: 'Difícil', className: 'badge-hard' },
};

export default function CourseDetail() {
    const navigate = useNavigate();
    const [modules, setModules] = useState([]);
    const [courseName, setCourseName] = useState('');
    const [courseData, setCourseData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [misPuntos, setMisPuntos] = useState(0);
    const [totalPuntos, setTotalPuntos] = useState(0);

    const token = localStorage.getItem('token');
    const courseId = window.location.pathname.split('/').pop();

    useEffect(() => {
        if (!token) {
            navigate('/auth');
            return;
        }
        fetchModules();
        fetchProgress();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [navigate, token]);

    const fetchProgress = async () => {
        try {
            const myPts = await courseService.getMisPuntos(courseId);
            const totalPts = await courseService.getTotalPuntos(courseId);
            setMisPuntos(myPts);
            setTotalPuntos(totalPts);
        } catch (err) {
            console.error("Error fetching progress", err);
        }
    };

    const fetchModules = async () => {
        try {
            const data = await courseService.getCourse(courseId);
            setCourseName(data.nombre);
            setCourseData(data);
            setModules(data.modulos ?? []);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    const navigateToCourses = () => {
        navigate('/courses');
    };

    return (
        <div className="profile-layout">
            <Navbar>
                <button onClick={navigateToCourses} className="btn-secondary">Volver a cursos</button>
            </Navbar>

            <main className="profile-main">
                <div className="courses-container">

                    <div className="courses-header">
                        <h1>{courseName}</h1>
                        <p className="profile-email">{modules.length} módulos</p>
                    </div>

                    {totalPuntos > 0 && (
                        <div style={{ marginBottom: '2rem' }}>
                            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
                                <span>Progreso del curso</span>
                                <span>{misPuntos} / {totalPuntos} puntos ({Math.round((misPuntos / totalPuntos) * 100)}%)</span>
                            </div>
                            <div style={{ background: '#e2e8f0', borderRadius: '8px', overflow: 'hidden', width: '100%', height: '16px' }}>
                                <div style={{ background: '#3e76a6', width: `${Math.round((misPuntos / totalPuntos) * 100)}%`, height: '100%', transition: 'width 0.3s ease' }}></div>
                            </div>
                        </div>
                    )}

                    {loading && <div className="loading-spinner">Cargando módulos...</div>}
                    {error && <div className="profile-card error-card"><p>{error}</p></div>}

                    {courseData && (
                        <div className="course-info-card">
                            <div className="course-info-row">
                                <span className="course-info-label">Categoría</span>
                                <span className="course-info-value">{courseData.categoria}</span>
                            </div>
                            <div className="course-info-row">
                                <span className="course-info-label">Dificultad</span>
                                <span className={`badge-difficulty ${DIFFICULTY_LABELS[courseData.dificultad]?.className}`}>
                                    {DIFFICULTY_LABELS[courseData.dificultad]?.label ?? courseData.dificultad}
                                </span>
                            </div>
                            <div className="course-info-row">
                                <span className="course-info-label">Descripción</span>
                                <span className="course-info-value">{courseData.descripcion}</span>
                            </div>
                            <button className="btn-primary" onClick={() => navigate(`forum`)}>
                                Ver foro
                            </button>
                        </div>
                    )}

                    {!loading && !error && (
                        <div className="modules-list">
                            {modules.map((mod) => (
                                <div key={mod.id} className="module-card">
                                    <div className="module-header">
                                        <div>
                                            <h2 className="module-name">{mod.nombre}</h2>
                                            <p className="module-description">{mod.descripcion}</p>
                                        </div>
                                    </div>

                                    {mod.ejercicios && mod.ejercicios.length > 0 && (
                                        <div className="exercises-list">
                                            {mod.ejercicios.map((ex) => (
                                                <div key={ex.id} className="exercise-card">
                                                    <div className="exercise-info">
                                                        <span className="exercise-name">{ex.nombre}</span>
                                                        <span className="exercise-statement">{ex.enunciado}</span>
                                                    </div>
                                                    <div className="exercise-card-actions">
                                                        <span className="exercise-points">⭐ {ex.puntos} pts</span>
                                                        <button className="btn-primary" onClick={() => navigate(`/exercises/${ex.id}`)}>
                                                            Hacer ejercicio →
                                                        </button>
                                                    </div>
                                                </div>
                                            ))}
                                        </div>
                                    )}

                                    {(!mod.ejercicios || mod.ejercicios.length === 0) && (
                                        <p className="no-exercises">No hay ejercicios en este módulo todavía.</p>
                                    )}
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </main>
        </div>
    );
}