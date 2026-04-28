import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../../components/layout/Navbar';
import { authService, courseService } from '../../services/api.service';
import './Admin.css';
import './CourseList.css';

const DIFFICULTY_LABELS = {
    FACIL: 'Fácil',
    MEDIO: 'Medio',
    DIFICIL: 'Difícil',
};

export default function CourseList() {
    const navigate = useNavigate();
    const [courses, setCourses] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [search, setSearch] = useState('');
    const [deletingId, setDeletingId] = useState(null);

    const token = localStorage.getItem('token');

    useEffect(() => {
        if (!token) {
            navigate('/auth');
            return;
        }

        const fetchCourses = async () => {
            try {
                const data = await courseService.getAllCourses();
                setCourses(data);
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        fetchCourses();
    }, [navigate, token]);

    const handleLogout = async () => {
        try {
            await authService.logout();
        } catch (e) {
            console.error(e);
        }

        localStorage.removeItem('token');
        navigate('/');
    };

    const navigateToAdmin = () => {
        navigate('/admin');
    };

    const filteredCourses = useMemo(() => {
        const normalizedSearch = search.trim().toLowerCase();
        if (!normalizedSearch) {
            return courses;
        }

        return courses.filter((course) => {
            const teacherName = course.usuario?.nombre || '';
            return (
                course.nombre.toLowerCase().includes(normalizedSearch)
                || course.categoria.toLowerCase().includes(normalizedSearch)
                || teacherName.toLowerCase().includes(normalizedSearch)
            );
        });
    }, [courses, search]);

    const handleDeleteCourse = async (course) => {
        if (!window.confirm(`¿Seguro que quieres eliminar el curso "${course.nombre}"? Esta acción dejará de dar acceso a alumnos y profesores.`)) {
            return;
        }

        setDeletingId(course.id);
        try {
            await courseService.deleteCurso(course.id);
            setCourses((prev) => prev.filter((c) => c.id !== course.id));
        } catch (err) {
            alert(`No se pudo eliminar el curso: ${err.message}`);
        } finally {
            setDeletingId(null);
        }
    };

    if (loading) {
        return (
            <div className="profile-wrapper">
                <div className="loading-spinner">Cargando cursos...</div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="profile-wrapper">
                <div className="profile-card error-card">
                    <h2>Oops, hubo un problema</h2>
                    <p>{error}</p>
                    <button onClick={navigateToAdmin} className="btn-primary">Volver al panel</button>
                </div>
            </div>
        );
    }

    return (
        <div className="profile-layout">
            <Navbar>
                <a onClick={navigateToAdmin} style={{ cursor: 'pointer' }}>Panel Admin</a>
                <button onClick={handleLogout} className="btn-secondary">Cerrar sesión</button>
            </Navbar>

            <main className="profile-main">
                <div className="course-admin-card">
                    <div className="profile-info course-admin-header">
                        <h1>Lista de cursos</h1>
                        <p className="profile-email">{courses.length} cursos registrados</p>
                    </div>

                    <div className="course-admin-search-wrapper">
                        <input
                            className="course-admin-search"
                            type="text"
                            placeholder="Buscar por nombre, categoría o profesor..."
                            value={search}
                            onChange={(e) => setSearch(e.target.value)}
                        />
                    </div>

                    {filteredCourses.length === 0 ? (
                        <p className="course-admin-empty">No se encontraron cursos.</p>
                    ) : (
                        <table className="course-admin-table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Curso</th>
                                    <th>Categoría</th>
                                    <th>Dificultad</th>
                                    <th>Profesor</th>
                                    <th>Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                {filteredCourses.map((course) => {
                                    const difficultyLabel = DIFFICULTY_LABELS[course.dificultad] || course.dificultad;
                                    const moduleCount = course.modulos?.length || 0;
                                    return (
                                        <tr key={course.id}>
                                            <td>{course.id}</td>
                                            <td>
                                                <div className="course-admin-title">{course.nombre}</div>
                                                <div className="course-admin-description">{course.descripcion}</div>
                                                <div className="course-admin-meta">{moduleCount} módulo{moduleCount !== 1 ? 's' : ''}</div>
                                            </td>
                                            <td>{course.categoria}</td>
                                            <td>{difficultyLabel}</td>
                                            <td>{course.usuario?.nombre || 'Sin asignar'}</td>
                                            <td>
                                                <button
                                                    onClick={() => handleDeleteCourse(course)}
                                                    disabled={deletingId === course.id}
                                                    className="course-admin-delete"
                                                >
                                                    {deletingId === course.id ? 'Eliminando...' : 'Eliminar'}
                                                </button>
                                            </td>
                                        </tr>
                                    );
                                })}
                            </tbody>
                        </table>
                    )}

                    <div className="profile-actions course-admin-actions">
                        <button className="btn-primary btn-full" onClick={navigateToAdmin}>
                            ← Volver al panel
                        </button>
                    </div>
                </div>
            </main>
        </div>
    );
}
