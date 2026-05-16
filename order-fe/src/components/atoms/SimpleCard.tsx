import Card from '@mui/material/Card'
import CardContent from '@mui/material/CardContent'
import type { ReactNode } from 'react'

type CardProps = {
  children: ReactNode
}

export function SimpleCard({ children }: CardProps) {
  return (
    <Card>
      <CardContent>{children}</CardContent>
    </Card>
  )
}
