export const wearableProfiles = [
  {
    id: 'demo2026',
    name: '演示用户',
    device: '知衡健康手环 ZHISHU',
    worn: true,
    battery: 86,
    lastSync: '刚刚同步',
    metrics: { heartRate: 72, oxygen: 98, temperature: 36.8, sleep: '7小时42分', pressure: '118/76', bodyFat: 22, steps: 8260 },
    activities: [
      { time: '07:40', title: '晨间步行', detail: '自动识别 · 18分钟 · 1.4 km', icon: 'el-icon-sunny' },
      { time: '12:26', title: '午间活动', detail: '自动识别 · 9分钟 · 860步', icon: 'el-icon-bicycle' },
      { time: '16:18', title: '久坐提醒', detail: '建议起身活动3分钟', icon: 'el-icon-alarm-clock' }
    ]
  },
  {
    id: 'zhang',
    name: '张森栋',
    device: '知衡健康手环 ZHISHU',
    worn: false,
    battery: 42,
    lastSync: '1小时18分钟前同步',
    metrics: { heartRate: null, oxygen: null, temperature: null, sleep: '--', pressure: '--', bodyFat: null, steps: null },
    activities: []
  },
  {
    id: 'liu',
    name: '刘子豪',
    device: '知衡健康手环 ZHISHU',
    worn: true,
    battery: 64,
    lastSync: '6分钟前同步',
    metrics: { heartRate: 68, oxygen: 99, temperature: 36.5, sleep: '8小时06分', pressure: '121/79', bodyFat: 19, steps: 10430 },
    activities: [
      { time: '08:12', title: '骑行', detail: '自动识别 · 26分钟 · 5.8 km', icon: 'el-icon-bicycle' },
      { time: '14:05', title: '力量训练', detail: '自动识别 · 32分钟', icon: 'el-icon-medal' }
    ]
  }
]

export function findWearableProfile(id) {
  return wearableProfiles.find(profile => profile.id === id) || {
    id: id || 'current-user',
    name: '当前用户',
    device: '知衡健康手环 ZHISHU',
    worn: false,
    battery: 0,
    lastSync: '尚未绑定设备',
    metrics: { heartRate: null, oxygen: null, temperature: null, sleep: '--', pressure: '--', bodyFat: null, steps: null },
    activities: []
  }
}
