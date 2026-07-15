import { useCallback, useEffect, useRef, useState } from 'react'

export type SimulatedVitals = {
  heartRate: number
  oxygen: 98 | 99
  temperature: number
  sleep: string
  steps: number
  healthScore: number
}

const initialVitals: SimulatedVitals = {
  heartRate: 77,
  oxygen: 99,
  temperature: 36.8,
  sleep: '7小时42分',
  steps: 6842,
  healthScore: 88
}

const heartDeltas = [0, 1, 0, -1, 0, 0, 1, -1] as const
const oxygenDeltas = [0, 0, 0, -1, 0, 0, 0, 1] as const
const temperatureDeltas = [0.1, 0, -0.1, 0] as const
const stepIncrements = [0, 7, 13, 20, 4] as const

const clamp = (value: number, min: number, max: number) => Math.min(max, Math.max(min, value))

export function useSimulatedVitals() {
  const [vitals, setVitals] = useState<SimulatedVitals>(initialVitals)
  const heartIndex = useRef(0)
  const oxygenIndex = useRef(0)
  const temperatureIndex = useRef(0)
  const stepIndex = useRef(0)
  const heartTimer = useRef<number | null>(null)
  const oxygenTimer = useRef<number | null>(null)

  const stopTimers = useCallback(() => {
    if (heartTimer.current !== null) window.clearInterval(heartTimer.current)
    if (oxygenTimer.current !== null) window.clearInterval(oxygenTimer.current)
    heartTimer.current = null
    oxygenTimer.current = null
  }, [])

  const updateHeartRate = useCallback(() => {
    const delta = heartDeltas[heartIndex.current % heartDeltas.length]
    heartIndex.current += 1
    setVitals(current => ({ ...current, heartRate: clamp(current.heartRate + delta, 74, 80) }))
  }, [])

  const updateOxygen = useCallback(() => {
    const delta = oxygenDeltas[oxygenIndex.current % oxygenDeltas.length]
    oxygenIndex.current += 1
    setVitals(current => ({ ...current, oxygen: clamp(current.oxygen + delta, 98, 99) as 98 | 99 }))
  }, [])

  const startTimers = useCallback(() => {
    stopTimers()
    if (document.visibilityState !== 'visible') return
    heartTimer.current = window.setInterval(updateHeartRate, 5000)
    oxygenTimer.current = window.setInterval(updateOxygen, 20000)
  }, [stopTimers, updateHeartRate, updateOxygen])

  useEffect(() => {
    const handleVisibilityChange = () => {
      if (document.visibilityState === 'visible') startTimers()
      else stopTimers()
    }
    startTimers()
    document.addEventListener('visibilitychange', handleVisibilityChange)
    return () => {
      document.removeEventListener('visibilitychange', handleVisibilityChange)
      stopTimers()
    }
  }, [startTimers, stopTimers])

  const applySyncUpdate = useCallback(() => {
    const temperatureDelta = temperatureDeltas[temperatureIndex.current % temperatureDeltas.length]
    const stepIncrease = stepIncrements[stepIndex.current % stepIncrements.length]
    temperatureIndex.current += 1
    stepIndex.current += 1
    setVitals(current => ({
      ...current,
      temperature: Number(clamp(current.temperature + temperatureDelta, 36.7, 36.9).toFixed(1)),
      steps: current.steps + stepIncrease
    }))
  }, [])

  return { vitals, applySyncUpdate }
}
