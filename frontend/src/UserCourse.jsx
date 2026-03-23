import { useState, useEffect } from 'react';
import './UserCourse.css';
import { courseService } from './services/api.service';

export default function CourseDetail() {
    const [modules, setModules] = useState([]);
    const [courseName, setCourseName] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showModuleForm, setShowModuleForm] = useState(false);
    const [showExerciseForm, setShowExerciseForm] = useState({});
    const [moduleName, setModuleName] = useState('');
    const [moduleDescription, setModuleDescription] = useState('');
    const [exerciseName, setExerciseName] = useState('');
    const [exerciseStatement, setExerciseStatement] = useState('');
    const [exercisePoints, setExercisePoints] = useState('');

    const token = localStorage.getItem('token');

    const courseId = window.location.pathname.split('/').pop();

    useEffect(() => {
        if (!token) {
            window.history.pushState({}, '', '/auth');
            window.dispatchEvent(new PopStateEvent('popstate'));
            return;
        }
        fetchModules();
    }, []);

    const fetchModules = async () => {
        try {
            const data = await courseService.getCourse(courseId);
            setCourseName(data.nombre);
            setModules(data.modulos ?? []);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    const handleCreateModule = async () => {
        try {
            await courseService.createModule({
                nombre: moduleName,
                descripcion: moduleDescription,
                idCurso: courseId,
            });
            setModuleName('');
            setModuleDescription('');
            setShowModuleForm(false);
            fetchModules();
        } catch (err) {
            alert(err.message);
        }
    };

    const handleCreateExercise = async (moduleId) => {
        try {
            await courseService.createExercise({
                nombre: exerciseName,
                enunciado: exerciseStatement,
                puntos: parseInt(exercisePoints),
                idModulo: moduleId,
            });
            setExerciseName('');
            setExerciseStatement('');
            setExercisePoints('');
            setShowExerciseForm({ ...showExerciseForm, [moduleId]: false });
            fetchModules();
        } catch (err) {
            alert(`Error: ${err.message}`);
        }
    };

    const toggleExerciseForm = (moduleId) => {
        setShowExerciseForm(prev => ({ ...prev, [moduleId]: !prev[moduleId] }));
    };

    const navigateToHome = () => {
        window.history.pushState({}, '', '/');
        window.dispatchEvent(new PopStateEvent('popstate'));
    };

    const navigateToCourses = () => {
        window.history.pushState({}, '', '/courses');
        window.dispatchEvent(new PopStateEvent('popstate'));
    };

    return (
        <div className="profile-layout">
            <nav className="navbar">
                <div className="navbar-logo" onClick={navigateToHome} style={{ cursor: 'pointer' }}>
                    🎓 Flexilearn
                </div>
                <div className="navbar-links">
                    <button onClick={navigateToCourses} className="btn-secondary">Volver a cursos</button>
                </div>
            </nav>

            <main className="profile-main">
                <div className="courses-container">

                    <div className="courses-header">
                        <h1>{courseName}</h1>
                        <p className="profile-email">{modules.length} módulos</p>
                    </div>

                    {loading && <div className="loading-spinner">Cargando módulos...</div>}
                    {error && <div className="profile-card error-card"><p>{error}</p></div>}

                    {!loading && !error && (
                        <>
                            {/* Module list */}
                            <div className="modules-list">
                                {modules.map((mod) => (
                                    <div key={mod.id} className="module-card">
                                        <div className="module-header">
                                            <div>
                                                <h2 className="module-name">{mod.nombre}</h2>
                                                <p className="module-description">{mod.descripcion}</p>
                                            </div>
                                            <button
                                                className="btn-primary"
                                                onClick={() => {
                                                    window.history.pushState({}, '', `/created_courses/${courseId}/modules/${mod.id}/create_exercise`);
                                                    window.dispatchEvent(new PopStateEvent('popstate'));
                                                }}
                                            >
                                                + Ejercicio
                                            </button>
                                        </div>

                                        {/* Exercise form */}
                                        {showExerciseForm[mod.id] && (
                                            <div className="inline-form">
                                                <div className="form-group">
                                                    <label className="form-label">Nombre</label>
                                                    <input
                                                        type="text"
                                                        className="form-input"
                                                        placeholder="Nombre del ejercicio"
                                                        value={exerciseName}
                                                        onChange={(e) => setExerciseName(e.target.value)}
                                                    />
                                                </div>
                                                <div className="form-group">
                                                    <label className="form-label">Enunciado</label>
                                                    <textarea
                                                        className="form-input form-textarea"
                                                        placeholder="Descripción del ejercicio..."
                                                        rows={3}
                                                        value={exerciseStatement}
                                                        onChange={(e) => setExerciseStatement(e.target.value)}
                                                    />
                                                </div>
                                                <div className="form-group">
                                                    <label className="form-label">Puntos</label>
                                                    <input
                                                        type="number"
                                                        className="form-input"
                                                        placeholder="Ej. 10"
                                                        value={exercisePoints}
                                                        onChange={(e) => setExercisePoints(e.target.value)}
                                                    />
                                                </div>
                                                <div className="inline-form-actions">
                                                    <button className="btn-primary" onClick={() => handleCreateExercise(mod.id)}>
                                                        Guardar ejercicio
                                                    </button>
                                                    <button className="btn-secondary" onClick={() => toggleExerciseForm(mod.id)}>
                                                        Cancelar
                                                    </button>
                                                </div>
                                            </div>
                                        )}

                                        {/* Exercise list */}
                                        {mod.ejercicios && mod.ejercicios.length > 0 && (
                                            <div className="exercises-list">
                                                {mod.ejercicios.map((ex) => (
                                                    <div key={ex.id} className="exercise-card">
                                                        <div className="exercise-info">
                                                            <span className="exercise-name">{ex.nombre}</span>
                                                            <span className="exercise-statement">{ex.enunciado}</span>
                                                        </div>
                                                        <span className="exercise-points">⭐ {ex.puntos} pts</span>
                                                    </div>
                                                ))}
                                            </div>
                                        )}
                                    </div>
                                ))}
                            </div>

                            {/* Create module form */}
                            {showModuleForm && (
                                <div className="profile-card create-course-card" style={{ marginTop: '1.5rem' }}>
                                    <div className="create-course-form">
                                        <div className="profile-info">
                                            <h2>Nuevo módulo</h2>
                                        </div>
                                        <div className="form-group">
                                            <label className="form-label">Nombre del módulo</label>
                                            <input
                                                type="text"
                                                className="form-input"
                                                placeholder="Ej. Introducción"
                                                value={moduleName}
                                                onChange={(e) => setModuleName(e.target.value)}
                                            />
                                        </div>
                                        <div className="form-group">
                                            <label className="form-label">Descripción</label>
                                            <textarea
                                                className="form-input form-textarea"
                                                placeholder="Describe el módulo..."
                                                rows={3}
                                                value={moduleDescription}
                                                onChange={(e) => setModuleDescription(e.target.value)}
                                            />
                                        </div>
                                        <div className="profile-actions">
                                            <button className="btn-primary btn-full" onClick={handleCreateModule}>
                                                Crear módulo
                                            </button>
                                            <button className="btn-secondary btn-full" onClick={() => setShowModuleForm(false)}>
                                                Cancelar
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            )}

                            {/* Add module button */}
                            {!showModuleForm && (
                                <button className="btn-primary add-module-btn" onClick={() => setShowModuleForm(true)}>
                                    + Añadir módulo
                                </button>
                            )}
                        </>
                    )}
                </div>
            </main>
        </div>
    );
}