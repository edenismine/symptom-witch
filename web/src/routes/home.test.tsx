import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'

import { HomeRouteView } from '@/routes/home'

describe('HomeRouteView', () => {
  it('renders the shell and symptom draft form', async () => {
    const queryClient = new QueryClient()

    render(
      <QueryClientProvider client={queryClient}>
        <HomeRouteView />
      </QueryClientProvider>,
    )

    expect(await screen.findByText('Symptom Witch shell is ready')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Save draft' })).toBeInTheDocument()
    expect(screen.getByPlaceholderText('Headache')).toBeInTheDocument()
  })
})
