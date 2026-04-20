const metricResponse = {
    type: 'object',
    properties: {
        elapsed:          { type: 'number',  description: 'Mean execution time per iteration (ms)' },
        minTime:          { type: 'number',  description: 'Minimum iteration time (ms)' },
        maxTime:          { type: 'number',  description: 'Maximum iteration time (ms)' },
        delta:            { type: 'number',  description: 'Average heap delta per iteration (bytes)' },
        minMemory:        { type: 'number',  description: 'Minimum heap delta (bytes)' },
        maxMemory:        { type: 'number',  description: 'Maximum heap delta (bytes)' },
        result:           { type: 'integer', description: 'Result set size of the last iteration' },
        repetitions:      { type: 'integer', description: 'Number of measurement iterations executed' },
        iterationResults: { type: 'array',   description: 'Per-iteration detail' },
        status:           { type: 'string',  enum: ['success', 'error'] }
    }
};

const repParam = {
    name: 'repetitions', in: 'query', required: false,
    schema: { type: 'integer', default: 10 },
    description: 'Number of measurement iterations (warmup excluded)'
};

export function queryEndpoint(summary) {
    return {
        get: {
            summary,
            parameters: [repParam],
            responses: { 200: { description: 'Benchmark result', content: { 'application/json': { schema: metricResponse } } } }
        }
    };
}

export function queryEndpointExtra(summary, extraParams) {
    return {
        get: {
            summary,
            parameters: [...extraParams, repParam],
            responses: { 200: { description: 'Benchmark result', content: { 'application/json': { schema: metricResponse } } } }
        }
    };
}

export function loaderEndpoint(summary) {
    return {
        post: {
            summary,
            responses: { 200: { description: 'Load result message', content: { 'text/plain': { schema: { type: 'string' } } } } }
        }
    };
}

const commonTags = [
    { name: 'Health' },
    { name: 'A — Selection/Projection' },
    { name: 'B — Aggregation' },
    { name: 'C — Joins' },
    { name: 'D — Set Operations' },
    { name: 'E — Result Modification' },
    { name: 'Q — TPC-H Queries' },
    { name: 'R — Embedded Queries' },
    { name: 'Loader' }
];

const commonPaths = {
    '/health': {
        get: {
            tags: ['Health'],
            summary: 'Health check',
            responses: { 200: { description: 'OK', content: { 'text/plain': { schema: { type: 'string', example: 'OK' } } } } }
        }
    },

    '/a1': { get: { ...queryEndpoint('A1 — Full scan lineitem (SELECT * FROM lineitem)').get,          tags: ['A — Selection/Projection'] } },
    '/a2': {
        get: {
            ...queryEndpointExtra('A2 — Range query on orders by date', [
                { name: 'startDate', in: 'query', schema: { type: 'string',  default: '1996-01-01' } },
                { name: 'endDate',   in: 'query', schema: { type: 'string',  default: '1996-12-31' } }
            ]).get,
            tags: ['A — Selection/Projection']
        }
    },
    '/a3': { get: { ...queryEndpoint('A3 — Full scan customer (SELECT * FROM customer)').get,          tags: ['A — Selection/Projection'] } },
    '/a4': {
        get: {
            ...queryEndpointExtra('A4 — Range query on orders by PK', [
                { name: 'minOrderKey', in: 'query', schema: { type: 'integer', default: 1000  } },
                { name: 'maxOrderKey', in: 'query', schema: { type: 'integer', default: 50000 } }
            ]).get,
            tags: ['A — Selection/Projection']
        }
    },

    '/b1': { get: { ...queryEndpoint('B1 — COUNT orders grouped by month').get,                       tags: ['B — Aggregation'] } },
    '/b2': { get: { ...queryEndpoint('B2 — MAX extended price grouped by ship month').get,             tags: ['B — Aggregation'] } },

    '/c1': { get: { ...queryEndpoint('C1 — Cartesian product customer × orders').get,                 tags: ['C — Joins'] } },
    '/c2': { get: { ...queryEndpoint('C2 — Inner join customer ⋈ orders').get,                        tags: ['C — Joins'] } },
    '/c3': { get: { ...queryEndpoint('C3 — 3-way join customer ⋈ nation ⋈ orders').get,               tags: ['C — Joins'] } },
    '/c4': { get: { ...queryEndpoint('C4 — 4-way join customer ⋈ nation ⋈ region ⋈ orders').get,     tags: ['C — Joins'] } },
    '/c5': { get: { ...queryEndpoint('C5 — Left outer join customer ⟕ orders').get,                   tags: ['C — Joins'] } },

    '/d1': { get: { ...queryEndpoint('D1 — UNION customer nationkeys ∪ supplier nationkeys').get,     tags: ['D — Set Operations'] } },
    '/d2': { get: { ...queryEndpoint('D2 — INTERSECT (simulated via $lookup match)').get,             tags: ['D — Set Operations'] } },
    '/d3': { get: { ...queryEndpoint('D3 — EXCEPT (simulated via $lookup no-match)').get,             tags: ['D — Set Operations'] } },

    '/e1': { get: { ...queryEndpoint('E1 — Sort customer by acctbal DESC (non-indexed)').get,         tags: ['E — Result Modification'] } },
    '/e2': { get: { ...queryEndpoint('E2 — Sort orders by PK ASC (indexed)').get,                     tags: ['E — Result Modification'] } },
    '/e3': { get: { ...queryEndpoint('E3 — DISTINCT nationkey + mktsegment from customer').get,       tags: ['E — Result Modification'] } },

    '/q1': { get: { ...queryEndpoint('Q1 — Pricing summary report').get,                              tags: ['Q — TPC-H Queries'] } },
    '/q2': { get: { ...queryEndpoint('Q2 — Minimum cost supplier').get,                               tags: ['Q — TPC-H Queries'] } },
    '/q3': { get: { ...queryEndpoint('Q3 — Shipping priority').get,                                   tags: ['Q — TPC-H Queries'] } },
    '/q4': { get: { ...queryEndpoint('Q4 — Order priority checking').get,                             tags: ['Q — TPC-H Queries'] } },
    '/q5': { get: { ...queryEndpoint('Q5 — Local supplier volume').get,                               tags: ['Q — TPC-H Queries'] } },

    '/r1': { get: { ...queryEndpoint('R1 — Embedded lineitems: filter by l_quantity > 5').get,                     tags: ['R — Embedded Queries'] } },
    '/r2': { get: { ...queryEndpoint('R2 — Embedded lineitems: filter by l_partkey > 20000 (indexed)').get,        tags: ['R — Embedded Queries'] } },
    '/r3': { get: { ...queryEndpoint('R3 — Array tags: find orders with tag "MAIL"').get,                          tags: ['R — Embedded Queries'] } },
    '/r4': { get: { ...queryEndpoint('R4 — Indexed array tags: find orders with tag "MAIL"').get,                  tags: ['R — Embedded Queries'] } },
    '/r5': { get: { ...queryEndpoint('R5 — Nested document: filter by region name "AMERICA"').get,                 tags: ['R — Embedded Queries'] } },
    '/r6': { get: { ...queryEndpoint('R6 — Regex text search on comment (no index)').get,                          tags: ['R — Embedded Queries'] } },
    '/r7': { get: { ...queryEndpoint('R7 — Text index search on comment').get,                                     tags: ['R — Embedded Queries'] } },
    '/r8': { get: { ...queryEndpoint('R8 — Unwind embedded lineitems array').get,                                  tags: ['R — Embedded Queries'] } },
    '/r9': { get: { ...queryEndpoint('R9 — Aggregation: sum revenue per order from embedded lineitems').get,       tags: ['R — Embedded Queries'] } },

    '/load':         { post: { ...loaderEndpoint('Load relational TPC-H collections (idempotent)').post,  tags: ['Loader'] } },
    '/loadEmbedded': { post: { ...loaderEndpoint('Load embedded TPC-H collections (idempotent)').post,   tags: ['Loader'] } }
};

/**
 * @param {{ title: string, description: string }} info
 */
export function buildSwaggerSpec({ title, description }) {
    return {
        openapi: '3.0.3',
        info: { title, description, version: '1.0.0' },
        tags: commonTags,
        paths: commonPaths
    };
}
