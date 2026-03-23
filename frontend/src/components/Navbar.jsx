import React from 'react';
import { useNavigate } from 'react-router-dom';

export default function Navbar({ children }) {
    const navigate = useNavigate();

    const navigateToHome = () => {
        navigate('/');
    };

    return (
        <nav className="navbar">
            <div
                className="navbar-logo"
                onClick={navigateToHome}
                style={{ cursor: 'pointer' }}
            >
                🎓 Flexilearn
            </div>
            {children && (
                <div className="navbar-links">
                    {children}
                </div>
            )}
        </nav>
    );
}