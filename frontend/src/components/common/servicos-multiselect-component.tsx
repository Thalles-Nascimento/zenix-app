import { useState, useRef, useEffect } from "react"
import { ChevronDown, Check } from "lucide-react"
import { useServicos } from "../../hooks/use-servicos"

interface Props {
    selecionados: string[]
    onChange: (servicos: string[]) => void
}

export function ServicosMultiSelect({ selecionados, onChange }: Props) {
    const [aberto, setAberto] = useState(false)
    const ref = useRef<HTMLDivElement>(null)
    const { servicos, carregando } = useServicos()

    useEffect(() => {
        const handler = (e: MouseEvent) => {
            if (ref.current && !ref.current.contains(e.target as Node)) {
                setAberto(false)
            }
        }
        document.addEventListener("mousedown", handler)
        return () => document.removeEventListener("mousedown", handler)
    }, [])

    const toggle = (nome: string) => {
        if (selecionados.includes(nome)) {
            onChange(selecionados.filter(s => s !== nome))
        } else {
            onChange([...selecionados, nome])
        }
    }

    const formatarValor = (valor: number) =>
        valor.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })

    const label = selecionados.length === 0
        ? "Selecione os serviços"
        : selecionados.length <= 2
            ? selecionados.map(nome => {
                const servico = servicos.find(s => s.servico === nome)
                return servico ? `${servico.servico} - ${formatarValor(servico.valor)}` : nome
            }).join(", ")
            : `${selecionados.length} serviços selecionados`

    return (
        <div ref={ref} className="relative mt-1 notranslate">
            <button
                type="button"
                onClick={() => setAberto(prev => !prev)}
                className="bg-gray-900 w-full flex items-center justify-between border border-gray-600 text-sm rounded-md px-3 py-2 text-left focus:outline-none focus:ring-1 focus:ring-gray-500"
            >
                <span className={`truncate ${selecionados.length === 0 ? "text-zinc-400" : "text-white"}`}>
                    {carregando ? "Carregando serviços..." : label}
                </span>
                <ChevronDown className={`ml-2 h-4 w-4 text-gray-400 shrink-0 transition-transform ${aberto ? "rotate-180" : ""}`} />
            </button>

            {aberto && (
                <div className="bg-black absolute z-50 mt-1 w-full border border-gray-300 rounded-md shadow-lg max-h-60 overflow-y-auto notranslate">
                    {carregando ? (
                        <p className="text-gray-400 text-sm px-3 py-2">Carregando...</p>
                    ) : (
                        servicos.map(({ servico: nome, valor }) => {
                            const marcado = selecionados.includes(nome)
                            return (
                                <button
                                    key={nome}
                                    type="button"
                                    onClick={() => toggle(nome)}
                                    className="w-full flex items-center gap-3 px-3 py-2.5 text-sm text-left hover:bg-primary transition-colors"
                                >
                                    <div className={`notranslate w-4 h-4 shrink-0 rounded border flex items-center justify-center transition-colors
                                        ${marcado ? "bg-orange-600 border-orange-600" : "border-gray-500 bg-transparent"}`}>
                                        {marcado && <Check className="w-3 h-3 text-white notranslate" strokeWidth={3} />}
                                    </div>
                                    <span className={marcado ? "text-white" : "text-gray-300"}>
                                        {nome}
                                    </span>
                                    <span className="ml-auto text-primary text-xs shrink-0">
                                        {formatarValor(valor)}
                                    </span>
                                </button>
                            )
                        })
                    )}
                </div>
            )}
        </div>
    )
}