import { useMemo, useState } from 'react'
import { Bar, CartesianGrid, ComposedChart, LabelList, Line, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts'
import { TrendPoint } from '../types'

const data7: TrendPoint[] = [
  { date: '5/14 周二', steps: 5600, calories: 3700, goal: 68 },
  { date: '5/15 周三', steps: 5400, calories: 3600, goal: 72 },
  { date: '5/16 周四', steps: 6400, calories: 4200, goal: 85 },
  { date: '5/17 周五', steps: 7100, calories: 5200, goal: 92 },
  { date: '5/18 周六', steps: 6600, calories: 4700, goal: 78 },
  { date: '5/19 周日', steps: 6200, calories: 4100, goal: 65 },
  { date: '5/20 周一', steps: 6800, calories: 4300, goal: 88 }
]

const makeRange = (length: number) => Array.from({ length }, (_, index) => {
  const source = data7[index % data7.length]
  return { ...source, date: `5/${14 + index}` }
})

function TrendTooltip({ active, payload, label }: { active?: boolean; payload?: Array<{ payload: TrendPoint }>; label?: string }) {
  if (!active || !payload?.length) return null
  const point = payload[0].payload
  return <div className="trend-tooltip"><strong>{label}</strong><span>步数：{point.steps.toLocaleString()} 步</span><span>消耗：{point.calories.toLocaleString()} kcal</span><span>完成率：{point.goal}%</span></div>
}

const formatGoal = (value: unknown) => `${value}%`

export function TrendChart() {
  const [range, setRange] = useState('7')
  const data = useMemo(() => range === '7' ? data7 : makeRange(Number(range)), [range])
  const compactAxis = typeof window !== 'undefined' && window.innerWidth < 680
  return <section className="trend-card">
    <div className="card-heading"><div><span className="eyebrow">WEEKLY RHYTHM</span><h2>近7天趋势</h2></div><select value={range} onChange={event => setRange(event.target.value)} aria-label="趋势时间范围"><option value="7">近7天</option><option value="14">近14天</option><option value="30">近30天</option></select></div>
    <div className="chart-legend"><span><i className="legend-bar" />步数（步）</span><span><i className="legend-bar legend-bar--light" />消耗（kcal）</span><span><i className="legend-line" />目标完成率（%）</span></div>
    <div className="chart-wrap trend-chart-wrapper"><ResponsiveContainer width="100%" height="100%"><ComposedChart data={data} margin={{ top: 18, right: 16, bottom: 4, left: 0 }}>
      <CartesianGrid vertical={false} stroke="#edf2eb" />
      <XAxis dataKey="date" tick={{ fill: '#68756c', fontSize: compactAxis ? 9 : 11 }} tickLine={false} axisLine={{ stroke: '#dce5da' }} interval={compactAxis ? 1 : range === '30' ? 3 : range === '14' ? 1 : 0} />
      <YAxis yAxisId="steps" domain={[0, 12000]} ticks={[0, 3000, 6000, 9000, 12000]} tick={{ fill: '#7e8b82', fontSize: 10 }} tickLine={false} axisLine={false} />
      <YAxis yAxisId="goal" orientation="right" domain={[0, 100]} ticks={[0, 25, 50, 75, 100]} tickFormatter={value => `${value}%`} tick={{ fill: '#7e8b82', fontSize: 10 }} tickLine={false} axisLine={false} />
      <Tooltip content={<TrendTooltip />} cursor={{ fill: 'rgba(220,235,221,.22)' }} />
      <Bar yAxisId="steps" dataKey="steps" barSize={18} fill="#a8c9ad" radius={[4, 4, 0, 0]} isAnimationActive animationDuration={450} />
      <Bar yAxisId="steps" dataKey="calories" barSize={18} fill="#d4e5d3" radius={[4, 4, 0, 0]} isAnimationActive animationDuration={450} />
      <Line yAxisId="goal" type="monotone" dataKey="goal" stroke="#246247" strokeWidth={2.5} dot={{ r: 4, fill: '#fcfbf7', stroke: '#246247', strokeWidth: 2 }} activeDot={{ r: 6 }} isAnimationActive animationDuration={450}>
        <LabelList dataKey="goal" position="top" formatter={formatGoal} fill="#315f46" fontSize={11} />
      </Line>
    </ComposedChart></ResponsiveContainer></div>
  </section>
}
