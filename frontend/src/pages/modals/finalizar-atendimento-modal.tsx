import { Dialog, DialogContent, DialogHeader, DialogTitle } from "../../components/ui/dialog"
import { Botao } from "../../components/common/botao"
import type { FilaProps } from "../../types/fila"
import { SERVICOS } from "../../utils/servicos"

interface Props {
    cliente: FilaProps | null
    open: boolean
    onFechar: () => void
    onFinalizar: (id: number, valor: number) => void
}

export function ModalFinalizarAtendimento({ cliente, open, onFechar, onFinalizar }: Props) {

    const calcularTotal = (servicos: string | string[]): string => {
        const lista = Array.isArray(servicos) ? servicos : [servicos]
        const total = lista.reduce((acc, nome) => {
            const servico = SERVICOS.find(s => s.nome === nome)
            if (!servico) return acc
            const valor = parseFloat(servico.valor.replace("R$", "").replace(",", ".").trim())
            return acc + valor
        }, 0)
        return total.toFixed(2).replace(".", ",")
    }

    const handleFinalizar = () => {
        if (!cliente) return
        const valor = parseFloat(calcularTotal(cliente.servico))
        onFinalizar(cliente.id, valor)
    }

    return (
        <Dialog open={open} onOpenChange={onFechar}>
            <DialogContent className="bg-gray-900 border-gray-700 text-white w-[calc(100vw-2rem)] max-w-md max-h-[90vh] overflow-y-auto">
                <DialogHeader>
                    <DialogTitle className="text-white">Finalizar Atendimento</DialogTitle>
                </DialogHeader>
                <div className="flex flex-col gap-4 mt-2">
                    <div className="bg-gray-800 rounded-lg p-4 flex flex-col gap-2">
                        <p className="text-gray-400 text-xs uppercase tracking-widest">Resumo</p>
                        <p className="text-white font-medium">{cliente?.nomeCliente}</p>
                        <div className="flex flex-wrap gap-1">
                            {Array.isArray(cliente?.servico)
                                ? cliente.servico.map(s => (
                                    <span key={s} className="bg-gray-700 text-gray-200 text-xs px-2 py-0.5 rounded-full">{s}</span>
                                ))
                                : <span className="text-gray-300 text-sm">{cliente?.servico}</span>
                            }
                        </div>
                        <p className="text-gray-300 text-sm">{cliente?.formaPagamento}</p>
                        <p className="text-orange-400 font-semibold text-sm mt-1">
                            Total: R$ {cliente ? calcularTotal(cliente.servico) : "0,00"}
                        </p>
                    </div>
                    <div className="flex flex-col gap-2 mt-2">
                        <Botao texto="Finalizar Atendimento" color="sucess" click={handleFinalizar} />
                        <Botao texto="Cancelar" color="cancel" click={onFechar} />
                    </div>
                </div>
            </DialogContent>
        </Dialog>
    )
}