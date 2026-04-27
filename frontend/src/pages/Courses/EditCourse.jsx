import { useState, useEffect } from 'react';
import Navbar from '../../components/layout/Navbar';
import { useNavigate, useParams } from 'react-router-dom';
import './EditCourse.css';
import { courseService } from '../../services/api.service';

const DIFFICULTY_OPTIONS = {
    FACIL: 'facil',
    MEDIO: 'medio',
    DIFICIL: 'dificil'
};

export default function EditCourse() {
    const navigate = useNavigate();
    const { id } = useParams();
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState(null);
    
    // Datos del formulario
    const [courseData, setCourseData] = useState({
        nombre: '',
        categoria: '',
        descripcion: '',
        dificultad: '',
        modulos: []
    });

    const token = localStorage.getItem('token');

    useEffect(() => {
        if (!token) {
            navigate('/auth');
            return;
        }
        loadCourse();
    }, [id, token]);

    const loadCourse = async () => {
        try {
            const data = await courseService.getCourse(id);
            setCourseData({
                nombre: data.nombre,
                categoria: data.categoria,
                descripcion: data.descripcion,
                dificultad: data.dificultad.toLowerCase(),
                modulos: data.modulos || []
            });
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    const handleCourseChange = (field, value) => {
        setCourseData(prev => ({ ...prev, [field]: value }));
    };

    const handleModuleChange = (index, field, value) => {
        const updatedModules = [...courseData.modulos];
        updatedModules[index] = { ...updatedModules[index], [field]: value };
        setCourseData(prev => ({ ...prev, modulos: updatedModules }));
    };

    const handleExerciseChange = (modIndex, exIndex, field, value) => {
        const updatedModules = [...courseData.modulos];
        updatedModules[modIndex].ejercicios[exIndex] = {
            ...updatedModules[modIndex].ejercicios[exIndex],
            [field]: value
        };
        setCourseData(prev => ({ ...prev, modulos: updatedModules }));
    };

    const addModule = () => {
        setCourseData(prev => ({
            ...prev,
            modulos: [...prev.modulos, {
                id: null,
                nombre: '',
                descripcion: '',
                ejercicios: []
            }]
        }));
    };

    const removeModule = (index) => {
        const updatedModules = courseData.modulos.filter((_, i) => i !== index);
        setCourseData(prev => ({ ...prev, modulos: updatedModules }));
    };

    const addExercise = (modIndex) => {
        const updatedModules = [...courseData.modulos];
        updatedModules[modIndex].ejercicios = [
            ...(updatedModules[modIndex].ejercicios || []),
            {
                id: null,
                nombre: '',
                teoria: '',
                enunciado: '',
                codigoInicial: '',
                puntos: 0,
                lenguaje: 'javascript'
            }
        ];
        setCourseData(prev => ({ ...prev, modulos: updatedModules }));
    };

    const removeExercise = (modIndex, exIndex) => {
        const updatedModules = [...courseData.modulos];
        updatedModules[modIndex].ejercicios = updatedModules[modIndex].ejercicios.filter((_, i) => i !== exIndex);
        setCourseData(prev => ({ ...prev, modulos: updatedModules }));
    };

    const saveCourse = async () => {
        setSaving(true);
        try {
            await courseService.updateCourse(id, courseData);
            navigate(`/courses/${id}`);
        } catch (err) {
            setError(err.message);
            setSaving(false);
        }
    };

    const navigateToCourse = () => {
        navigate(`/courses/${id}`);
    };

    if (loading) {
        return (
            <div className="profile-layout">
                <Navbar>
                    <button onClick={navigateToCourse} className="btn-secondary">Volver al curso</button>
                </Navbar>
                <main className="profile-main">
                    <div className="loading-spinner">Cargando curso...</div>
                </main>
            </div>
        );
    }

    return (
        <div className="profile-layout">
            <Navbar>
                <button onClick={navigateToCourse} className="btn-secondary">Volver al curso</button>
            </Navbar>

            <main className="profile-main">
                <div className="edit-course-container">
                    <div className="edit-course-header">
                        <h1>Editar curso</h1>
                        <p className="subtitle">Modifica los datos del curso, módulos y ejercicios</p>
                    </div>

                    {error && (
                        <div className="error-card">
                            <p>{error}</p>
                        </div>
                    )}

                    <div className="edit-course-form">
                        {/* Datos del curso */}
                        <section className="form-section">
                            <h2>Datos del curso</h2>
                            
                            <div className="form-group">
                                <label>Nombre del curso *</label>
                                <input
                                    type="text"
                                    value={courseData.nombre}
                                    onChange={(e) => handleCourseChange('nombre', e.target.value)}
                                    placeholder="Ej. Introducción a React"
                                />
                            </div>

                            <div className="form-row">
                                <div className="form-group">
                                    <label>Categoría *</label>
                                    <input
                                        type="text"
                                        value={courseData.categoria}
                                        onChange={(e) => handleCourseChange('categoria', e.target.value)}
                                        placeholder="Ej. Programación"
                                    />
                                </div>

                                <div className="form-group">
                                    <label>Dificultad *</label>
                                    <select
                                        value={courseData.dificultad}
                                        onChange={(e) => handleCourseChange('dificultad', e.target.value)}
                                    >
                                        <option value="facil">Fácil</option>
                                        <option value="medio">Medio</option>
                                        <option value="dificil">Difícil</option>
                                    </select>
                                </div>
                            </div>

                            <div className="form-group">
                                <label>Descripción *</label>
                                <textarea
                                    value={courseData.descripcion}
                                    onChange={(e) => handleCourseChange('descripcion', e.target.value)}
                                    rows={4}
                                    placeholder="Describe de qué trata el curso..."
                                />
                            </div>
                        </section>

                        {/* Módulos y ejercicios */}
                        <section className="form-section">
                            <div className="section-header">
                                <h2>Módulos y ejercicios</h2>
                                <button type="button" className="btn-secondary" onClick={addModule}>
                                    + Añadir módulo
                                </button>
                            </div>

                            {courseData.modulos.length === 0 && (
                                <p className="empty-message">No hay módulos. Haz clic en "Añadir módulo" para empezar.</p>
                            )}

                            {courseData.modulos.map((modulo, modIndex) => (
                                <div key={modIndex} className="module-editor">
                                    <div className="module-header">
                                        <h3>Módulo {modIndex + 1}</h3>
                                        <button 
                                            type="button" 
                                            className="btn-danger btn-small"
                                            onClick={() => removeModule(modIndex)}
                                        >
                                            Eliminar módulo
                                        </button>
                                    </div>

                                    <div className="form-group">
                                        <label>Nombre del módulo *</label>
                                        <input
                                            type="text"
                                            value={modulo.nombre}
                                            onChange={(e) => handleModuleChange(modIndex, 'nombre', e.target.value)}
                                            placeholder="Ej. Fundamentos de React"
                                        />
                                    </div>

                                    <div className="form-group">
                                        <label>Descripción</label>
                                        <textarea
                                            value={modulo.descripcion}
                                            onChange={(e) => handleModuleChange(modIndex, 'descripcion', e.target.value)}
                                            rows={2}
                                            placeholder="Breve descripción del módulo..."
                                        />
                                    </div>

                                    {/* Ejercicios del módulo */}
                                    <div className="exercises-section">
                                        <div className="subsection-header">
                                            <h4>Ejercicios</h4>
                                            <button 
                                                type="button" 
                                                className="btn-secondary btn-small"
                                                onClick={() => addExercise(modIndex)}
                                            >
                                                + Añadir ejercicio
                                            </button>
                                        </div>

                                        {(!modulo.ejercicios || modulo.ejercicios.length === 0) && (
                                            <p className="empty-message-small">No hay ejercicios en este módulo.</p>
                                        )}

                                        {modulo.ejercicios && modulo.ejercicios.map((ejercicio, exIndex) => (
                                            <div key={exIndex} className="exercise-editor">
                                                <div className="exercise-header">
                                                    <h5>Ejercicio {exIndex + 1}</h5>
                                                    <button 
                                                        type="button"
                                                        className="btn-danger btn-small"
                                                        onClick={() => removeExercise(modIndex, exIndex)}
                                                    >
                                                        Eliminar
                                                    </button>
                                                </div>

                                                <div className="form-row">
                                                    <div className="form-group">
                                                        <label>Nombre *</label>
                                                        <input
                                                            type="text"
                                                            value={ejercicio.nombre}
                                                            onChange={(e) => handleExerciseChange(modIndex, exIndex, 'nombre', e.target.value)}
                                                            placeholder="Ej. Hola Mundo con JSX"
                                                        />
                                                    </div>

                                                    <div className="form-group">
                                                        <label>Puntos *</label>
                                                        <input
                                                            type="number"
                                                            value={ejercicio.puntos}
                                                            onChange={(e) => handleExerciseChange(modIndex, exIndex, 'puntos', parseInt(e.target.value) || 0)}
                                                            placeholder="10"
                                                            min="0"
                                                        />
                                                    </div>

                                                    <div className="form-group">
                                                        <label>Lenguaje *</label>
                                                        <select
                                                            value={ejercicio.lenguaje}
                                                            onChange={(e) => handleExerciseChange(modIndex, exIndex, 'lenguaje', e.target.value)}
                                                        >
                                                            <option value="javascript">JavaScript</option>
                                                            <option value="python">Python</option>
                                                            <option value="java">Java</option>
                                                            <option value="cpp">C++</option>
                                                            <option value="html">HTML/CSS</option>
                                                        </select>
                                                    </div>
                                                </div>

                                                <div className="form-group">
                                                    <label>Teoría</label>
                                                    <textarea
                                                        value={ejercicio.teoria}
                                                        onChange={(e) => handleExerciseChange(modIndex, exIndex, 'teoria', e.target.value)}
                                                        rows={3}
                                                        placeholder="Explicación teórica del ejercicio..."
                                                    />
                                                </div>

                                                <div className="form-group">
                                                    <label>Enunciado *</label>
                                                    <textarea
                                                        value={ejercicio.enunciado}
                                                        onChange={(e) => handleExerciseChange(modIndex, exIndex, 'enunciado', e.target.value)}
                                                        rows={3}
                                                        placeholder="Descripción de lo que debe hacer el alumno..."
                                                    />
                                                </div>

                                                <div className="form-group">
                                                    <label>Código inicial</label>
                                                    <textarea
                                                        value={ejercicio.codigoInicial}
                                                        onChange={(e) => handleExerciseChange(modIndex, exIndex, 'codigoInicial', e.target.value)}
                                                        rows={5}
                                                        placeholder="// Código base para que el alumno empiece..."
                                                        className="code-editor"
                                                    />
                                                </div>
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            ))}
                        </section>

                        {/* Botones de acción */}
                        <div className="form-actions">
                            <button 
                                className="btn-primary btn-large"
                                onClick={saveCourse}
                                disabled={saving}
                            >
                                {saving ? 'Guardando...' : 'Guardar cambios'}
                            </button>
                            <button 
                                className="btn-secondary btn-large"
                                onClick={navigateToCourse}
                            >
                                Cancelar
                            </button>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    );
}