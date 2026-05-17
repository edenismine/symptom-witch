import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'

import { SymptomsRouteView } from '@/routes/symptoms'

const authState = vi.hoisted(() => ({
  isAuthenticated: true,
  isLoading: false,
  getAccessTokenSilently: vi.fn().mockResolvedValue('test-token'),
}))

vi.mock('@auth0/auth0-react', () => ({
  useAuth0: () => authState,
}))

const mockFetchActiveSymptoms = vi.hoisted(() => vi.fn())
const mockCreateSymptom = vi.hoisted(() => vi.fn())
const mockPatchSymptom = vi.hoisted(() => vi.fn())

vi.mock('@/api/symptoms', () => ({
  fetchActiveSymptoms: mockFetchActiveSymptoms,
  createSymptom: mockCreateSymptom,
  patchSymptom: mockPatchSymptom,
}))

function renderWithQueryClient(ui: React.ReactElement) {
  const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } })
  return render(<QueryClientProvider client={queryClient}>{ui}</QueryClientProvider>)
}

const symptomA = { id: 'id-a', name: 'Anxiety', archived: false, createdAt: '', updatedAt: '' }
const symptomH = { id: 'id-h', name: 'Headache', archived: false, createdAt: '', updatedAt: '' }

describe('SymptomsRouteView', () => {
  afterEach(cleanup)

  beforeEach(() => {
    vi.resetAllMocks()
    authState.isAuthenticated = true
    authState.isLoading = false
    authState.getAccessTokenSilently.mockResolvedValue('test-token')
  })

  it('clicking a symptom name opens inline rename and submitting updates it', async () => {
    const renamed = { ...symptomA, name: 'Anxiety Disorder' }
    mockFetchActiveSymptoms.mockResolvedValueOnce([symptomA]).mockResolvedValueOnce([renamed])
    mockPatchSymptom.mockResolvedValueOnce(renamed)

    renderWithQueryClient(<SymptomsRouteView />)
    expect(await screen.findByText('Anxiety')).toBeInTheDocument()

    fireEvent.click(screen.getByText('Anxiety'))

    const renameInput = screen.getByLabelText('Rename symptom')
    fireEvent.change(renameInput, { target: { value: 'Anxiety Disorder' } })
    fireEvent.click(screen.getByRole('button', { name: 'Save' }))

    expect(await screen.findByText('Anxiety Disorder')).toBeInTheDocument()
    expect(mockPatchSymptom).toHaveBeenCalledWith('test-token', 'id-a', {
      name: 'Anxiety Disorder',
    })
  })

  it('archive button removes the symptom from the active list', async () => {
    mockFetchActiveSymptoms.mockResolvedValueOnce([symptomA]).mockResolvedValueOnce([])
    mockPatchSymptom.mockResolvedValueOnce({ ...symptomA, archived: true })

    renderWithQueryClient(<SymptomsRouteView />)
    expect(await screen.findByText('Anxiety')).toBeInTheDocument()

    fireEvent.click(screen.getByRole('button', { name: 'Archive' }))

    await waitFor(() => expect(screen.queryByText('Anxiety')).not.toBeInTheDocument())
    expect(mockPatchSymptom).toHaveBeenCalledWith('test-token', 'id-a', { archived: true })
  })

  it('add symptom form submits and the new symptom appears in the list', async () => {
    const newSymptom = {
      id: 'id-new',
      name: 'Nausea',
      archived: false,
      createdAt: '',
      updatedAt: '',
    }
    mockFetchActiveSymptoms.mockResolvedValueOnce([]).mockResolvedValueOnce([newSymptom])
    mockCreateSymptom.mockResolvedValueOnce(newSymptom)

    renderWithQueryClient(<SymptomsRouteView />)
    await screen.findByText('No active symptoms yet.')

    fireEvent.change(screen.getByLabelText('New symptom name'), { target: { value: 'Nausea' } })
    fireEvent.click(screen.getByRole('button', { name: 'Add symptom' }))

    expect(await screen.findByText('Nausea')).toBeInTheDocument()
    expect(mockCreateSymptom).toHaveBeenCalledWith('test-token', 'Nausea')
  })

  it('shows empty state when there are no active symptoms', async () => {
    mockFetchActiveSymptoms.mockResolvedValueOnce([])

    renderWithQueryClient(<SymptomsRouteView />)

    expect(await screen.findByText('No active symptoms yet.')).toBeInTheDocument()
  })

  it('renders the list of active symptoms', async () => {
    mockFetchActiveSymptoms.mockResolvedValueOnce([symptomA, symptomH])

    renderWithQueryClient(<SymptomsRouteView />)

    expect(await screen.findByText('Anxiety')).toBeInTheDocument()
    expect(screen.getByText('Headache')).toBeInTheDocument()
  })
})
