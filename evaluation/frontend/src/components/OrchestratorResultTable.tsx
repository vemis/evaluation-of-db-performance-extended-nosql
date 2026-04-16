import { Button, Carousel, Table } from 'antd'
import type { OrchestratorType } from '../types'
import { SERVICES } from '../types'
import { Column } from '@ant-design/plots'
import styles from './OrchestratorResultTable.module.scss'
import Title from 'antd/es/typography/Title'
import { exportOrchestratorCsv } from '../service/utils'

const ormNames: Record<string, string> = Object.fromEntries(
  SERVICES.map(({ id, label }) => [id, label]),
)

export const OrchestratorResultTable: React.FC<{
  data: OrchestratorType
  microservices?: string[]
}> = ({ data, microservices }) => {
  const columns = [
    { title: 'Name', dataIndex: 'name', key: 'name' },
    { title: 'Repetitions', dataIndex: 'repetition', key: 'repetition' },
    {
      title: 'Average Time Execution (ms)',
      dataIndex: 'averageExecutionTime',
      key: 'averageExecutionTime',
    },
    {
      title: 'Average Memory Usage (B)',
      dataIndex: 'averageMemoryUsage',
      key: 'averageMemoryUsage',
    },
    {
      title: 'Max Time Execution (ms)',
      dataIndex: 'maxExecutionTime',
      key: 'maxExecutionTime',
    },
    {
      title: 'Min Time Execution (ms)',
      dataIndex: 'minExecutionTime',
      key: 'minExecutionTime',
    },
    {
      title: 'Max Memory Usage (B)',
      dataIndex: 'maxMemoryUsage',
      key: 'maxMemoryUsage',
    },
    {
      title: 'Min Memory Usage (B)',
      dataIndex: 'minMemoryUsage',
      key: 'minMemoryUsage',
    },
  ]

  const rows = Object.entries(data)
    .filter(
      ([key, value]) =>
        typeof value === 'object' &&
        value !== null &&
        microservices?.includes(key),
    )
    .map(([key, value]) => {
      if (typeof value !== 'object') {
        return null
      }

      return {
        name: ormNames[key] || key,
        repetition: value.repetition,
        averageExecutionTime: value.averageExecutionTime,
        averageMemoryUsage: value.averageMemoryUsage,
        maxExecutionTime: value.maxExecutionTime,
        minExecutionTime: value.minExecutionTime,
        maxMemoryUsage: value.maxMemoryUsage,
        minMemoryUsage: value.minMemoryUsage,
        iterationResults: value.iterationResults || [],
      }
    })

  const chartData = Object.entries(rows).map(([key, value]) => ({
    name: value?.name || key,
    averageExecutionTime: value?.averageExecutionTime,
    averageMemoryUsage: value?.averageMemoryUsage,
    maxExecutionTime: value?.maxExecutionTime,
    minExecutionTime: value?.minExecutionTime,
    maxMemoryUsage: value?.maxMemoryUsage,
    minMemoryUsage: value?.minMemoryUsage,
  }))

  const baseChartConfig = {
    width: 600,
    height: 400,
    lazyLoad: true,
    centerMode: true,
    legend: {
      position: 'top-left',
    },
    style: {
      radiusTopLeft: 10,
      radiusTopRight: 10,
    },
  }

  const charts: { title: string; yField: keyof (typeof chartData)[number] }[] = [
    { title: 'Average Execution Time (ms)', yField: 'averageExecutionTime' },
    { title: 'Average Memory Usage (B)',    yField: 'averageMemoryUsage' },
    { title: 'Max Execution Time (ms)',     yField: 'maxExecutionTime' },
    { title: 'Min Execution Time (ms)',     yField: 'minExecutionTime' },
    { title: 'Max Memory Usage (B)',        yField: 'maxMemoryUsage' },
    { title: 'Min Memory Usage (B)',        yField: 'minMemoryUsage' },
  ]

  return (
    <div className={styles.container}>
      <Table
        dataSource={rows}
        columns={columns}
        rowKey="name"
        pagination={false}
        bordered
        expandable={{
          expandedRowRender: (record) => (
            <Table
              dataSource={record?.iterationResults}
              columns={[
                { title: 'Iteration', render: (_, __, index) => index + 1 },
                { title: 'Time (ms)', dataIndex: 'elapsed', key: 'elapsed' },
                { title: 'Memory (B)', dataIndex: 'delta', key: 'delta' },
              ]}
              rowKey="service"
              pagination={false}
            />
          ),
        }}
      />
      <Button
        type="primary"
        onClick={() => exportOrchestratorCsv(data)}
        style={{ margin: '16px 0' }}
      >
        Export CSV
      </Button>
      <div>
        <Carousel
          style={{
            maxWidth: 700,
          }}
          arrows
          dots={false}
          draggable
          // autoplay
          // autoplaySpeed={5000}
        >
          {charts.map(({ title, yField }) => (
            <div key={yField}>
              <div className={styles.slide}>
                <Title level={3} className={styles.chartTitle}>
                  {title}
                </Title>
                <Column data={chartData} xField="name" yField={yField} {...baseChartConfig} />
              </div>
            </div>
          ))}
        </Carousel>
      </div>
    </div>
  )
}
