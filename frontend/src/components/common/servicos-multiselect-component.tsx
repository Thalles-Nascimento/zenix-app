import { useState, useRef, useEffect } from "react"
import { ChevronDown, Check } from "lucide-react"
import { SERVICOS } from "../../utils/servicos"

interface Props {
    selecionados: string[]
    onChange: (servicos: string[]) => void
}

export function ServicosMultiSelect({ selecionados, onChange }: Props) {
    const [aberto, setAberto] = useState(false)
    const ref = useRef<HTMLDivElement>(null)

    // Fecha ao clicar fora
    useEffect(() => {
        const handler = (e: MouseEvent) => {
            if (ref.current && !ref.current.contains(e.target as Node)) {
                setAberto(false)
            }
        }
        document.addEventListener("mousedown", handler)
        return () => document.removeEventListener("mousedown", handler)
    }, [])

    const toggle = (servico: string) => {
        if (selecionados.includes(servico)) {
            onChange(selecionados.filter(s => s !== servico))
        } else {
            onChange([...selecionados, servico])
        }
    }

    const label = selecionados.length === 0
        ? "Selecione os serviços"
        : selecionados.join(", ")

    return (
        <div ref={ref} className="relative mt-1">
            {/* Trigger */}
            <button
                type="button"
                onClick={() => setAberto(prev => !prev)}
                className="w-full flex items-center justify-between border border-gray-300 text-sm rounded-md px-3 py-2 text-left focus:outline-none focus:ring-1 focus:ring-gray-500"
            >
                <span className={`truncate ${selecionados.length === 0 ? "text-zinc-400" : "text-white"}`}>
                    {label}
                </span>
                <ChevronDown className={`ml-2 h-4 w-4 text-gray-400 shrink-0 transition-transform ${aberto ? "rotate-180" : ""}`} />
            </button>

            {/* Dropdown */}
            {aberto && (
                <div className="bg-black absolute z-50 mt-1 w-full border border-gray-300 rounded-md shadow-lg max-h-60 overflow-y-auto">
                    {SERVICOS.map(s => {
                        const marcado = selecionados.includes(s)
                        return (
                            <button
                                key={s}
                                type="button"
                                onClick={() => toggle(s)}
                                className="w-full flex items-center gap-3 px-3 py-2.5 text-sm text-left hover:bg-primary transition-colors"
                            >
                                {/* Checkbox */}
                                <div className={`w-4 h-4 shrink-0 rounded border flex items-center justify-center transition-colors
                                    ${marcado ? "bg-orange-600 border-orange-600" : "border-gray-500 bg-transparent"}`}>
                                    {marcado && <Check className="w-3 h-3 text-white" strokeWidth={3} />}
                                </div>
                                <span className={marcado ? "text-white" : "text-gray-300"}>{s}</span>
                            </button>
                        )
                    })}
                </div>
            )}
        </div>
    )
}
