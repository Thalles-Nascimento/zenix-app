import { useState } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "../../components/ui/dialog"
import { Input } from "../../components/ui/input"
import { Label } from "../../components/ui/label"
import { Botao } from "../../components/common/botao"
import type { FilaProps } from "../../types/fila"
// TODO - Refazer resumo
interface Props {
    cliente: FilaProps | null
    open: boolean
    onFechar: () => void
    onFinalizar: (id: number, valor: string) => void
}

export function ModalFinalizarAtendimento({ cliente, open, onFechar, onFinalizar }: Props) {
    const [valor, setValor] = useState("")

    const handleFinalizar = () => {
        if (!cliente || !valor) return
        onFinalizar(cliente.id, valor)
        setValor("")
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
                    </div>
                    <div>
                        <Label className="text-gray-300">Valor cobrado</Label>
                        <Input
                            className="mt-1 bg-gray-800 border-gray-700 text-white"
                            placeholder="R$ 0,00"
                            type="number"
                            value={valor}
                            onChange={(e) => setValor(e.target.value)}
                        />
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
