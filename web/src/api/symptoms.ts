import { z } from 'zod'

import { apiFetch } from '@/api/client'
export type { FetchError } from '@/api/client'

const symptomResponseSchema = z.object({
  id: z.string(),
  name: z.string(),
  archived: z.boolean(),
  createdAt: z.string(),
  updatedAt: z.string(),
})

export type SymptomResponse = z.infer<typeof symptomResponseSchema>

export interface PatchSymptomRequest {
  name?: string
  archived?: boolean
}

const baseUrl = import.meta.env.VITE_API_URL ?? 'http://localhost:8080'

function authHeaders(token: string): HeadersInit {
  return { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' }
}

export async function fetchActiveSymptoms(token: string): Promise<SymptomResponse[]> {
  return apiFetch(
    `${baseUrl}/v1/symptoms`,
    { headers: authHeaders(token) },
    z.array(symptomResponseSchema),
  )
}

export async function createSymptom(token: string, name: string): Promise<SymptomResponse> {
  return apiFetch(
    `${baseUrl}/v1/symptoms`,
    { method: 'POST', headers: authHeaders(token), body: JSON.stringify({ name }) },
    symptomResponseSchema,
  )
}

export async function patchSymptom(
  token: string,
  id: string,
  patch: PatchSymptomRequest,
): Promise<SymptomResponse> {
  return apiFetch(
    `${baseUrl}/v1/symptoms/${id}`,
    { method: 'PATCH', headers: authHeaders(token), body: JSON.stringify(patch) },
    symptomResponseSchema,
  )
}
