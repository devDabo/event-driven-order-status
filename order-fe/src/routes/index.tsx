import { useState } from 'react'
import { createFileRoute } from '@tanstack/react-router'
import { OrderForm } from '#/components/organisms/OrderForm.tsx'
import { useCreateOrderMutation } from '#/hooks/use-create-order.ts'
import type { Order } from '#/types'

export const Route = createFileRoute('/')({
  component: Page,
})

export function Page() {
  const [order, setOrder] = useState<Order>({
    customerId: crypto.randomUUID(),
    price: 0,
  })
  const mutation = useCreateOrderMutation({
    onSuccess: () => {
      setOrder({
        customerId: crypto.randomUUID(),
        price: 0,
      })
    },
  })

  return (
    <main className="page">
      <section className="page-content">
        <OrderForm
          order={order}
          onPriceChange={(price) => setOrder({ ...order, price })}
          isSubmitting={mutation.isPending}
          onSubmit={() => mutation.mutate(order)}
        />
        {mutation.isError ? (
          <p role="alert">
            {mutation.error instanceof Error
              ? mutation.error.message
              : 'Unable to create the order.'}
          </p>
        ) : null}
        {mutation.isSuccess ? (
          <p role="status">Order {mutation.data.id} created successfully.</p>
        ) : null}
      </section>
    </main>
  )
}
