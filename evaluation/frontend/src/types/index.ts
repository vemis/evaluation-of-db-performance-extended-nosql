export type OrchestratorQueryType = {
  query: string
  services?: string
  repetitions?: number
}

export const SERVICES = [
  { id: 'myBatis',       label: 'MyBatis',           supportsEmbedded: false },
  { id: 'jooq',          label: 'JOOQ',              supportsEmbedded: false },
  { id: 'jdbc',          label: 'JDBC',              supportsEmbedded: false },
  { id: 'ebean',         label: 'Ebean',             supportsEmbedded: false },
  { id: 'cayenne',       label: 'Cayenne',           supportsEmbedded: false },
  { id: 'springDataJpa', label: 'Spring Data JPA',   supportsEmbedded: false },
  { id: 'morphia',             label: 'Morphia (MongoDB)',          supportsEmbedded: true  },
  { id: 'couchbaseSpringData', label: 'Spring Data (Couchbase)',    supportsEmbedded: true  },
  { id: 'springDataMongoDB',        label: 'Spring Data (MongoDB)',      supportsEmbedded: true  },
  { id: 'mongooseMongoDBJavascript',    label: 'Mongoose (MongoDB)',          supportsEmbedded: true  },
  { id: 'ottomanJSCouchbaseJavascript', label: 'Ottoman.js (Couchbase)',      supportsEmbedded: true  },
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
