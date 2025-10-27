import React from 'react'
import { createRoot } from 'react-dom/client'
import HomePage from './pages/HomePage'
import './style.css'

createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <HomePage />
  </React.StrictMode>
)
