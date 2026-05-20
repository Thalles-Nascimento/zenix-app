import { useEffect, useState } from "react"
import { formatarTelefone, limparTelefone } from "../../utils/formatter"

interface Props {
    value: string
    onChange: (telefoneCompleto: string) => void
}

export function InputTelefone({ value, onChange }: Props) {
     const [telefoneFormatado, setTelefoneFormatado] = useState("")

    useEffect(() => {
        if (value) {
            const numero = value.replace(/^55/, "")
            setTelefoneFormatado(formatarTelefone(numero))
        } else {
            setTelefoneFormatado("")
        }
    }, [value])

    const handleTelefone = (e: React.ChangeEvent<HTMLInputElement>) => {
        const formatado = formatarTelefone(e.target.value)
        setTelefoneFormatado(formatado)
        onChange(`55${limparTelefone(formatado)}`)
    }

    return (
        <div className="flex gap-2 mt-1">
            <span className="bg-gray-900 border border-gray-300 text-white text-sm rounded-lg px-3 py-2 select-none">
                +55
            </span>
            <input
                type="tel"
                value={telefoneFormatado}
                onChange={handleTelefone}
                placeholder="(21) 99999-9999"
                maxLength={15}
                className="flex-1 bg-gray-900 border border-gray-300 text-white text-sm rounded-lg px-3 py-2 outline-none"
            />
        </div>
    )
}
