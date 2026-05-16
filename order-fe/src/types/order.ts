export type Order = {
  customerId: string
  price: number
}

export type OrderStatus =
  | 'PENDING'
  | 'PAID'
  | 'APPROVED'
  | 'CANCELLED'
  | 'CANCELLING'

export type OrderResponse = {
  id: string
  sagaId: string
  customerId: string
  price: number
  orderStatus: OrderStatus
  createdAt: string
  updatedAt: string
}
