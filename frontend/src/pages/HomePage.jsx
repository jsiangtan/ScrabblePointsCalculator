import React, { useState, useMemo, useEffect } from 'react'
import { Input, Button, Typography, Space, Card, message } from 'antd'
import 'antd/dist/reset.css'
import { scoreWord, saveScore, getTopScores  } from '../services/score'
import TopScoresModal from '../components/modal/TopScoresModal'

const { Title, Text } = Typography

const TILE_COUNT = 10

export default function HomePage() {
  const [tiles, setTiles] = useState(Array(TILE_COUNT).fill(''))
  const inputRefs = Array.from({ length: TILE_COUNT }).map(() => React.createRef())
  const [showTop, setShowTop] = useState(false)
  const [topScoresState, setTopScoresState] = useState([])
  const [loadingTop, setLoadingTop] = useState(false)

  const [total, setTotal] = useState(0)
  const [breakdown, setBreakdown] = useState([])

  // debounce scoring calls
  useEffect(() => {
    let cancelled = false
    const timer = setTimeout(async () => {
      const word = tiles.join('')
      try {
        const res = await scoreWord(word)
        if (!cancelled) {
          setTotal(res.score)
          setBreakdown(res.breakdown || [])
        }
      } catch (err) {
        // ignore errors for live scoring
      }
    }, 200)
    return () => { cancelled = true; clearTimeout(timer) }
  }, [tiles])

  const setTile = (index, value) => {
    const ch = value.slice(0,1)
    const newTiles = tiles.slice()
    newTiles[index] = ch
    setTiles(newTiles)
    if (ch && index + 1 < TILE_COUNT) {
      const next = inputRefs[index + 1]
      if (next && next.current) next.current.focus()
    }
  }

  const resetTiles = () => {
    setTiles(Array(TILE_COUNT).fill(''))
  }

  const save = async () => {
    const raw = tiles.join('')
    const word = raw.trim()
    if (!word) {
      message.error('Invalid input: empty word')
      return
    }
    try {
      await saveScore(word)
      message.success('Score saved Successfully')
    } catch (err) {
      // show server-provided message when available
      const errMsg = err?.message || 'Could not save score'
      message.error(errMsg)
    }
  }

  const loadTopScores = async () => {
    setLoadingTop(true)
    try {
      const data = await getTopScores(10)
      setTopScoresState(data)
    } catch (err) {
      message.error('Failed to load top scores')
    } finally {
      setLoadingTop(false)
    }
  }

  return (
    <div className="container">
      <Card>
        <Space direction="vertical" style={{ width: '100%', alignItems: 'center' }}>
          <Title level={3}>Scrabble Points Calculator</Title>

          <div style={{ display: 'flex', overflowX: 'auto', paddingBottom: 8 }}>
            {tiles.map((t,i) => (
              <div key={i} style={{ minWidth: 48 }}>
                <Input
                  ref={inputRefs[i]}
                  value={t}
                  onChange={e => setTile(i, e.target.value)}
                  maxLength={1}
                  style={{ textAlign: 'center', width: 48, borderRadius: 0 }}
                />
              </div>
            ))}
          </div>

          <div style={{ paddingBottom: 16 }}>
            <Text>Total score: <strong>{total}</strong></Text>
          </div>

          <Space>
            <Button onClick={resetTiles}>Reset Tiles</Button>
            <Button type="primary" onClick={save}>Save Score</Button>
            <Button onClick={() => { setShowTop(true); loadTopScores(); }}>View Top Scores</Button>
          </Space>

          <TopScoresModal
            open={showTop}
            onClose={() => setShowTop(false)}
            topScores={topScoresState}
            loading={loadingTop}
          />

        </Space>
      </Card>
    </div>
  )
}
