import { ReactNode, useEffect, useRef, useState } from 'react'
import anatomyBody from '../assets/anatomy-body.png'

export type AnnotationId = 'heartRate' | 'restingRespiration' | 'spo2' | 'coreTemperature'
export type Annotation = {
  id: AnnotationId
  label: string
  status: string
  anchor: { x: number; y: number }
  labelPosition: { x: number; y: number }
  side: 'left' | 'right'
  hotspotColor: string
}

export const annotations: Annotation[] = [
  { id: 'heartRate', label: '心率', status: '正常', anchor: { x: 486, y: 225 }, labelPosition: { x: 210, y: 175 }, side: 'left', hotspotColor: '#B96C77' },
  { id: 'restingRespiration', label: '静息呼吸', status: '稳定', anchor: { x: 524, y: 215 }, labelPosition: { x: 210, y: 340 }, side: 'left', hotspotColor: '#5F9273' },
  { id: 'spo2', label: '血氧水平', status: '良好', anchor: { x: 476, y: 215 }, labelPosition: { x: 740, y: 210 }, side: 'right', hotspotColor: '#4C9270' },
  { id: 'coreTemperature', label: '核心体温', status: '正常', anchor: { x: 500, y: 330 }, labelPosition: { x: 740, y: 390 }, side: 'right', hotspotColor: '#B18A5A' }
]

export type BodyModelProps = { bodyTone?: string; detailTone?: string }
export type BodyModelRenderer = (props: BodyModelProps) => ReactNode
export type BodyImageFormat = 'webp' | 'png' | 'svg'
export type BodyImageAsset = {
  kind: 'image'
  src: string
  format: BodyImageFormat
  ariaLabel?: string
}
export type BodyModelSource = BodyModelRenderer | BodyImageAsset | string
export const bodyBounds = { x: 250, y: 10, width: 500, height: 680 } as const

let fallbackWarningShown = false

const bodyShape = 'M157 99 C155 111 146 119 132 128 L106 158 L84 274 L111 288 L137 226 L137 347 L112 550 L145 550 L180 371 L215 550 L248 550 L223 347 L223 226 L249 288 L276 274 L254 158 L228 128 C214 119 205 111 203 99 Z'
const leftFoot = 'M112 548 C108 565 91 579 72 594 C94 607 119 606 145 585 L153 553 Z'
const rightFoot = 'M248 548 C252 565 269 579 288 594 C266 607 241 606 215 585 L207 553 Z'

// This is deliberately a replaceable fallback model. The current project does not contain a licensed high-detail anatomy asset.
export function BodyModel({ bodyTone = '#8B9B96', detailTone = '#DDE8E2' }: BodyModelProps) {
  return <g id="body-model" transform="translate(280 10) scale(1.25 .94)" opacity=".92">
    <g fill={bodyTone} fillOpacity=".28" stroke="#718680" strokeWidth="2">
      <ellipse cx="180" cy="59" rx="34" ry="47" />
      <path d={bodyShape} />
      <path d={leftFoot} />
      <path d={rightFoot} />
    </g>
    <g fill="none" stroke={detailTone} strokeLinecap="round" strokeLinejoin="round">
      <path d="M180 112 V375" strokeWidth="5" opacity=".9" />
      <path d="M155 122 Q180 143 205 122 M148 148 Q180 171 212 148 M143 176 Q180 200 217 176 M140 206 Q180 231 220 206 M141 238 Q180 263 219 238 M145 270 Q180 293 215 270 M151 303 Q180 323 209 303" strokeWidth="3" opacity=".9" />
      <path d="M180 111 C157 142 155 186 180 221 C205 186 203 142 180 111 M180 221 C155 250 155 292 180 326 M180 221 C205 250 205 292 180 326" stroke="#B5CDC0" strokeWidth="3" />
      <path d="M136 226 L108 272 M224 226 L252 272 M137 349 L112 548 M223 349 L248 548" strokeWidth="3" />
      <path d="M180 337 C158 359 151 384 155 408 C164 424 196 424 205 408 C209 384 202 359 180 337" strokeWidth="3" />
      <path d="M166 35 Q180 48 194 35 M158 58 Q180 71 202 58 M166 82 Q180 91 194 82 M157 100 Q180 112 203 100" strokeWidth="2" opacity=".9" />
    </g>
    <g fill="none" stroke="#9BB5A8" strokeWidth="2.3" opacity=".92">
      <path d="M180 111 C165 146 164 178 180 208 C196 178 195 146 180 111" />
      <path d="M180 208 C165 235 164 275 180 319 M180 208 C195 235 196 275 180 319" />
      <path d="M180 319 C165 350 165 401 180 443 M180 319 C195 350 195 401 180 443" />
      <path d="M180 120 C143 142 133 184 139 223 M180 120 C217 142 227 184 221 223" />
      <path d="M180 150 C148 174 143 207 146 238 M180 150 C212 174 217 207 214 238" />
      <path d="M180 338 C154 367 148 402 155 438 M180 338 C206 367 212 402 205 438" />
      <path d="M180 438 C162 463 157 502 149 547 M180 438 C198 463 203 502 211 547" />
      <path d="M153 225 C139 262 125 279 109 285 M207 225 C221 262 235 279 251 285" />
    </g>
    <g fill="#ADC4B8" stroke="#6F927F" strokeWidth="2" opacity=".82">
      <path d="M174 165 C158 148 143 163 145 184 C147 202 165 214 180 225 C195 214 213 202 215 184 C217 163 202 148 186 165 L180 173 Z" />
      <path d="M150 155 C133 170 135 210 155 227 C166 214 171 190 168 166 Z" opacity=".72" />
      <path d="M210 155 C227 170 225 210 205 227 C194 214 189 190 192 166 Z" opacity=".72" />
      <path d="M151 250 C166 241 176 250 180 263 C184 250 194 241 209 250 C209 273 196 287 180 291 C164 287 151 273 151 250 Z" opacity=".7" />
      <path d="M153 304 C164 294 174 298 180 309 C186 298 196 294 207 304 C204 326 193 337 180 340 C167 337 156 326 153 304 Z" opacity=".64" />
    </g>
    <circle cx="180" cy="188" r="9" fill="#B96C77" opacity=".85" />
  </g>
}

export function ImageBodyModel({ href, ariaLabel, onLoad, onError }: { href: string; ariaLabel?: string; onLoad?: () => void; onError?: () => void }) {
  return <image id="body-image-model" href={href} x={bodyBounds.x} y={bodyBounds.y} width={bodyBounds.width} height={bodyBounds.height} preserveAspectRatio="xMidYMid meet" aria-label={ariaLabel ?? '透明人体解剖素材'} onLoad={onLoad} onError={onError} />
}

export function buildConnectorPath(anchor: Annotation['anchor'], labelPosition: Annotation['labelPosition'], side: Annotation['side']) {
  const labelEdge = labelPosition.x
  const direction = side === 'left' ? -1 : 1
  const controlX = anchor.x + direction * 70
  const elbowX = side === 'left' ? labelEdge + 36 : labelEdge - 36
  return `M ${anchor.x} ${anchor.y} C ${controlX} ${anchor.y}, ${controlX} ${labelPosition.y}, ${elbowX} ${labelPosition.y} H ${labelEdge}`
}

function SceneStyles() {
  return <style>{`#body-scan-scene .scene-body-motion{animation:sceneFloat 4s ease-in-out infinite;transform-box:fill-box;transform-origin:center}#body-scan-scene .scene-scan-beam{animation:sceneScan 5.3s ease-in-out infinite}#body-scan-scene .scene-pulse-ring{animation:scenePulse 2.4s ease-out infinite;transform-box:fill-box;transform-origin:center}#body-scan-scene .scene-heart-core{animation:sceneHeart 2.4s ease-in-out infinite;transform-box:fill-box;transform-origin:center}#body-scan-scene .scene-connector{stroke-dasharray:1;stroke-dashoffset:1;animation:sceneDraw 1.2s ease forwards;vector-effect:non-scaling-stroke}#body-scan-scene .scene-label{font-family:'Noto Sans SC','Microsoft YaHei',sans-serif;animation:sceneLabel .8s ease forwards;opacity:0}@keyframes sceneFloat{0%,100%{transform:translateY(0)}50%{transform:translateY(-2px)}}@keyframes sceneScan{0%,10%{transform:translateY(-10px);opacity:0}18%,78%{opacity:.72}90%,100%{transform:translateY(550px);opacity:0}}@keyframes scenePulse{0%{transform:scale(.5);opacity:.58}80%,100%{transform:scale(2.5);opacity:0}}@keyframes sceneHeart{0%,14%,30%,100%{transform:scale(1)}6%{transform:scale(1.28)}20%{transform:scale(1.16)}}@keyframes sceneDraw{to{stroke-dashoffset:0}}@keyframes sceneLabel{from{opacity:0;transform:translateY(4px)}to{opacity:1;transform:translateY(0)}}@media(prefers-reduced-motion:reduce){#body-scan-scene .scene-body-motion,#body-scan-scene .scene-scan-beam,#body-scan-scene .scene-pulse-ring,#body-scan-scene .scene-heart-core,#body-scan-scene .scene-connector,#body-scan-scene .scene-label{animation:none;opacity:1;stroke-dashoffset:0}}`}</style>
}

export function BodyScanVisualization({ bodyModel = anatomyBody, heartRate = 77 }: { bodyModel?: BodyModelSource; heartRate?: number }) {
  const [assetFailed, setAssetFailed] = useState(false)
  const [layoutMode, setLayoutMode] = useState<'compact' | 'regular'>('regular')
  const scanStageRef = useRef<HTMLDivElement>(null)
  const imageAsset = typeof bodyModel === 'object' ? bodyModel : null
  const imageSrc = typeof bodyModel === 'string' ? bodyModel : imageAsset?.src
  const renderer = typeof bodyModel === 'function' ? bodyModel : null
  const usingFallback = (!imageSrc && !renderer) || assetFailed

  useEffect(() => {
    setAssetFailed(false)
  }, [imageSrc])

  useEffect(() => {
    if (!imageSrc) return
    const probe = new window.Image()
    let active = true
    probe.onload = () => { if (active && import.meta.env.DEV) console.info('人体素材 onLoad:', probe.naturalWidth, 'x', probe.naturalHeight, '最终渲染模式: bodyModel') }
    probe.onerror = () => { if (active && import.meta.env.DEV) console.warn('人体素材 onError，最终渲染模式: fallback') }
    probe.src = imageSrc
    return () => { active = false; probe.onload = null; probe.onerror = null }
  }, [imageSrc])

  useEffect(() => {
    if (import.meta.env.DEV && usingFallback && !fallbackWarningShown) {
      fallbackWarningShown = true
      console.warn('当前使用低细节人体占位素材，请提供透明解剖人体 WebP/PNG/SVG。')
    }
  }, [usingFallback])

  useEffect(() => {
    const stage = scanStageRef.current
    if (!stage) return
    const update = () => setLayoutMode(stage.clientWidth < 430 ? 'compact' : 'regular')
    const observer = new ResizeObserver(update)
    update()
    observer.observe(stage)
    return () => observer.disconnect()
  }, [])

  const compactLabels = layoutMode === 'compact'
  const renderedAnnotations = compactLabels
    ? annotations.map(item => ({ ...item, labelPosition: { x: item.side === 'left' ? 400 : 630, y: item.labelPosition.y } }))
    : annotations

  return <div ref={scanStageRef} className="body-scan-stage body-visual" aria-label="动态人体健康扫描">
    <svg id="body-scan-scene" className="body-scan-svg body-scene" viewBox="0 0 1000 700" preserveAspectRatio="xMidYMid meet" role="img" aria-label="人体健康扫描场景">
      <SceneStyles />
      <defs>
        <radialGradient id="sceneAura"><stop offset="0" stopColor="#EAF3EC" stopOpacity=".8" /><stop offset=".72" stopColor="#F7FAF6" stopOpacity=".22" /><stop offset="1" stopColor="#FFF" stopOpacity="0" /></radialGradient>
        <linearGradient id="sceneBeam" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stopColor="#B8D9BE" stopOpacity="0" /><stop offset=".5" stopColor="#A8CFAF" stopOpacity=".58" /><stop offset="1" stopColor="#B8D9BE" stopOpacity="0" /></linearGradient>
        <clipPath id="bodyScanClip" clipPathUnits="userSpaceOnUse"><g transform="translate(280 10) scale(1.25 .94)"><ellipse cx="180" cy="59" rx="34" ry="47" /><path d={bodyShape} /><path d={leftFoot} /><path d={rightFoot} /></g></clipPath>
        {imageSrc && <mask id="bodyScanMask" maskUnits="userSpaceOnUse" maskContentUnits="userSpaceOnUse" style={{ maskType: 'alpha' }}><image href={imageSrc} x={bodyBounds.x} y={bodyBounds.y} width={bodyBounds.width} height={bodyBounds.height} preserveAspectRatio="xMidYMid meet" /></mask>}
      </defs>
      <g id="background-layer"><ellipse cx="500" cy="350" rx="315" ry="300" fill="url(#sceneAura)" /><ellipse cx="500" cy="350" rx="246" ry="280" fill="none" stroke="#DCEBDD" strokeOpacity=".28" /><ellipse cx="500" cy="350" rx="210" ry="250" fill="none" stroke="#DCEBDD" strokeOpacity=".22" /><ellipse cx="500" cy="350" rx="176" ry="220" fill="none" stroke="#DCEBDD" strokeOpacity=".16" /><path d="M260 120H740 M245 205H755 M235 290H765 M235 375H765 M245 460H755 M260 545H740" stroke="#DCEBDD" strokeOpacity=".12" strokeWidth="1" /></g>
      <g className="scene-body-motion">
        <g id="body-layer">{usingFallback ? <BodyModel bodyTone="#8B9B96" detailTone="#DDE8E2" /> : renderer ? renderer({ bodyTone: '#8B9B96', detailTone: '#DDE8E2' }) : imageSrc ? <ImageBodyModel href={imageSrc} ariaLabel={imageAsset?.ariaLabel} onLoad={() => import.meta.env.DEV && console.info('人体素材 SVG image onLoad，最终渲染模式: bodyModel')} onError={() => { if (import.meta.env.DEV) console.warn('人体素材 SVG image onError，最终渲染模式: fallback'); setAssetFailed(true) }} /> : null}</g>
        <g id="scan-layer" mask={imageSrc && !assetFailed ? 'url(#bodyScanMask)' : undefined} clipPath={!imageSrc || assetFailed ? 'url(#bodyScanClip)' : undefined}><rect className="scene-scan-beam" x={bodyBounds.x} y={bodyBounds.y} width={bodyBounds.width} height={bodyBounds.height} fill="url(#sceneBeam)" filter="blur(8px)" /></g>
        <g id="hotspot-layer">{renderedAnnotations.map((item, index) => <g key={item.id} transform={`translate(${item.anchor.x} ${item.anchor.y})`}><circle className="scene-pulse-ring" r="12" fill="none" stroke={item.hotspotColor} strokeOpacity=".48" strokeWidth="1.5" style={{ animationDelay: `${index * .36}s` }} /><circle className="scene-pulse-ring" r="12" fill="none" stroke={item.hotspotColor} strokeOpacity=".34" strokeWidth="1.5" style={{ animationDelay: `${index * .36 + 1.2}s` }} /><circle className={item.id === 'heartRate' ? 'scene-heart-core' : ''} r={item.id === 'heartRate' ? 6 : 4} fill={item.hotspotColor} fillOpacity=".78" /></g>)}</g>
        <g id="connector-layer">{renderedAnnotations.map((item, index) => <path key={item.id} className="scene-connector" d={buildConnectorPath(item.anchor, item.labelPosition, item.side)} pathLength="1" fill="none" stroke="#78A687" strokeWidth="1.4" style={{ animationDelay: `${index * .12}s` }} />)}</g>
      </g>
      <g id="label-layer">{renderedAnnotations.map((item, index) => <text key={item.id} className="scene-label" x={item.labelPosition.x} y={item.labelPosition.y} textAnchor={item.side === 'left' ? 'end' : 'start'} fill="#4C725A" fontSize={compactLabels ? 26 : 20} fontWeight="600" dominantBaseline="middle" style={{ animationDelay: `${index * .12 + .15}s` }}><tspan>{item.label}</tspan><tspan dx="8" fill="#315F46" fontSize={compactLabels ? 22 : 18}>{item.status}</tspan></text>)}</g>
    </svg>
    <div className="scan-status-pill"><span className="live-dot" />实时扫描 · {heartRate} bpm</div>
  </div>
}
