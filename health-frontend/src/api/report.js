import request from '@/utils/request'

export function getReportSource() {
  const safe = (config, fallback) => request(config).catch(() => ({ data: fallback }))

  return Promise.all([
    safe({ url: '/dashboard/overview', method: 'get' }, {}),
    safe({ url: '/diet-records/stats', method: 'get' }, {}),
    safe({ url: '/health-records', method: 'get' }, [])
  ]).then(([overview, diet, health]) => ({
    overview: {
      ...(overview.data || {}),
      suggestions: Array.isArray(overview.data && overview.data.suggestions)
        ? overview.data.suggestions
        : []
    },
    diet: diet.data || {},
    health: Array.isArray(health.data) ? health.data : []
  }))
}
