import React from 'react'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { describe, test, expect, vi, beforeEach } from 'vitest'
import '@testing-library/jest-dom/vitest'
import HomePage from '@/pages/HomePage'

vi.mock('@/services/score', () => ({
  scoreWord: vi.fn(),
  saveScore: vi.fn(),
  getTopScores: vi.fn()
}))

import { scoreWord } from '@/services/score'

describe('HomePage', () => {
  beforeEach(() => vi.clearAllMocks())

  test('renders title and 10 tile inputs and updates total after typing', async () => {
    scoreWord.mockResolvedValue({ word: 'A', score: 1, breakdown: [{ letter: 'A', value: 1 }] })

    render(<HomePage />)

    expect(screen.getByText(/Scrabble Points Calculator/i)).toBeInTheDocument()

    const inputs = screen.getAllByRole('textbox')
    expect(inputs.length).toBe(10)

    fireEvent.change(inputs[0], { target: { value: 'A' } })

    await waitFor(() =>
      expect(screen.getByText(/Total score:/i)).toHaveTextContent('1')
    )
  })
})
