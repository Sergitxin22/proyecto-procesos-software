import { useState, useEffect, useRef } from 'react';
import Navbar from '../../components/layout/Navbar';
import { useNavigate } from 'react-router-dom';
import './Forum.css';
import { courseService } from '../../services/api.service';

export default function Forum() {
    const navigate = useNavigate();
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState('');
    const [loading, setLoading] = useState(true);
    const [sending, setSending] = useState(false);
    const [error, setError] = useState(null);
    const bottomRef = useRef(null);

    const token = localStorage.getItem('token');
    const courseId = window.location.pathname.split('/')[2];

    useEffect(() => {
        if (!token) {
            navigate('/auth');
            return;
        }
        fetchMessages();
    }, []);

    useEffect(() => {
        bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [messages]);

    const fetchMessages = async () => {
        try {
            const data = await courseService.getMessages(courseId)
            console.log(data)
            setMessages(data);
        } catch (err) {
            setError(`Error de conexión: ${err.message}`);
        } finally {
            setLoading(false);
        }
    };

    const handleSend = async () => {
    if (!newMessage.trim()) return;
    setSending(true);
    try {
        await courseService.sendMessage(courseId, newMessage);
        setNewMessage('');
        await fetchMessages();
    } catch (err) {
        alert(`Error de conexión: ${err.message}`);
    } finally {
        setSending(false);
    }
};

    const handleKeyDown = (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            handleSend();
        }
    };

    const formatDate = (dateStr) => {
        const date = new Date(dateStr);
        return date.toLocaleDateString('es-ES', {
            day: '2-digit', month: 'short', year: 'numeric',
            hour: '2-digit', minute: '2-digit',
        });
    };

    const getInitial = (username) => username?.charAt(0).toUpperCase() || '?';

    return (
        <div className="profile-layout">
            <Navbar>
                <button onClick={() => navigate(-1)} className="btn-secondary">Volver al curso</button>
            </Navbar>

            <main className="forum-main">
                <div className="forum-container">
                    <div className="forum-header">
                        <h1>Foro del curso</h1>
                        <p className="profile-email">{messages.length} mensajes</p>
                    </div>

                    <div className="forum-messages">
                        {loading && <div className="loading-spinner">Cargando mensajes...</div>}
                        {error && <div className="profile-card error-card"><p>{error}</p></div>}

                        {!loading && !error && messages.length === 0 && (
                            <div className="forum-empty">
                                <span className="forum-empty-icon">💬</span>
                                <p>No hay mensajes todavía. ¡Sé el primero en escribir!</p>
                            </div>
                        )}

                        {!loading && !error && messages.map((msg, index) => (
                            <div key={index} className="forum-message">
                                <div className="message-avatar">
                                    {getInitial(msg.username)}
                                </div>
                                <div className="message-body">
                                    <div className="message-header">
                                        <span className="message-username">{msg.username}</span>
                                        <span className="message-date">{formatDate(msg.date)}</span>
                                    </div>
                                    <p className="message-text">{msg.mensaje}</p>
                                </div>
                            </div>
                        ))}
                        <div ref={bottomRef} />
                    </div>

                    <div className="forum-input-area">
                        <textarea
                            className="forum-input"
                            placeholder="Escribe un mensaje... (Enter para enviar)"
                            value={newMessage}
                            onChange={(e) => setNewMessage(e.target.value)}
                            onKeyDown={handleKeyDown}
                            rows={3}
                        />
                        <button
                            className="btn-primary forum-send-btn"
                            onClick={handleSend}
                            disabled={sending || !newMessage.trim()}
                        >
                            {sending ? 'Enviando...' : 'Enviar mensaje →'}
                        </button>
                    </div>
                </div>
            </main>
        </div>
    );
}