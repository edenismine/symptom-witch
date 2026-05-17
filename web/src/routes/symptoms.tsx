import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useAuth0 } from '@auth0/auth0-react'
import { useState } from 'react'

import { createSymptom, fetchActiveSymptoms, patchSymptom, type SymptomResponse } from '@/api/symptoms'
import { Button } from '@/components/ui/button'
import { queryKeys } from '@/lib/queryKeys'

export function SymptomsRouteView() {
  const { isAuthenticated, isLoading, getAccessTokenSilently } = useAuth0()
  const queryClient = useQueryClient()
  const [newName, setNewName] = useState('')
  const [renamingId, setRenamingId] = useState<string | null>(null)
  const [renameValue, setRenameValue] = useState('')

  const symptomsQuery = useQuery({
    queryKey: queryKeys.symptoms(),
    queryFn: async () => {
      const token = await getAccessTokenSilently()
      return fetchActiveSymptoms(token)
    },
    enabled: isAuthenticated,
  })

  const createMutation = useMutation({
    mutationFn: async (name: string) => {
      const token = await getAccessTokenSilently()
      return createSymptom(token, name)
    },
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: queryKeys.symptoms() })
      setNewName('')
    },
  })

  const patchMutation = useMutation({
    mutationFn: async ({ id, patch }: { id: string; patch: Parameters<typeof patchSymptom>[2] }) => {
      const token = await getAccessTokenSilently()
      return patchSymptom(token, id, patch)
    },
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: queryKeys.symptoms() })
      setRenamingId(null)
    },
  })

  if (isLoading) {
    return (
      <main className="mx-auto flex min-h-screen w-full max-w-3xl flex-col gap-6 px-6 py-10">
        <p className="text-sm text-muted-foreground">Checking your session...</p>
      </main>
    )
  }

  if (!isAuthenticated) {
    return (
      <main className="mx-auto flex min-h-screen w-full max-w-3xl flex-col gap-6 px-6 py-10">
        <p className="text-sm text-muted-foreground">Please log in to manage your symptoms.</p>
      </main>
    )
  }

  const symptoms: SymptomResponse[] = symptomsQuery.data ?? []

  return (
    <main className="mx-auto flex min-h-screen w-full max-w-3xl flex-col gap-6 px-6 py-10">
      <header className="flex flex-col gap-2">
        <h1 className="text-2xl font-semibold">Symptoms</h1>
      </header>

      <form
        className="flex gap-2"
        onSubmit={(e) => {
          e.preventDefault()
          if (newName.trim()) createMutation.mutate(newName.trim())
        }}
      >
        <input
          className="h-10 flex-1 rounded-md border bg-background px-3 text-sm"
          placeholder="New symptom name"
          value={newName}
          onChange={(e) => setNewName(e.target.value)}
          aria-label="New symptom name"
        />
        <Button type="submit" disabled={createMutation.isPending}>
          Add symptom
        </Button>
      </form>

      {symptomsQuery.isError && (
        <p className="text-sm text-destructive">Failed to load symptoms.</p>
      )}

      {symptomsQuery.isSuccess && symptoms.length === 0 && (
        <p className="text-sm text-muted-foreground">No active symptoms yet.</p>
      )}

      {symptoms.length > 0 && (
        <ul className="flex flex-col gap-2">
          {symptoms.map((symptom) => (
            <li key={symptom.id} className="flex items-center gap-3 rounded-lg border px-4 py-3">
              {renamingId === symptom.id ? (
                <form
                  className="flex flex-1 gap-2"
                  onSubmit={(e) => {
                    e.preventDefault()
                    if (renameValue.trim()) {
                      patchMutation.mutate({ id: symptom.id, patch: { name: renameValue.trim() } })
                    }
                  }}
                >
                  <input
                    className="h-8 flex-1 rounded-md border bg-background px-3 text-sm"
                    value={renameValue}
                    onChange={(e) => setRenameValue(e.target.value)}
                    aria-label="Rename symptom"
                    autoFocus
                  />
                  <Button type="submit" size="sm" disabled={patchMutation.isPending}>
                    Save
                  </Button>
                  <Button
                    type="button"
                    variant="ghost"
                    size="sm"
                    onClick={() => setRenamingId(null)}
                  >
                    Cancel
                  </Button>
                </form>
              ) : (
                <>
                  <button
                    type="button"
                    className="flex-1 text-left text-sm font-medium"
                    onClick={() => {
                      setRenamingId(symptom.id)
                      setRenameValue(symptom.name)
                    }}
                  >
                    {symptom.name}
                  </button>
                  <Button
                    type="button"
                    variant="outline"
                    size="sm"
                    onClick={() => patchMutation.mutate({ id: symptom.id, patch: { archived: true } })}
                    disabled={patchMutation.isPending}
                  >
                    Archive
                  </Button>
                </>
              )}
            </li>
          ))}
        </ul>
      )}
    </main>
  )
}
