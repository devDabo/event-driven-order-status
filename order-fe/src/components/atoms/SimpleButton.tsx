import type { ReactNode } from 'react'
import { Button } from '@mui/material'

type SimpleButtonProps = {
  children: ReactNode
  type?: 'button' | 'submit' | 'reset'
  variant?: 'contained' | 'outlined'
  onClick?: () => void
  disabled?: boolean
  fullWidth?: boolean
}

export function SimpleButton({
  children,
  variant,
  type,
  onClick,
  disabled,
  fullWidth = false,
}: SimpleButtonProps) {
  return (
    <Button
      variant={variant}
      type={type}
      onClick={onClick}
      disabled={disabled}
      fullWidth={fullWidth}
    >
      {children}
    </Button>
  )
}
