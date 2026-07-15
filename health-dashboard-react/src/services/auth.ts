type LoginResult = {
  token: string
  userInfo: Record<string, unknown>
}

type ApiResult<T> = { code: number; msg?: string; data?: T }

export type RegisterInput = {
  username: string
  nickname: string
  email: string
  password: string
}

export async function login(username: string, password: string) {
  const response = await fetch('/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  })
  const result = await response.json() as ApiResult<LoginResult>
  if (!response.ok || result.code !== 200 || !result.data) throw new Error(result.msg ?? '用户名或密码错误')
  return result.data
}

export async function register(input: RegisterInput) {
  const response = await fetch('/api/auth/register', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(input)
  })
  const result = await response.json() as ApiResult<null>
  if (!response.ok || result.code !== 200) throw new Error(result.msg ?? '注册失败，请稍后重试')
}
