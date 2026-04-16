export type OrchestratorQueryType = {
  query: string
  services?: string
  repetitions?: number
}

export const SERVICES = [
  { id: 'myBatis',      label: 'MyBatis' },
  { id: 'jooq',         label: 'JOOQ' },
  { id: 'jdbc',         label: 'JDBC' },
  { id: 'ebean',        label: 'Ebean' },
  { id: 'cayenne',      label: 'Cayenne' },
  { id: 'springDataJpa', label: 'Spring Data JPA' },
  { id: 'morphia',      label: 'Morphia (MongoDB)' },
] as const

export type ServiceId = (typeof SERVICES)[number]['id']

export type OrchestratorType = {
  query: string
  description: string
} & Partial<Record<ServiceId, MetricType>>

type JfrType = {
  gcCount: number
  allocatedInsideTLAB: number
  heapUsedAvg: number
  allocatedOutsideTLAB: number
  totalAllocated: number
}

export type ServiceMetricType = {
  elapsed: number
  result: number
  jfr: JfrType
  delta: number
  status: string
}

export type MetricType = {
  status: string
  repetition: number
  averageExecutionTime: number
  averageMemoryUsage: number
  maxExecutionTime: number
  minExecutionTime: number
  maxMemoryUsage: number
  minMemoryUsage: number
  iterationResults: ServiceMetricType[]
}

export type OrchestratorSearchParamsType = {
  items: string[]
  query: string
  repetitions: number
}
