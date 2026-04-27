const API_BASE_URL = "http://localhost:8080/api";

const getHeaders = (requireAuth = true) => {
    const headers = { 'Content-Type': 'application/json' };
    if (requireAuth) {
        const token = localStorage.getItem('token');
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }
    }
    return headers;
};

// Interceptor básico para manejar respuestas
const handleResponse = async (res) => {
    const isJson = res.headers.get('content-type')?.includes('application/json');
    const data = isJson ? await res.json() : await res.text();
    console.log(data)

    if (!res.ok) {
        const errorMsg = (data && data.mensaje) || (typeof data === 'string' ? data : "Error en la petición");
        throw new Error(errorMsg);
    }
    return data;
};

export const authService = {
    login: async (email, password) => {
        const res = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: getHeaders(false),
            body: JSON.stringify({ email, password })
        });
        return handleResponse(res);
    },

    register: async (nombre, email, password) => {
        const res = await fetch(`${API_BASE_URL}/auth/registro`, {
            method: 'POST',
            headers: getHeaders(false),
            body: JSON.stringify({ nombre, email, password })
        });
        return handleResponse(res);
    },

    logout: async () => {
        const token = localStorage.getItem('token');
        if (!token) return;
        const res = await fetch(`${API_BASE_URL}/auth/logout`, {
            method: 'POST',
            headers: getHeaders(true)
        });
        return handleResponse(res);
    },

    delete: async () => {
        const token = localStorage.getItem('token');
        if (!token) return;
        const res = await fetch(`${API_BASE_URL}/auth/delete`, {
            method: 'DELETE',
            headers: getHeaders(true)
        });
        return handleResponse(res);
    },

    getUserProfile: async () => {
        const res = await fetch(`${API_BASE_URL}/auth/user`, {
            method: 'GET',
            headers: getHeaders(true)
        });
        return handleResponse(res);
    },

    getAllUsers: async () => {
        // En TestAuth.jsx lo tienes como GET sin token aparente, pero por seguridad general lo dejamos preparado
        const res = await fetch(`${API_BASE_URL}/auth/users`, {
            method: 'GET',
            headers: getHeaders(true)
        });
        return handleResponse(res);
    }
};

export const userService = {
    getCreatedCourses: async () => {
        const res = await fetch(`${API_BASE_URL}/users/createdCourses`, {
            method: 'GET',
            headers: getHeaders(true)
        });
        return handleResponse(res);
    }
};

export const courseService = {
    createCourse: async (courseData) => {
        const res = await fetch(`${API_BASE_URL}/courses/`, {
            method: 'POST',
            headers: getHeaders(true),
            body: JSON.stringify(courseData)
        });
        return handleResponse(res);
    },

    getEnrolledCourses: async () => {
        const res = await fetch(`${API_BASE_URL}/courses/enrolled`, {
            method: 'GET',
            headers: getHeaders(true),
        });
        return handleResponse(res);
    },

    enroll: async (id) => {
        const res = await fetch(`${API_BASE_URL}/courses/${id}/enroll`, {
            method: 'POST',
            headers: getHeaders(true),
        });
        return handleResponse(res);
    },

    getCourse: async (id) => {
        const res = await fetch(`${API_BASE_URL}/courses/${id}/`, {
            method: 'GET',
            headers: getHeaders(true)
        });
        return handleResponse(res);
    },

    getAllCourses: async () => {
        const res = await fetch(`${API_BASE_URL}/courses/`, {
            method: 'GET',
        });
        return handleResponse(res);
    },

    createModule: async (moduleData) => {
        // En tu backend CreateModule es POST /api/courses/modules
        const res = await fetch(`${API_BASE_URL}/courses/modules`, {
            method: 'POST',
            headers: getHeaders(true),  // Usamos el token si está disponible
            body: JSON.stringify(moduleData)
        });
        return handleResponse(res);
    },

    createExercise: async (exerciseData) => {
        // POST /api/courses/exercises
        const res = await fetch(`${API_BASE_URL}/courses/exercises`, {
            method: 'POST',
            headers: getHeaders(true),
            body: JSON.stringify(exerciseData)
        });
        return handleResponse(res);
    },
    
    deleteCurso: async (id) => {
        const res = await fetch(`${API_BASE_URL}/courses/deleteCurso?cursoId=${id}`, {
            method: 'DELETE',
            headers: getHeaders(true)
        });
        return handleResponse(res);
    },

    submitSolution : async (solutionData) => {
        const res = await fetch(`${API_BASE_URL}/exercises/verify`, {
            method: 'POST',
            headers: getHeaders(true),
            body: JSON.stringify(solutionData)
        });
        return res.json()
    },
	
	updateCourse: async (id, courseData) => {
	    const res = await fetch(`${API_BASE_URL}/courses/${id}`, {
	        method: 'PUT',
	        headers: getHeaders(true),
	        body: JSON.stringify(courseData)
	    });
	    return handleResponse(res);
	}
}

export const adminService = {
    getUsers: async () => {
        const token = localStorage.getItem('token');
        const res = await fetch(`${API_BASE_URL}/admin/users`, {
            method: 'GET', // Siguiendo tu backend actual que usa POST para requerir el DTO
            headers: getHeaders(true)
        });
        return handleResponse(res);
    },

    deleteUser: async (nombreUsuario) => {
        const token = localStorage.getItem('token');
        const res = await fetch(`${API_BASE_URL}/admin/deleteUser`, {
            method: 'DELETE',
            headers: getHeaders(true),
            body: JSON.stringify({ nombreUsuario })
        });
        return handleResponse(res);
    }
};