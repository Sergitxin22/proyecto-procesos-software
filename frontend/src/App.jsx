import { useState, useEffect } from 'react'
import TestAuth from './TestAuth'
import Landing from './Landing'
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

  return <Landing />
}
