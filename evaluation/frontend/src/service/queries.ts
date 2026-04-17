import type { OrchestratorQueryType, OrchestratorType } from '../types'

const orchestratorUrl =
  process.env.VITE_ORCHESTRATOR_API_URL || 'http://localhost:8100'

export const fethchOrchestratorQuery = async ({
  query,
  repetitions = 1,
  services,
}: OrchestratorQueryType): Promise<OrchestratorType> => {
  const queryParam = new URLSearchParams({
    repetitions: repetitions.toString(),
    ...(services && { services }),
  }).toString()
  const response = await fetch(
    `${orchestratorUrl}/orchestrator/${query}?${queryParam}`,
    {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    },
  )

  if (!response.ok) {
    throw new Error('Network response was not ok')
  }

  const data = await response.json()
  return data
}

type QueryDescriptionType = {
  [key: string]: string
}

export const fetchQueryEndpoints = async (): Promise<QueryDescriptionType> => {
  const response = await fetch(`${orchestratorUrl}/getQueryDescriptions`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  })

  if (!response.ok) {
    throw new Error('Network response was not ok')
  }

  const data = await response.json()
  return data
}

export const fetchEmbeddedQueryEndpoints = async (): Promise<string[]> => {
  const response = await fetch(`${orchestratorUrl}/getEmbeddedQueryEndpoints`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  })

  if (!response.ok) {
    throw new Error('Network response was not ok')
  }

  const data = await response.json()
  return data
}
