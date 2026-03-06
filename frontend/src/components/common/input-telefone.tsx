import { useState } from "react"
import { formatarTelefone, limparTelefone } from "../../utils/formatter"

const CODIGOS_PAIS = [
    { codigo: "+55", pais: "BR" },
    { codigo: "+1",  pais: "US" },
]

interface Props {
    value: string
    onChange: (telefoneCompleto: string) => void
}

export function InputTelefone({ onChange }: Props) {
    const [codigo, setCodigo] = useState("+55")
    const [telefoneFormatado, setTelefoneFormatado] = useState("")

    const handleTelefone = (e: React.ChangeEvent<HTMLInputElement>) => {
        const formatado = formatarTelefone(e.target.value)
        setTelefoneFormatado(formatado)
        // Envia código + número limpo para o form
        onChange(`${codigo}${limparTelefone(formatado)}`)
    }

    const handleCodigo = (novoCodigo: string) => {
        setCodigo(novoCodigo)
        // Atualiza o valor completo com o novo código
        onChange(`${novoCodigo}${limparTelefone(telefoneFormatado)}`)
    }

    return (
            <div className="flex gap-2 mt-1">
                <select
                    value={codigo}
                    onChange={(e) => handleCodigo(e.target.value)}
                    className="bg-gray-800 border border-gray-700 text-white text-sm rounded-lg px-2 py-2 outline-none cursor-pointer">
                    
                    {CODIGOS_PAIS.map((p: any) => (
                        <option key={p.codigo} value={p.codigo}>
                            {p.pais} {p.codigo}
                        </option>
                    ))}
                </select>
                
                <input
                    type="tel"
                    value={telefoneFormatado}
                    onChange={handleTelefone}
                    placeholder="(11) 99999-9999"
                    maxLength={15}
                    className="flex-1 bg-gray-800 border border-gray-700 text-white text-sm rounded-lg px-3 py-2 outline-none"
                />
            </div>
    )
}