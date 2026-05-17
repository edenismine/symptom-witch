import { z } from 'zod'

export type FetchError =
  | { kind: 'network' }
  | { kind: 'server-problem'; status: number; type?: string; detail?: string }
  | { kind: 'invalid-response'; error: z.ZodError }

const problemDetailSchema = z.object({
  type: z.string().optional(),
  detail: z.string().optional(),
})

export async function apiFetch<T>(
  url: string,
  init: RequestInit,
  schema: z.ZodSchema<T>,
): Promise<T> {
  let res: Response
  try {
    res = await fetch(url, init)
  } catch (e) {
    if (e instanceof TypeError) throw { kind: 'network' } satisfies FetchError
    throw e
  }
  if (!res.ok) {
    const body = await res.json().catch(() => ({}))
    const { type, detail } = problemDetailSchema.parse(body)
    throw { kind: 'server-problem', status: res.status, type, detail } satisfies FetchError
  }
  const result = z.safeParse(schema, await res.json())
  if (!result.success) throw { kind: 'invalid-response', error: result.error } satisfies FetchError
  return result.data
}
