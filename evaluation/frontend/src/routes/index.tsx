import { createFileRoute, useNavigate } from '@tanstack/react-router'
import {
  Button,
  Card,
  Checkbox,
  Form,
  Input,
  Select,
  Typography,
} from 'antd'
import { useQueryEndpoints } from '../hooks/useQueryEndpoints'
import { useState, useEffect } from 'react'
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter'
import { vscDarkPlus } from 'react-syntax-highlighter/dist/esm/styles/prism'
import { format } from 'sql-formatter'
import { SERVICES } from '../types'

export const Route = createFileRoute('/')({
  component: Home,
})

const { Title } = Typography

const options = SERVICES.map(({ id, label }) => ({ label, value: id }))

function Home() {
  const [form] = Form.useForm()
  const { data: queryEndpoints } = useQueryEndpoints()
  const [selectedQuery, setSelectedQuery] = useState<string | undefined>()
  const [formattedQuery, setFormattedQuery] = useState<string>('')
  const navigate = useNavigate()

  // useEffect is used, because it is going to make easier to implement
  // 'prettifier' like formators for other than SQL like strings
  useEffect(() => {
    if (!selectedQuery || !queryEndpoints) {
      setFormattedQuery('')
      return
    }
    const raw = queryEndpoints[selectedQuery]
    try {
      setFormattedQuery(format(raw, { language: 'mysql' }))
    } catch {
      setFormattedQuery(raw)
    }
  }, [selectedQuery, queryEndpoints])

  const allValues = options.map((opt) => opt.value)
  const queryOptions = Object.entries(queryEndpoints || {})
    .map(([key]) => ({
      label: key,
      value: key,
    }))
    .sort((a, b) => a.label.localeCompare(b.label))

  const onFinish = (values: {
    items: string[]
    query: string
    repetitions: number
  }) => {
    navigate({
      to: '/orchestrator',
      search: {
        items: values.items,
        query: values.query,
        repetitions: values.repetitions,
      },
    })
  }

  return (
    <div
      style={{
        display: 'flex',
        justifyContent: 'center',
        flexDirection: 'column',
        alignItems: 'center',
        padding: '16px',
        maxWidth: '600px',
        margin: '0 auto',
      }}
    >
      <Title level={1}>ORM Benchmark</Title>
      <Form
        form={form}
        name="orm-benchmark"
        onFinish={onFinish}
        layout="vertical"
      >
        <div style={{ display: 'flex', gap: '8px' }}>
          <Form.Item
            name="query"
            label="Select Query"
            rules={[{ required: true, message: 'Please select a query!' }]}
            style={{ flexGrow: 1 }}
          >
            <Select
              placeholder="Select a query"
              options={queryOptions}
              style={{ width: '100%' }}
              allowClear
              showSearch
              filterOption={(input, option) =>
                (option?.label ?? '')
                  .toLowerCase()
                  .includes(input.toLowerCase())
              }
              onChange={(value) => setSelectedQuery(value)}
              optionFilterProp="label"
            />
          </Form.Item>
          <Form.Item
            name="repetitions"
            label="Repetitions"
            initialValue={1}
            rules={[{ required: true, message: 'Please input repetitions!' }]}
          >
            <Input type="number" min={1} />
          </Form.Item>
        </div>

        <Form.Item
          noStyle
          shouldUpdate={(prevValues, currentValues) =>
            prevValues.query !== currentValues.query ||
            prevValues.items !== currentValues.items
          }
        >
          {({ getFieldValue }) => {
            const querySelected = !!getFieldValue('query')
            const selectedList: string[] = getFieldValue('items') || []
            const checkedCount = selectedList.length
            const isAllChecked = checkedCount === allValues.length
            const isIndeterminate =
              checkedCount > 0 && checkedCount < allValues.length

            return (
              <>
                <Form.Item name="items" initialValue={[]}>
                  <Checkbox.Group
                    options={options}
                    disabled={!querySelected}
                    style={{ opacity: querySelected ? 1 : 0.4 }}
                  />
                </Form.Item>
                <Form.Item>
                  <Checkbox
                    indeterminate={isIndeterminate}
                    checked={isAllChecked}
                    disabled={!querySelected}
                    style={{ opacity: querySelected ? 1 : 0.4 }}
                    onChange={(e) => {
                      const nextList = e.target.checked ? allValues : []
                      form.setFieldsValue({ items: nextList })
                    }}
                  >
                    Check all
                  </Checkbox>
                </Form.Item>
              </>
            )
          }}
        </Form.Item>

        <Form.Item>
          <Button type="primary" htmlType="submit">
            Execute
          </Button>
        </Form.Item>

        {selectedQuery && queryEndpoints && (
          <Card style={{ maxWidth: 600, margin: '2rem auto' }}>
            <SyntaxHighlighter
              language="sql"
              style={vscDarkPlus}
              customStyle={{
                margin: -24,
                padding: '16px 24px',
                borderRadius: '8px',
              }}
              wrapLines={true}
              wrapLongLines={true}
            >
              {formattedQuery}
            </SyntaxHighlighter>
          </Card>
        )}
      </Form>
    </div>
  )
}
