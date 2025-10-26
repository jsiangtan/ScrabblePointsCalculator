const API_BASE = import.meta.env.VITE_API_BASE

export async function scoreWord(word) {
  const resp = await fetch(`${API_BASE}/api/score`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ word })
  })
  if (!resp.ok) {
    const text = await resp.text()
    throw new Error(text || 'Server error')
  }
  return resp.json()
}

export async function saveScore(word) {
  const resp = await fetch(`${API_BASE}/api/score/save`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ word })
  })
  if (!resp.ok) {
    const errorData = await resp.json();
    throw new Error(errorData.error || 'Cannot save score')
  }
  return true
}

export async function getTopScores(limit = 10) {
  const resp = await fetch(`${API_BASE}/api/score/top?limit=${limit}`)
  if (!resp.ok) throw new Error('Failed to load top scores')
  return resp.json()
}

