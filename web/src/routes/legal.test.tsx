import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'

import { PrivacyRouteView } from '@/routes/privacy'
import { TermsRouteView } from '@/routes/terms'

describe('Legal routes', () => {
  it('renders the privacy placeholder page content', () => {
    render(<PrivacyRouteView />)

    expect(screen.getByRole('heading', { name: 'Privacy' })).toBeInTheDocument()
    expect(screen.getByText('Privacy details are coming soon.')).toBeInTheDocument()
  })

  it('renders the terms placeholder page content', () => {
    render(<TermsRouteView />)

    expect(screen.getByRole('heading', { name: 'Terms' })).toBeInTheDocument()
    expect(screen.getByText('Terms of use are coming soon.')).toBeInTheDocument()
  })
})
