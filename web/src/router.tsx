import { createRootRoute, createRoute, createRouter, Outlet } from '@tanstack/react-router'

import { HomeRouteView } from '@/routes/home'
import { PrivacyRouteView } from '@/routes/privacy'
import { SymptomsRouteView } from '@/routes/symptoms'
import { TermsRouteView } from '@/routes/terms'

const rootRoute = createRootRoute({
  component: () => <Outlet />,
})

const indexRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/',
  component: HomeRouteView,
})

const privacyRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/privacy',
  component: PrivacyRouteView,
})

const termsRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/terms',
  component: TermsRouteView,
})

const symptomsRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/symptoms',
  component: SymptomsRouteView,
})

const routeTree = rootRoute.addChildren([indexRoute, symptomsRoute, privacyRoute, termsRoute])

export const router = createRouter({ routeTree })

declare module '@tanstack/react-router' {
  interface Register {
    router: typeof router
  }
}
