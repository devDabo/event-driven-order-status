import { useMutation } from '@tanstack/react-query'
import type { UseMutationOptions } from '@tanstack/react-query'
import { createServerFn, useServerFn } from '@tanstack/react-start'
import { postJson } from '#/api/http.ts'
import type { Order, OrderResponse } from '#/types'

const createOrderServerFn = createServerFn({ method: 'POST' })
  .inputValidator((order: Order) => order)
  .handler(async ({ data }) => {
    const apiBaseUrl = process.env.ORDER_API_BASE_URL!

    return postJson<OrderResponse, Order>({
      baseUrl: apiBaseUrl,
      path: '/orders',
      data,
    })
  })

type UseCreateOrderMutationOptions = Omit<
  UseMutationOptions<OrderResponse, unknown, Order>,
  'mutationFn'
>

export function useCreateOrderMutation(options?: UseCreateOrderMutationOptions) {
  const createOrder = useServerFn(createOrderServerFn)

  return useMutation<OrderResponse, unknown, Order>({
    mutationFn: (order) => createOrder({ data: order }),
    ...options,
  })
}
