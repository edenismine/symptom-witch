import { render } from '@testing-library/react'
import type { ReactNode } from 'react'
import { describe, expect, it, vi } from 'vitest'

import { Auth0AppProvider, getAuth0RuntimeConfig } from '@/auth/auth0-provider'

const auth0ProviderSpy = vi.hoisted(() => vi.fn())

vi.mock('@auth0/auth0-react', () => ({
  Auth0Provider: ({ children, ...props }: { children: ReactNode }) => {
    auth0ProviderSpy(props)
    return <>{children}</>
  },
}))

describe('Auth0AppProvider', () => {
  it('configures Auth0Provider with refresh token support', () => {
    render(
      <Auth0AppProvider config={{ domain: 'example.auth0.com', clientId: 'client-123' }}>
        <div>child</div>
      </Auth0AppProvider>,
    )

    expect(auth0ProviderSpy).toHaveBeenCalledWith(
      expect.objectContaining({
        domain: 'example.auth0.com',
        clientId: 'client-123',
        cacheLocation: 'memory',
        useRefreshTokens: true,
        authorizationParams: { redirect_uri: window.location.origin },
      }),
    )
  })

  it('reads runtime config from vite environment variables', () => {
    const config = getAuth0RuntimeConfig({
      BASE_URL: '/',
      MODE: 'test',
      DEV: false,
      PROD: false,
      SSR: false,
      VITE_AUTH0_DOMAIN: 'tenant.auth0.com',
      VITE_AUTH0_CLIENT_ID: 'client-id',
    } as ImportMetaEnv)

    expect(config).toEqual({
      domain: 'tenant.auth0.com',
      clientId: 'client-id',
    })
  })
})
