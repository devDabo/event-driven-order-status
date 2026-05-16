import type { Order } from '#/types'
import { Form } from '#/components/molecules/Form.tsx'
import { Stack } from '@mui/system'
import { SimpleButton } from '#/components/atoms/SimpleButton.tsx'

type OrderFormProps = {
  order: Order
  onPriceChange: (price: number) => void
  onSubmit: () => void
  isSubmitting?: boolean
}

export function OrderForm({
  order,
  onPriceChange,
  onSubmit,
  isSubmitting = false,
}: OrderFormProps) {
  return (
    <Stack spacing={2} className="order-form">
      <Form order={order} onPriceChange={onPriceChange} />
      <SimpleButton variant="contained" onClick={onSubmit} disabled={isSubmitting} fullWidth>
        {isSubmitting ? 'Submitting...' : 'Submit'}
      </SimpleButton>
    </Stack>
  )
}
