import { TextField } from '@mui/material'
import type { TextFieldProps } from '@mui/material'

type SimpleTextFieldProps = TextFieldProps

export function SimpleTextField(props: SimpleTextFieldProps) {
  return <TextField {...props} />
}
