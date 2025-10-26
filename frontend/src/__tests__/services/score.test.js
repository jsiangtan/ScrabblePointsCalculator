import { describe, it, expect, beforeEach, vi } from 'vitest'
import { scoreWord, saveScore, getTopScores } from '@/services/score'

const mockResponse = ({ ok = true, jsonData = null, textData = '' } = {}) => ({
  ok,
  json: async () => jsonData,
  text: async () => textData,
})

describe('services/score', () => {
  beforeEach(() => {
    vi.restoreAllMocks()
  })

  it('scoreWord resolves JSON on success and calls fetch with /api/score', async () => {
    const resp = { word: 'HELLO', score: 8, breakdown: [] }
    global.fetch = vi.fn().mockResolvedValue(mockResponse({ ok: true, jsonData: resp }))

    const result = await scoreWord('HELLO')
    expect(result).toEqual(resp)
    expect(global.fetch).toHaveBeenCalled()
    expect(global.fetch.mock.calls[0][0]).toEqual(expect.stringContaining('/api/score'))
  })

  it('scoreWord throws with server text when response not ok', async () => {
    global.fetch = vi.fn().mockResolvedValue(mockResponse({ ok: false, textData: 'bad' }))

    await expect(scoreWord('X')).rejects.toThrow('bad')
  })

  it('saveScore returns true on success', async () => {
    global.fetch = vi.fn().mockResolvedValue(mockResponse({ ok: true, jsonData: {} }))
    await expect(saveScore('HELLO')).resolves.toBe(true)
    expect(global.fetch).toHaveBeenCalled()
    expect(global.fetch.mock.calls[0][0]).toEqual(expect.stringContaining('/api/score/save'))
  })

  it('saveScore throws server error message when response not ok', async () => {
    global.fetch = vi.fn().mockResolvedValue(mockResponse({ ok: false, jsonData: { error: 'exists' } }))
    await expect(saveScore('HELLO')).rejects.toThrow('exists')
  })

  it('getTopScores returns parsed array on success', async () => {
    const data = [{ word: 'A', score: 1 }]
    global.fetch = vi.fn().mockResolvedValue(mockResponse({ ok: true, jsonData: data }))
    await expect(getTopScores(5)).resolves.toEqual(data)
    expect(global.fetch.mock.calls[0][0]).toEqual(expect.stringContaining('/api/score/top'))
  })

  it('getTopScores throws on non-ok response', async () => {
    global.fetch = vi.fn().mockResolvedValue(mockResponse({ ok: false }))
    await expect(getTopScores()).rejects.toThrow('Failed to load top scores')
  })
})
