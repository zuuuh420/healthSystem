import { SimulatedVitals } from '../hooks/useSimulatedVitals'
import { FamilyMember } from '../types'

export type LocalAssistantContext = { family: FamilyMember[]; userVitals: SimulatedVitals }
export type LocalAssistantAnswer = {
  text: string
  category: 'data' | 'lifestyle' | 'triage' | 'fallback'
  severity: 'normal' | 'caution' | 'urgent'
  source: 'device' | 'health-guide' | 'safety-guide'
}

const answer = (text: string, category: LocalAssistantAnswer['category'], source: LocalAssistantAnswer['source'], severity: LocalAssistantAnswer['severity'] = 'normal'): LocalAssistantAnswer => ({ text, category, source, severity })

function findMember(question: string, family: FamilyMember[]) {
  return family.find(member => [member.name, member.originalName, member.relationship].filter(Boolean).some(name => question.includes(name!.toLowerCase())))
}

export function answerLocalHealthQuestion(question: string, context: LocalAssistantContext): LocalAssistantAnswer {
  const text = question.trim().toLowerCase()
  if (!text) return answer('请告诉我你想了解的数据或健康问题。', 'fallback', 'health-guide')

  if (/(胸痛|胸口疼|呼吸困难|喘不上气|昏厥|意识不清|抽搐|单侧无力|突然口齿不清|高热不退|39度|40度)/.test(text)) {
    return answer('如果症状正在发生、突然出现或正在加重，请立即联系急救服务或前往急诊，不要等待设备数据给出判断，也不要自行开药。若症状较轻但持续存在，请尽快联系医生。', 'triage', 'safety-guide', 'urgent')
  }

  const member = findMember(text, context.family)
  if (member) {
    if (text.includes('佩戴') || text.includes('设备') || text.includes('在线')) return answer(`${member.name}当前${member.deviceOnline ? '设备在线' : '设备离线'}，${member.wearing ? '手环已佩戴。' : '手环未佩戴，暂时没有实时健康指标。'}`, 'data', 'device')
    if (!member.vitals || !member.wearing || !member.deviceOnline) return answer(`${member.name}当前没有可用的实时指标，因为设备${member.deviceOnline ? '未佩戴' : '处于离线状态'}。`, 'data', 'device', 'caution')
    if (text.includes('心率') || text.includes('心跳')) return answer(`${member.name}当前心率约 ${member.vitals.heartRate} bpm。数据来自已授权设备快照，仅供日常参考。`, 'data', 'device')
    if (text.includes('血氧')) return answer(`${member.name}当前血氧约 ${member.vitals.oxygen}%。数据来自已授权设备快照，仅供日常参考。`, 'data', 'device')
    if (text.includes('体温')) return answer(`${member.name}当前体温约 ${member.vitals.temperature.toFixed(1)}°C。数据来自已授权设备快照，仅供日常参考。`, 'data', 'device')
    if (text.includes('睡眠') || text.includes('睡得')) return answer(`${member.name}昨夜睡眠约 ${member.vitals.sleep}。可以结合连续几天的趋势观察，不要只根据单晚数据下结论。`, 'data', 'device')
    if (text.includes('步数') || text.includes('走了多少')) return answer(`${member.name}今天约 ${member.vitals.steps.toLocaleString()} 步。`, 'data', 'device')
  }

  const askingMe = text.includes('我') || text.includes('本人') || text.includes('我的')
  if (askingMe && (text.includes('心率') || text.includes('心跳'))) return answer(`你当前心率约 ${context.userVitals.heartRate} bpm，属于设备当前记录值。`, 'data', 'device')
  if (askingMe && text.includes('血氧')) return answer(`你当前血氧为 ${context.userVitals.oxygen}%，属于设备当前记录值。`, 'data', 'device')
  if (askingMe && text.includes('体温')) return answer(`你当前体温为 ${context.userVitals.temperature.toFixed(1)}°C，属于设备当前记录值。`, 'data', 'device')
  if (askingMe && (text.includes('睡眠') || text.includes('睡得'))) return answer(`你昨夜睡眠约 ${context.userVitals.sleep}。今天可以根据精神状态调整运动强度，连续疲劳或长期睡眠不足建议咨询医生。`, 'data', 'device')
  if (askingMe && (text.includes('步数') || text.includes('走了多少'))) return answer(`你今天约走了 ${context.userVitals.steps.toLocaleString()} 步。可以把目标拆成几次短时间活动，逐步完成。`, 'data', 'device')

  if (text.includes('睡不着') || text.includes('失眠') || text.includes('睡眠') || text.includes('怎么睡')) return answer('可以先固定起床时间，睡前一小时减少屏幕和咖啡因，白天安排适量活动；如果每周多次持续数周，或伴随明显情绪和白天功能受影响，建议咨询医生。', 'lifestyle', 'health-guide')
  if (text.includes('运动多久') || text.includes('运动多少') || text.includes('锻炼') || text.includes('跑步')) return answer('一般可以从每周累计约 150 分钟中等强度活动开始，并加入适度力量训练；如果平时运动较少，先从短时、低强度开始，根据身体反应逐步增加。', 'lifestyle', 'health-guide')
  if (text.includes('饭后') || text.includes('饮食') || text.includes('吃什么') || text.includes('喝水') || text.includes('饮水')) return answer('饮食可以优先保证蔬菜、蛋白质和主食的均衡，少量多次饮水并根据天气和活动量调整。饭后先轻松走动，剧烈运动可间隔一段时间；具体疾病饮食请遵循医生或营养师建议。', 'lifestyle', 'health-guide')
  if (text.includes('久坐') || text.includes('腰酸') || text.includes('放松')) return answer('如果连续坐着工作，可以每小时起身活动几分钟，做轻柔的肩颈和下肢伸展；出现持续疼痛、麻木或无力时，应停止自行锻炼并咨询医生。', 'lifestyle', 'health-guide', 'caution')
  if (text.includes('家人') || text.includes('谁')) return answer(context.family.length ? context.family.map(item => `${item.name}${item.wearing && item.deviceOnline ? '已佩戴且在线' : '当前没有实时指标'}`).join('，') + '。你可以继续询问具体家人的设备状态或指标。' : '目前没有已授权的家人数据。', 'data', 'device')

  return answer('我可以帮你查询本人或已授权家人的设备数据，也可以回答睡眠、运动、饮食和饮水等日常健康问题。涉及胸痛、呼吸困难、昏厥等情况时，请优先寻求医疗帮助。', 'fallback', 'health-guide')
}
