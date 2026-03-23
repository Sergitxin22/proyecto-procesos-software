import { Routes, Route } from 'react-router-dom'
import TestAuth from './TestAuth'
import Landing from './Landing'
import Auth from './Auth'
import Admin from './Admin'
import UserList from './UserList'
import Profile from './Profile'
import CreateCourse from './CreateCourse'
import UserCourse from './UserCourse'
import CreatedCourses from './CreatedCourses'
import CreateExercise from './CreateExercise'
import CreateModule from './CreateModule'
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
