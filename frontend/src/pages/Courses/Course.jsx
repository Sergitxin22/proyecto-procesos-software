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

    const token = localStorage.getItem('token');
    const courseId = window.location.pathname.split('/').pop();

    useEffect(() => {
        if (!token) {
            navigate('/auth');
            return;
        }
        fetchModules();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [navigate, token]);

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