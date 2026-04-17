import { useQuery } from '@tanstack/react-query'
import { fetchEmbeddedQueryEndpoints } from '../service/queries'

export const useEmbeddedQueryEndpoints = () => {
  return useQuery({
    queryKey: ['embeddedQueryEndpoints'],
    queryFn: fetchEmbeddedQueryEndpoints,
    refetchOnWindowFocus: false,
  })
}
