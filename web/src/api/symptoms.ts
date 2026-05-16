export interface SymptomResponse {
  id: string
  name: string
  archived: boolean
  createdAt: string
  updatedAt: string
}

export interface PatchSymptomRequest {
  name?: string
  archived?: boolean
}

const baseUrl = import.meta.env.VITE_API_URL ?? 'http://localhost:8080'

function authHeaders(token: string): HeadersInit {
  return { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' }
}

export async function fetchActiveSymptoms(token: string): Promise<SymptomResponse[]> {
  const res = await fetch(`${baseUrl}/v1/symptoms`, { headers: authHeaders(token) })
  if (!res.ok) throw new Error('Failed to fetch symptoms')
  return res.json() as Promise<SymptomResponse[]>
}

export async function createSymptom(token: string, name: string): Promise<SymptomResponse> {
  const res = await fetch(`${baseUrl}/v1/symptoms`, {
    method: 'POST',
    headers: authHeaders(token),
    body: JSON.stringify({ name }),
  })
  if (!res.ok) throw new Error('Failed to create symptom')
  return res.json() as Promise<SymptomResponse>
}

export async function patchSymptom(
  token: string,
  id: string,
  patch: PatchSymptomRequest,
): Promise<SymptomResponse> {
  const res = await fetch(`${baseUrl}/v1/symptoms/${id}`, {
    method: 'PATCH',
    headers: authHeaders(token),
    body: JSON.stringify(patch),
  })
  if (!res.ok) throw new Error('Failed to update symptom')
  return res.json() as Promise<SymptomResponse>
}
