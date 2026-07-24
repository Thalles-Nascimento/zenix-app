import { Field, FieldDescription, FieldLabel } from "@/components/ui/field"
import { Textarea } from "@/components/ui/textarea"

interface Props {
    value: string
    onChange: (e: React.ChangeEvent<HTMLTextAreaElement>) => void
}

export function TextareaField({ value, onChange }: Props) {
    return (
        <Field>
            <FieldLabel htmlFor="textarea-message" className="text-white font-bold text-md">Observação</FieldLabel>
            <Textarea
                id="textarea-message"
                placeholder="Insira sua anotação aqui"
                className="bg-gray-900"
                value={value}
                onChange={onChange}
            />
        </Field>
    )
}