type Props = { tone?: string; values?: number[] }
export function LiveSparkline({ tone = 'green', values = [6, 10, 7, 12, 8, 14, 10] }: Props) {
  const min = Math.min(...values); const max = Math.max(...values); const points = values.map((value, index) => `${(index / (values.length - 1)) * 100},${36 - ((value - min) / Math.max(1, max - min)) * 28}`).join(' ')
  return <svg className={`sparkline sparkline--${tone}`} viewBox="0 0 100 40" role="img" aria-label="指标趋势"><polyline points={points} fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" /></svg>
}
