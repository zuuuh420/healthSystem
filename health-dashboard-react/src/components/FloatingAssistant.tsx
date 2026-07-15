import { Bot, X } from 'lucide-react'
import { PointerEvent, useEffect, useRef, useState } from 'react'

export function FloatingAssistant({ open, onOpen, onClose }: { open: boolean; onOpen: () => void; onClose: () => void }) {
  const [position, setPosition] = useState({ right: 28, bottom: 28 })
  const dragRef = useRef<{ startX: number; startY: number; right: number; bottom: number } | null>(null)
  const movedRef = useRef(false)

  useEffect(() => {
    const move = (event: globalThis.PointerEvent) => {
      if (!dragRef.current) return
      if (Math.abs(event.clientX - dragRef.current.startX) > 4 || Math.abs(event.clientY - dragRef.current.startY) > 4) movedRef.current = true
      setPosition(current => ({
        right: Math.max(16, Math.min(window.innerWidth - 64, dragRef.current!.right - (event.clientX - dragRef.current!.startX))),
        bottom: Math.max(16, Math.min(window.innerHeight - 64, dragRef.current!.bottom - (event.clientY - dragRef.current!.startY)))
      }))
    }
    const end = () => { dragRef.current = null }
    window.addEventListener('pointermove', move)
    window.addEventListener('pointerup', end)
    return () => { window.removeEventListener('pointermove', move); window.removeEventListener('pointerup', end) }
  }, [])

  const handlePointerDown = (event: PointerEvent<HTMLButtonElement>) => {
    movedRef.current = false
    dragRef.current = { startX: event.clientX, startY: event.clientY, right: position.right, bottom: position.bottom }
    event.currentTarget.setPointerCapture?.(event.pointerId)
  }

  return <>
    <button
      className={`floating-assistant-button ${open ? 'is-open' : ''}`}
      style={{ right: position.right, bottom: position.bottom }}
      aria-label={open ? '关闭健康助手' : '打开健康助手'}
      title="拖动或打开健康助手"
      onPointerDown={handlePointerDown}
      onClick={() => { if (!movedRef.current) open ? onClose() : onOpen() }}
    >
      {open ? <X size={20} /> : <Bot size={21} />}
      {!open && <span className="floating-assistant-label">健康助手</span>}
    </button>
  </>
}
