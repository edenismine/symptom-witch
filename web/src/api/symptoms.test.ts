import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'

import { createSymptom, fetchActiveSymptoms, patchSymptom } from '@/api/symptoms'

const mockFetch = vi.fn()

beforeEach(() => {
  vi.stubGlobal('fetch', mockFetch)
})

afterEach(() => {
  vi.unstubAllGlobals()
  vi.resetAllMocks()
})

const symptom = {
  id: 'abc-123',
  name: 'Headache',
  archived: false,
  createdAt: '2026-01-01T00:00:00Z',
  updatedAt: '2026-01-01T00:00:00Z',
}

describe('patchSymptom', () => {
  it('patches the symptom and returns the updated result', async () => {
    const archived = { ...symptom, archived: true }
    mockFetch.mockResolvedValueOnce({ ok: true, json: () => Promise.resolve(archived) })

    const result = await patchSymptom('test-token', 'abc-123', { archived: true })

    expect(result).toEqual(archived)
    expect(mockFetch).toHaveBeenCalledWith(
      expect.stringContaining('/v1/symptoms/abc-123'),
      expect.objectContaining({
        method: 'PATCH',
        body: JSON.stringify({ archived: true }),
      }),
    )
  })
})

describe('createSymptom', () => {
  it('posts the name and returns the created symptom', async () => {
    mockFetch.mockResolvedValueOnce({ ok: true, json: () => Promise.resolve(symptom) })

    const result = await createSymptom('test-token', 'Headache')

    expect(result).toEqual(symptom)
    expect(mockFetch).toHaveBeenCalledWith(
      expect.stringContaining('/v1/symptoms'),
      expect.objectContaining({
        method: 'POST',
        body: JSON.stringify({ name: 'Headache' }),
      }),
    )
  })
})

describe('fetchActiveSymptoms', () => {
  it('returns the parsed list of active symptoms', async () => {
    mockFetch.mockResolvedValueOnce({
      ok: true,
      json: () => Promise.resolve([symptom]),
    })

    const result = await fetchActiveSymptoms('test-token')

    expect(result).toEqual([symptom])
    expect(mockFetch).toHaveBeenCalledWith(
      expect.stringContaining('/v1/symptoms'),
      expect.objectContaining({
        headers: expect.objectContaining({ Authorization: 'Bearer test-token' }),
      }),
    )
  })
})
