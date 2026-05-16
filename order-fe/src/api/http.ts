import axios from 'axios'

const jsonHeaders = {
  Accept: 'application/json',
  'Content-Type': 'application/json',
} as const

type PostJsonOptions<TRequest> = {
  baseUrl: string
  path: string
  data: TRequest
}

export async function postJson<TResponse, TRequest>({
  baseUrl,
  path,
  data,
}: PostJsonOptions<TRequest>) {
  const response = await axios.post<TResponse>(`${baseUrl}${path}`, data, {
    headers: jsonHeaders,
  })

  return response.data
}
