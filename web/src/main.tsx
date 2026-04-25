import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { RouterProvider } from '@tanstack/react-router'

import { Auth0AppProvider } from '@/auth/auth0-provider'
import { router } from '@/router'

const queryClient = new QueryClient()

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
      <Auth0AppProvider>
        <RouterProvider router={router} />
      </Auth0AppProvider>
    </QueryClientProvider>
  </StrictMode>,
)
