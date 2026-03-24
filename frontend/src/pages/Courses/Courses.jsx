import { useState, useEffect } from 'react';
import Navbar from '../../components/layout/Navbar';
import { useNavigate } from 'react-router-dom';
import './Courses.css';
import { courseService } from '../../services/api.service';

const DIFFICULTY_LABELS = {
    FACIL: { label: 'Fácil', className: 'badge-easy' },
    MEDIO: { label: 'Medio', className: 'badge-medium' },
    DIFICIL: { label: 'Difícil', className: 'badge-hard' },
};

export default function CourseList() {
    const navigate = useNavigate();
    const [courses, setCourses] = useState([]);
    const [enrolledCourses, setEnrolledCourses] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const token = localStorage.getItem('token');

    useEffect(() => {

        if (!token) {
            navigate('/auth');

            return;
        }

        const fetchCourses = async () => {
        try {
            const [allData, enrolledData] = await Promise.all([
                courseService.getAllCourses(),
                courseService.getEnrolledCourses(),
            ]);
            setCourses(allData);
            setEnrolledCourses(enrolledData.map(c => c.id));
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

        fetchCourses();
    }, [navigate, token]);

        const enrollToCourse = async (id) => {
            try {
                await courseService.enroll(id);
                setEnrolledCourses(prev => [...prev, id]);
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
            
        };

    const navigateToProfile = () => {
        navigate('/profile');

    };

    const enroll = (id) => {
        enrollToCourse(id);

    };

    return (

        
        <div className="profile-layout">
            <Navbar>
                <button onClick={navigateToProfile} className="btn-secondary">Volver al perfil</button>
            </Navbar>

            <main className="profile-main">
                <div className="courses-container">
                    <div className="courses-header">
                        <h1>Cursos disponibles</h1>
                        <p className="profile-email">{courses.length} cursos encontrados</p>
                    </div>

                    {loading && (
                        <div className="loading-spinner">Cargando cursos...</div>
                    )}

                    {error && (
                        <div className="profile-card error-card">
                            <p>{error}</p>
                        </div>
                    )}

                    {!loading && !error && (
                        <div className="courses-grid">
                            {courses.map((course) => {
                                const isEnrolled = enrolledCourses.includes(course.id);
                                const diff = DIFFICULTY_LABELS[course.dificultad] || { label: course.dificultad, className: '' };
                                return (
                                    <div key={course.id} className="course-card"
                                                    onClick={(e) => {
                                                        e.stopPropagation();
                                                        if (isEnrolled) {
                                                            navigate(`/courses/${course.id}`);
                                                        } else {
                                                            enroll(course.id);
                                                        }
                                                    }}>
                                        <span className="course-category">{course.usuario.nombre}</span>
                                        <div className="course-card-header">
                                            <span className="course-category">{course.categoria}</span>
                                            <span className={`badge-difficulty ${diff.className}`}>{diff.label}</span>
                                        </div>
                                        <h2 className="course-name">{course.nombre}</h2>
                                        <p className="course-description">{course.descripcion}</p>
                                        <div className="course-card-footer">
                                            <span className="course-modules">
                                                📦 {course.modulos.length} módulo{course.modulos.length !== 1 ? 's' : ''}
                                            </span>
                                                <button
                                                    className={isEnrolled ? "btn-secondary" : "btn-primary"}>
                                                    {isEnrolled ? 'Ver curso →' : 'Matricúlate →'}
                                                </button>
                                        </div>
                                    </div>
                                );
                            })}
                        </div>
                    )}
                </div>
            </main>
        </div>
    );
}




