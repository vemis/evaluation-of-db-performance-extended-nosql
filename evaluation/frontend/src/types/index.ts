export type OrchestratorQueryType = {
  query: string
  services?: string
  repetitions?: number
}

export type OrchestratorType = {
  query: string
  description: string
  cayenne?: MetricType
  ebean?: MetricType
  jooq?: MetricType
  jdbc?: MetricType
  myBatis?: MetricType
  springDataJpa?: MetricType
  morphia?: MetricType
}

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
