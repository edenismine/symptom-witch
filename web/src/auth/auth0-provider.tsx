import { Auth0Provider } from '@auth0/auth0-react'
import type { ReactNode } from 'react'

export interface Auth0RuntimeConfig {
  domain: string
  clientId: string
  audience: string
}

export function getAuth0RuntimeConfig(env: ImportMetaEnv = import.meta.env): Auth0RuntimeConfig {
  return {
    domain: env.VITE_AUTH0_DOMAIN ?? '',
    clientId: env.VITE_AUTH0_CLIENT_ID ?? '',
    audience: env.VITE_AUTH0_AUDIENCE ?? '',
  }
}

interface Auth0AppProviderProps {
  children: ReactNode
  config?: Auth0RuntimeConfig
}

export function Auth0AppProvider({
  children,
  config = getAuth0RuntimeConfig(),
}: Auth0AppProviderProps) {
  return (
    <Auth0Provider
      domain={config.domain}
      clientId={config.clientId}
      cacheLocation="localstorage"
      useRefreshTokens={true}
      authorizationParams={{
        redirect_uri: window.location.origin,
        audience: config.audience,
      }}
    >
      {children}
    </Auth0Provider>
  )
}
