import { SimpleCard } from '#/components/atoms/SimpleCard.tsx'
import type { Order } from '#/types'
import { Stack } from '@mui/system'
import { SimpleTextField } from '#/components/atoms/SimpleTextField.tsx'

type FormProps = {
  order: Order
  onPriceChange: (price: number) => void
}

export function Form({ order, onPriceChange }: FormProps) {
  return (
    <SimpleCard>
      <Stack spacing={2}>
        <SimpleTextField
          label="Customer Id"
          value={order.customerId}
          disabled
          fullWidth
        />
        <SimpleTextField
          label="Price"
          type="number"
          value={order.price}
          onChange={(e) => onPriceChange(Number(e.target.value))}
          fullWidth
        />
      </Stack>
    </SimpleCard>
  )
}
