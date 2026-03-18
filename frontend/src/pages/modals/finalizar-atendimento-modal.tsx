import { Dialog, DialogContent, DialogHeader, DialogTitle } from "../../components/ui/dialog"
import { Botao } from "../../components/common/botao"
import type { FilaProps } from "../../types/fila"
import { TextareaField } from "@/components/common/textarea"
import { useState } from "react"
import { useServicos } from "../../hooks/use-servicos"

interface Props {
    cliente: FilaProps | null
    open: boolean
    onFechar: () => void
    onFinalizar: (id: number, valor: number, observacao: string) => void
}

export function ModalFinalizarAtendimento({ cliente, open, onFechar, onFinalizar }: Props) {
    const [observacao, setObservacao] = useState("")
    const { servicos } = useServicos()

    const calcularTotal = (lista: string | string[]): string => {
        const nomes = Array.isArray(lista) ? lista : [lista]
        const total = nomes.reduce((acc, nome) => {
            const servico = servicos.find(s => s.servico === nome)
            return servico ? acc + servico.valor : acc
        }, 0)
        return total.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })
    }

    const handleFinalizar = () => {
        if (!cliente) return
        const nomes = Array.isArray(cliente.servico) ? cliente.servico : [cliente.servico]
        const total = nomes.reduce((acc, nome) => {
            const servico = servicos.find(s => s.servico === nome)
            return servico ? acc + servico.valor : acc
        }, 0)
        onFinalizar(cliente.id, total, observacao)
        setObservacao("")
    }

    return (
        <Dialog open={open} onOpenChange={onFechar}>
            <DialogContent className="bg-black border-gray-500 text-white w-[calc(100vw-2rem)] max-w-md max-h-[90vh] overflow-y-auto">
                <DialogHeader>
                    <DialogTitle className="text-white">Finalizar Atendimento</DialogTitle>
                </DialogHeader>
                <div className="flex flex-col gap-4 mt-2">
                    <div className="bg-gray-900 rounded-lg border border-gray-500 p-4 flex flex-col gap-2">
                        <p className="text-white text-sm font-medium">Cliente: <span className="font-bold">{cliente?.nomeCliente}</span></p>
                        <div className="flex flex-wrap gap-1">
                            {Array.isArray(cliente?.servico)
                                ? cliente.servico.map(s => (
                                    <span key={s} className="bg-primary text-black text-sm px-2 py-0.5 rounded-xl">{s}</span>
                                ))
                                : <span className="text-gray-300 text-sm">{cliente?.servico}</span>
                            }
                        </div>
                        <p className="text-gray-300 text-sm">Forma de Pagamento: <span className="font-bold">{cliente?.formaPagamento}</span></p>
                        <p className="text-orange-400 font-semibold mt-1">
                            Total: {cliente ? calcularTotal(cliente.servico) : "R$ 0,00"}
                        </p>
                    </div>

                    <TextareaField
                        value={observacao}
                        onChange={(e) => setObservacao(e.target.value)}
                    />
                    <div className="flex flex-col gap-2 mt-2">
                        <Botao texto="Finalizar Atendimento" color="sucess" click={handleFinalizar} />
                        <Botao texto="Cancelar" color="cancel" click={onFechar} />
                    </div>
                </div>
            </DialogContent>
        </Dialog>
    )
}