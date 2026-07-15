import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App'
import './styles.css'
import './report.css'
import './device.css'
import './wellness.css'

const params = new URLSearchParams(window.location.search)
const handoffToken = params.get('token')
if (handoffToken) {
  window.localStorage.setItem('token', handoffToken)
  params.delete('token')
  const nextQuery = params.toString()
  window.history.replaceState({}, document.title, `${window.location.pathname}${nextQuery ? `?${nextQuery}` : ''}${window.location.hash}`)
}

ReactDOM.createRoot(document.getElementById('root')!).render(<React.StrictMode><App /></React.StrictMode>)
