import { useForm } from '@tanstack/react-form'
import { useQuery } from '@tanstack/react-query'
import { useAuth0 } from '@auth0/auth0-react'

import { Button } from '@/components/ui/button'

function fetchWelcomeMessage(): Promise<string> {
  return Promise.resolve('Symptom Witch shell is ready')
}

export function HomeRouteView() {
  const { isAuthenticated, isLoading, loginWithRedirect, logout } = useAuth0()
  const messageQuery = useQuery({
    queryKey: ['welcome-message'],
    queryFn: fetchWelcomeMessage,
  })

  const form = useForm({
    defaultValues: {
      symptomName: '',
    },
    onSubmit: ({ value }) => value,
  })

  return (
    <main className="mx-auto flex min-h-screen w-full max-w-3xl flex-col gap-6 px-6 py-10">
      <header className="flex flex-col gap-2">
        <h1 className="text-2xl font-semibold">Symptom Witch</h1>
        <p className="text-muted-foreground">{messageQuery.data ?? 'Loading app status...'}</p>
      </header>

      {isLoading ? (
        <section className="flex flex-col gap-4 rounded-lg border p-4">
          <p className="text-sm text-muted-foreground">Checking your session...</p>
        </section>
      ) : isAuthenticated ? (
        <>
          <section className="flex items-center justify-between">
            <a className="text-sm font-medium underline underline-offset-2" href="/symptoms">
              Manage symptoms
            </a>
            <Button
              type="button"
              variant="outline"
              onClick={() => logout({ logoutParams: { returnTo: window.location.origin } })}
            >
              Sign out
            </Button>
          </section>

          <form
            className="flex flex-col gap-4 rounded-lg border p-4"
            onSubmit={(event) => {
              event.preventDefault()
              event.stopPropagation()
              form.handleSubmit()
            }}
          >
            <form.Field name="symptomName">
              {(field) => (
                <label className="flex flex-col gap-2 text-sm">
                  Symptom to track
                  <input
                    className="h-10 rounded-md border bg-background px-3"
                    name={field.name}
                    value={field.state.value}
                    onBlur={field.handleBlur}
                    onChange={(event) => field.handleChange(event.target.value)}
                    placeholder="Headache"
                  />
                </label>
              )}
            </form.Field>
            <Button type="submit">Save draft</Button>
          </form>
        </>
      ) : (
        <section className="flex flex-col gap-4 rounded-lg border p-4">
          <p className="text-sm text-muted-foreground">Track patterns before symptoms escalate.</p>
          <div className="flex flex-wrap items-center gap-3">
            <Button type="button" onClick={() => void loginWithRedirect()}>
              Log in
            </Button>
            <a
              className="text-sm text-muted-foreground underline underline-offset-2"
              href="/privacy"
            >
              Privacy
            </a>
            <a className="text-sm text-muted-foreground underline underline-offset-2" href="/terms">
              Terms
            </a>
          </div>
        </section>
      )}
    </main>
  )
}
