import { useForm } from '@tanstack/react-form'
import { useQuery } from '@tanstack/react-query'

import { Button } from '@/components/ui/button'

function fetchWelcomeMessage(): Promise<string> {
  return Promise.resolve('Symptom Witch shell is ready')
}

export function HomeRouteView() {
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
    </main>
  )
}
