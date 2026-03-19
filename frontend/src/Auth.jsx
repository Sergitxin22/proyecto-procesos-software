import { useState } from 'react';
import './Auth.css';

export default function Auth() {
    const [isLogin, setIsLogin] = useState(true);
    const [formData, setFormData] = useState({
        nombre: '',
        email: '',
        password: ''
    });
    const [message, setMessage] = useState({ text: '', type: '' });

    const API_URL = "http://localhost:8080/api/auth";

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const showMessage = (text, type = 'error') => {
        setMessage({ text, type });
        setTimeout(() => setMessage({ text: '', type: '' }), 5000);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const endpoint = isLogin ? '/login' : '/registro';

        try {
            const res = await fetch(`${API_URL}${endpoint}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(
                    isLogin
                        ? { email: formData.email, password: formData.password }
                        : formData
                )
            });
            const data = await res.json();

            if (res.ok) {
                showMessage(isLogin ? 'Inicio de sesión exitoso' : 'Registro exitoso. Ahora puedes entrar.', 'success');
                if (!isLogin) {
                    setIsLogin(true); // switch to login after successful register
                } else {
                    // Here you would save the token to localStorage and redirect to a dashboard
                    localStorage.setItem('token', data.token);
                    setTimeout(() => {
                        window.history.pushState({}, '', '/profile');
                        window.dispatchEvent(new PopStateEvent('popstate'));
                    }, 1500);
                }
            } else {
                showMessage(data.mensaje || 'Error en la autenticación');
            }
        } catch (err) {
            showMessage(`Error de conexión: ${err.message}`);
        }
    };

    const navigateHome = () => {
        window.history.pushState({}, '', '/');
        window.dispatchEvent(new PopStateEvent('popstate'));
    };

    return (
        <div className="auth-wrapper">
            <div className="auth-container">
                <div className="auth-header">
                    <span className="auth-logo" onClick={navigateHome}>🎓 Flexilearn</span>
                    <h2>{isLogin ? 'Bienvenido de nuevo' : 'Crea tu cuenta'}</h2>
                    <p>{isLogin ? 'Inicia sesión para continuar con tu aprendizaje' : 'Únete a la mejor plataforma de openlearning'}</p>
                </div>

                {message.text && (
                    <div className={`auth-alert auth-alert-${message.type}`}>
                        {message.text}
                    </div>
                )}

                <form className="auth-form" onSubmit={handleSubmit}>
                    {!isLogin && (
                        <div className="form-group">
                            <label>Nombre completo</label>
                            <input
                                type="text"
                                name="nombre"
                                value={formData.nombre}
                                onChange={handleChange}
                                placeholder="Ej. María García"
                                required
                            />
                        </div>
                    )}
                    <div className="form-group">
                        <label>Correo electrónico</label>
                        <input
                            type="email"
                            name="email"
                            value={formData.email}
                            onChange={handleChange}
                            placeholder="tu@correo.com"
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label>Contraseña</label>
                        <input
                            type="password"
                            name="password"
                            value={formData.password}
                            onChange={handleChange}
                            placeholder="••••••••"
                            required
                        />
                    </div>
                    <button type="submit" className="auth-btn-submit">
                        {isLogin ? 'Entrar' : 'Registrarse'}
                    </button>
                </form>

                <div className="auth-footer">
                    <p>
                        {isLogin ? '¿No tienes cuenta? ' : '¿Ya tienes cuenta? '}
                        <button
                            className="auth-link-btn"
                            onClick={() => setIsLogin(!isLogin)}
                            type="button"
                        >
                            {isLogin ? 'Regístrate aquí' : 'Inicia sesión'}
                        </button>
                    </p>
                </div>
            </div>
        </div>
    );
}