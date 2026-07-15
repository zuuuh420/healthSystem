import { FamilyMember } from '../types'
import { SimulatedVitals } from '../hooks/useSimulatedVitals'

export type AssistantRequest = { question: string; family: FamilyMember[]; userVitals: SimulatedVitals }

export const remoteAssistantEnabled = import.meta.env.VITE_ASSISTANT_MODE === 'remote'

type AssistantResponse = {
  answer?: string
  data?: { answer?: string }
}

function getAuthHeaders(): Record<string, string> {
  const token = window.localStorage.getItem('token')
    ?? window.localStorage.getItem('accessToken')
  return token ? { Authorization: `Bearer ${token}` } : {}
}

export async function requestHealthAssistant(request: AssistantRequest) {
  if (!remoteAssistantEnabled) return null
  const response = await fetch('/api/assistant/chat', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', ...getAuthHeaders() },
    body: JSON.stringify(request)
  })
  if (!response.ok) throw new Error(`assistant request failed: ${response.status}`)
  const raw = await response.text()
  if (!raw.trim()) throw new Error('assistant response was empty')
  let data: AssistantResponse
  try {
    data = JSON.parse(raw) as AssistantResponse
  } catch {
    throw new Error('assistant response was invalid')
  }
  return (data.data?.answer ?? data.answer)?.trim() || null
}
