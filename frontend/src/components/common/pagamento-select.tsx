import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "../ui/select"
import { usePagamentos } from "../../hooks/use-pagamentos"

interface Props {
    value: string
    onValueChange: (value: string) => void
    className?: string
}

export function PagamentoSelect({ value, onValueChange, className }: Props) {
    const { pagamentos, carregando } = usePagamentos()

    return (
        <Select value={value} onValueChange={onValueChange}>
            <SelectTrigger className={`mt-1 bg-gray-900 border-gray-700 text-white w-full notranslate ${className ?? ""}`}>
                <SelectValue placeholder={carregando ? "Carregando..." : "Selecione"} />
            </SelectTrigger>
            <SelectContent className="bg-black border-gray-700 text-white notranslate">
                {pagamentos.map(p => (
                    <SelectItem key={p.id} value={p.formaPagamento} className="notranslate">
                        {p.formaPagamento}
                    </SelectItem>
                ))}
            </SelectContent>
        </Select>
    )
}
