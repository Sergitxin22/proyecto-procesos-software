import { Routes, Route } from 'react-router-dom'
import TestAuth from './pages/Auth/TestAuth'
import Landing from './pages/Landing/Landing'
import Auth from './pages/Auth/Auth'
import Admin from './pages/Admin/Admin'
import UserList from './pages/Admin/UserList'
import Profile from './pages/Profile/Profile'
import Courses from './pages/Courses/Courses'
import CreateCourse from './pages/Courses/CreateCourse'
import UserCourse from './pages/Courses/UserCourse'
import CreatedCourses from './pages/Courses/CreatedCourses'
import CreateExercise from './pages/Courses/CreateExercise'
import CreateModule from './pages/Courses/CreateModule'
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
      <Route path="/courses" element={<Courses />} />
    </Routes>
  )
}
