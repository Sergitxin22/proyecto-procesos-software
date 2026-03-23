import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './Auth.css';
import { authService } from '../services/api.service';

export default function Auth() {
    const navigate = useNavigate();
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

        try {
            if (isLogin) {
                const data = await authService.login(formData.email, formData.password);
                showMessage('Inicio de sesión exitoso', 'success');
                localStorage.setItem('token', data.token);
                setTimeout(() => {
                    navigate('/profile');

                }, 1500);
            } else {
                await authService.register(formData.nombre, formData.email, formData.password);
                showMessage('Registro exitoso. Ahora puedes entrar.', 'success');
                setIsLogin(true); // switch to login after successful register
            }
        } catch (err) {
            showMessage(err.message);
        }
    };

    const navigateHome = () => {
        navigate('/');

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
