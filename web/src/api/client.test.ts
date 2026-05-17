import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { z } from 'zod'

import { apiFetch } from '@/api/client'
import type { FetchError } from '@/api/client'

const mockFetch = vi.fn()

beforeEach(() => {
  vi.stubGlobal('fetch', mockFetch)
})

afterEach(() => {
  vi.unstubAllGlobals()
  vi.resetAllMocks()
})

const thingSchema = z.object({ id: z.string() })

describe('apiFetch', () => {
  it('throws FetchError { kind: "network" } when fetch rejects with TypeError', async () => {
    mockFetch.mockRejectedValueOnce(new TypeError('Failed to fetch'))

    const error = await apiFetch('/things', {}, thingSchema).catch((e: unknown) => e)

    expect(error).toMatchObject<FetchError>({ kind: 'network' })
  })

  it('throws FetchError { kind: "api" } with status, type, and detail from RFC 9457 body', async () => {
    mockFetch.mockResolvedValueOnce({
      ok: false,
      status: 409,
      json: () =>
        Promise.resolve({
          type: 'https://api.symptomwitch.local/errors/duplicate-symptom-name',
          detail: 'A symptom with this name already exists.',
        }),
    })

    const error = await apiFetch('/things', {}, thingSchema).catch((e: unknown) => e)

    expect(error).toMatchObject<FetchError>({
      kind: 'server-problem',
      status: 409,
      type: 'https://api.symptomwitch.local/errors/duplicate-symptom-name',
      detail: 'A symptom with this name already exists.',
    })
  })

  it('throws FetchError { kind: "api" } with only status when body has no type or detail', async () => {
    mockFetch.mockResolvedValueOnce({
      ok: false,
      status: 500,
      json: () => Promise.resolve({}),
    })

    const error = await apiFetch('/things', {}, thingSchema).catch((e: unknown) => e)

    expect(error).toMatchObject<FetchError>({ kind: 'server-problem', status: 500 })
  })

  it('throws FetchError { kind: "invalid-response" } when success body does not match schema', async () => {
    mockFetch.mockResolvedValueOnce({
      ok: true,
      json: () => Promise.resolve({ unexpected: 'shape' }),
    })

    const error = await apiFetch('/things', {}, thingSchema).catch((e: unknown) => e)

    expect(error).toMatchObject<FetchError>({ kind: 'invalid-response' })
  })

  it('returns parsed response when body matches schema', async () => {
    mockFetch.mockResolvedValueOnce({
      ok: true,
      json: () => Promise.resolve({ id: 'abc-123' }),
    })

    const result = await apiFetch('/things', {}, thingSchema)

    expect(result).toEqual({ id: 'abc-123' })
  })
})
