import { Routes, Route } from 'react-router-dom'
import TestAuth from './pages/TestAuth'
import Landing from './pages/Landing'
import Auth from './pages/Auth'
import Admin from './pages/Admin'
import UserList from './pages/UserList'
import Profile from './pages/Profile'
import CreateCourse from './pages/CreateCourse'
import UserCourse from './pages/UserCourse'
import CreatedCourses from './pages/CreatedCourses'
import CreateExercise from './pages/CreateExercise'
import CreateModule from './pages/CreateModule'
import './index.css'

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Landing />} />
      <Route path="/test/auth" element={<TestAuth />} />
      <Route path="/auth" element={<Auth />} />
      <Route path="/profile" element={<Profile />} />
      <Route path="/create_course" element={<CreateCourse />} />
      <Route path="/created_courses" element={<CreatedCourses />} />
      <Route path="/created_courses/:courseId" element={<UserCourse />} />
      <Route path="/created_courses/:courseId/create_module" element={<CreateModule />} />
      <Route path="/created_courses/:courseId/modules/:moduleId/create_exercise" element={<CreateExercise />} />
      <Route path="/admin" element={<Admin />} />
      <Route path="/admin/users" element={<UserList />} />
    </Routes>
  )
}
