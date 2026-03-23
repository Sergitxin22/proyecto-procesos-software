import React from 'react';
import Navbar from '../../components/layout/Navbar';
import { useNavigate } from 'react-router-dom';
import './Landing.css';

export default function Landing() {
    const navigate = useNavigate();
    const navigateToAuth = () => {
        navigate('/auth');
        const navEvent = new PopStateEvent('popstate');
        window.dispatchEvent(navEvent);
    };

    return (
        <div className="landing-container">
            <Navbar>
                <a href="#about">Sobre nosotros</a>
                    <a href="#features">Características</a>
                    <button onClick={navigateToAuth} className="btn-primary">Entrar</button>
            </Navbar>

            <main>
                <header className="hero">
                    <div className="hero-content">
                        <h1>Aprende haciendo, no solo mirando.</h1>
                        <p className="hero-subtitle">
                            Flexilearn es tu plataforma gratuita de <strong>openlearning</strong>. Apúntate a cursos o crea los tuyos propios enfocados en la práctica real.
                        </p>
                        <div className="hero-actions">
                            <button onClick={navigateToAuth} className="btn-primary btn-large">Comenzar a aprender</button>
                            <button onClick={navigateToAuth} className="btn-secondary btn-large">Crear un curso</button>
                        </div>
                    </div>
                    <div className="hero-image">
                        <div className="mockup-window">
                            <div className="mockup-header">
                                <span className="dot red"></span>
                                <span className="dot yellow"></span>
                                <span className="dot green"></span>
                            </div>
                            <div className="mockup-body">
                                <code>
                                    <span className="code-comment">// Entorno de pruebas integrado</span><br />
                                    <span className="code-keyword">function</span> <span className="code-func">aprender</span>() {'{'}<br />
                                    &nbsp;&nbsp;<span className="code-keyword">return</span> <span className="code-string">"¡Práctica real desde el navegador!"</span>;<br />
                                    {'}'}<br />
                                    <br />
                                    <span className="code-func">console.log</span>(<span className="code-func">aprender</span>());
                                </code>
                            </div>
                        </div>
                    </div>
                </header>

                <section id="about" className="section bg-light">
                    <div className="section-content text-center">
                        <h2>¿Qué es Flexilearn?</h2>
                        <p className="lead">
                            Nacimos con la idea de revolucionar el aprendizaje online. En vez de consumir contenido estático, Flexilearn te permite ejecutar, tocar y romper código (de forma segura) en un <strong>playground interactivo integrado directamente en la web</strong>.
                        </p>
                    </div>
                </section>

                <section id="features" className="section">
                    <div className="section-content">
                        <h2 className="text-center">Características principales</h2>
                        <div className="features-grid">
                            <div className="feature-card">
                                <div className="feature-icon">🚀</div>
                                <h3>100% Práctico</h3>
                                <p>Todos nuestros cursos están diseñados para que practiques desde el minuto uno. Escribe código y mira los resultados al instante en el playground integrado.</p>
                            </div>
                            <div className="feature-card">
                                <div className="feature-icon">🎓</div>
                                <h3>Estudia a tu ritmo</h3>
                                <p>Como plataforma openlearn gratuita, puedes apuntarte a todos los cursos que desees, sin costes ocultos ni suscripciones premium.</p>
                            </div>
                            <div className="feature-card">
                                <div className="feature-icon">✍️</div>
                                <h3>Crea tus cursos</h3>
                                <p>¿Eres un experto en algún área? Transforma tu conocimiento en un curso interactivo y compártelo con miles de estudiantes de todo el mundo.</p>
                            </div>
                        </div>
                    </div>
                </section>
            </main>

            <footer className="footer">
                <div className="footer-content">
                    <div className="footer-brand">
                        🎓 Flexilearn
                        <p>La plataforma openlearning del futuro.</p>
                    </div>
                    <div className="footer-links">
                        <h4>Enlaces</h4>
                        <a href="#">Privacidad</a>
                        <a href="#">Términos</a>
                        <a href="#">Contacto</a>
                    </div>
                </div>
                <div className="footer-bottom">
                    <p>&copy; {new Date().getFullYear()} Flexilearn. Todos los derechos reservados.</p>
                </div>
            </footer>
        </div>
    );
}




