import { Button, Carousel, Table } from 'antd'
import type { OrchestratorType } from '../types'
import { Column } from '@ant-design/plots'
import styles from './OrchestratorResultTable.module.scss'
import Title from 'antd/es/typography/Title'
import { exportOrchestratorCsv } from '../service/utils'

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

  const ormNames: Record<string, string> = {
    cayenne: 'Cayenne',
    ebean: 'Ebean',
    jooq: 'JOOQ',
    jdbc: 'JDBC',
    myBatis: 'MyBatis',
    springDataJpa: 'Spring Data JPA',
    morphia: 'Morphia (MongoDB)',
  }

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

  const chartConfig = {
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

  const timeChartConfig = {
    data: chartData,
    xField: 'name',
    yField: 'averageExecutionTime',
    ...chartConfig,
  }

  const memoryChartConfig = {
    data: chartData,
    xField: 'name',
    yField: 'averageMemoryUsage',
    ...chartConfig,
  }

  const maxExecutionTimeChartConfig = {
    data: chartData,
    xField: 'name',
    yField: 'maxExecutionTime',
    ...chartConfig,
  }
  const minExecutionTimeChartConfig = {
    data: chartData,
    xField: 'name',
    yField: 'minExecutionTime',
    ...chartConfig,
  }
  const maxMemoryUsageChartConfig = {
    data: chartData,
    xField: 'name',
    yField: 'maxMemoryUsage',
    ...chartConfig,
  }
  const minMemoryUsageChartConfig = {
    data: chartData,
    xField: 'name',
    yField: 'minMemoryUsage',
    ...chartConfig,
  }

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
          <div>
            <div className={styles.slide}>
              <Title level={3} className={styles.chartTitle}>
                Average Execution Time (ms)
              </Title>
              <Column {...timeChartConfig} />
            </div>
          </div>
          <div>
            <div className={styles.slide}>
              <Title level={3} className={styles.chartTitle}>
                Average Memory Usage (B)
              </Title>
              <Column {...memoryChartConfig} />
            </div>
          </div>
          <div>
            <div className={styles.slide}>
              <Title level={3} className={styles.chartTitle}>
                Max Execution Time (ms)
              </Title>
              <Column {...maxExecutionTimeChartConfig} />
            </div>
          </div>
          <div>
            <div className={styles.slide}>
              <Title level={3} className={styles.chartTitle}>
                Min Execution Time (ms)
              </Title>
              <Column {...minExecutionTimeChartConfig} />
            </div>
          </div>
          <div>
            <div className={styles.slide}>
              <Title level={3} className={styles.chartTitle}>
                Max Memory Usage (B)
              </Title>
              <Column {...maxMemoryUsageChartConfig} />
            </div>
          </div>
          <div>
            <div className={styles.slide}>
              <Title level={3} className={styles.chartTitle}>
                Min Memory Usage (B)
              </Title>
              <Column {...minMemoryUsageChartConfig} />
            </div>
          </div>
        </Carousel>
      </div>
    </div>
  )
}
