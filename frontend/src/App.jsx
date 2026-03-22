import { useState, useEffect } from 'react'
import TestAuth from './TestAuth'
import Landing from './Landing'
import Auth from './Auth'
import Profile from './Profile'
import CreateCourse from './CreateCourse'
import CreatedCourses from './CreatedCourses'
import './index.css'

export default function App() {
  const [currentPath, setCurrentPath] = useState(window.location.pathname)

  useEffect(() => {
    const handleLocationChange = () => setCurrentPath(window.location.pathname)
    window.addEventListener('popstate', handleLocationChange)
    return () => window.removeEventListener('popstate', handleLocationChange)
  }, [])

  if (currentPath === '/test/auth') {
    return <TestAuth />
  }

  if (currentPath === '/auth') {
    return <Auth />
  }

  if (currentPath === '/profile') {
    return <Profile />
  }

  if (currentPath === '/create_course') {
    return <CreateCourse />
  }

  if (currentPath === '/created_courses') {
    return <CreatedCourses />
  }

  return <Landing />
}
