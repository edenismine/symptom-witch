import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { cleanup, fireEvent, render, screen } from '@testing-library/react'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'

import { HomeRouteView } from '@/routes/home'

const authState = vi.hoisted(() => {
  return {
    isAuthenticated: false,
    isLoading: false,
    loginWithRedirect: vi.fn(),
    logout: vi.fn(),
  }
})

vi.mock('@auth0/auth0-react', () => ({
  useAuth0: () => ({
    isAuthenticated: authState.isAuthenticated,
    isLoading: authState.isLoading,
    loginWithRedirect: authState.loginWithRedirect,
    logout: authState.logout,
    user: undefined,
  }),
}))

describe('HomeRouteView', () => {
  afterEach(() => {
    cleanup()
  })

  beforeEach(() => {
    authState.isAuthenticated = false
    authState.isLoading = false
    authState.loginWithRedirect.mockReset()
    authState.logout.mockReset()
  })

  it('renders the public landing content for logged-out users', async () => {
    const queryClient = new QueryClient()

    render(
      <QueryClientProvider client={queryClient}>
        <HomeRouteView />
      </QueryClientProvider>,
    )

    expect(await screen.findByText('Symptom Witch shell is ready')).toBeInTheDocument()
    expect(screen.getByText('Track patterns before symptoms escalate.')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Log in' })).toBeInTheDocument()
    expect(screen.queryByRole('button', { name: 'Sign up' })).not.toBeInTheDocument()
    expect(screen.getByRole('link', { name: 'Privacy' })).toBeInTheDocument()
    expect(screen.getByRole('link', { name: 'Terms' })).toBeInTheDocument()
  })

  it('starts login with Auth0 redirect', async () => {
    const queryClient = new QueryClient()

    render(
      <QueryClientProvider client={queryClient}>
        <HomeRouteView />
      </QueryClientProvider>,
    )

    await screen.findByText('Symptom Witch shell is ready')

    fireEvent.click(screen.getByRole('button', { name: 'Log in' }))
    expect(authState.loginWithRedirect).toHaveBeenCalledWith()
  })

  it('does not show logged-out actions while Auth0 restores the session', async () => {
    authState.isLoading = true
    const queryClient = new QueryClient()

    render(
      <QueryClientProvider client={queryClient}>
        <HomeRouteView />
      </QueryClientProvider>,
    )

    expect(await screen.findByText('Symptom Witch shell is ready')).toBeInTheDocument()
    expect(screen.getByText('Checking your session...')).toBeInTheDocument()
    expect(screen.queryByRole('button', { name: 'Log in' })).not.toBeInTheDocument()
  })

  it('renders the shell and supports sign out for authenticated users', async () => {
    authState.isAuthenticated = true
    const queryClient = new QueryClient()

    render(
      <QueryClientProvider client={queryClient}>
        <HomeRouteView />
      </QueryClientProvider>,
    )

    expect(await screen.findByText('Symptom Witch shell is ready')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Save draft' })).toBeInTheDocument()
    expect(screen.queryByRole('button', { name: 'Log in' })).not.toBeInTheDocument()

    fireEvent.click(screen.getByRole('button', { name: 'Sign out' }))
    expect(authState.logout).toHaveBeenCalledWith({
      logoutParams: { returnTo: window.location.origin },
    })
  })
})
