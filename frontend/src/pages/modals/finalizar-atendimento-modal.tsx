import { useEffect, useMemo, useState } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "../../components/ui/dialog"
import { Botao } from "../../components/common/botao"
import type { FilaProps } from "../../types/fila"
import { TextareaField } from "@/components/common/textarea"
import { useServicos } from "../../hooks/use-servicos"
import type { ClienteDTO } from "@/types/cliente"
import { buscarClientesPorNomeService } from "@/services/cliente-service"
import { useCliente } from "@/hooks/use-cliente"

interface Props {
    fila: FilaProps | null
    open: boolean
    onFechar: () => void
    onFinalizar: (id: number, valor: number, observacao: string) => void
}

export function ModalFinalizarAtendimento({ fila, open, onFechar, onFinalizar }: Props) {
    const [observacao, setObservacao] = useState("")
    const { servicos } = useServicos()
    const [clienteDTO, setClienteDTO] = useState<ClienteDTO | null>(null)
    const { atualizarRetorno } = useCliente()

    useEffect(() => {
        if (!fila?.nomeCliente) return
        buscarClientesPorNomeService(fila.nomeCliente).then(resultado => {
            const encontrado = resultado.find(c => c.nomeCliente === fila.nomeCliente)
            setClienteDTO(encontrado ?? null) 
        })
    }, [fila?.nomeCliente])

    // Calcula o valor baseado no plano ou nos serviços selecionados
    const valorCalculado = useMemo(() => {
        if (!fila) return 0

        // Se tem plano ativo, divide o valor do plano pela quantidade de atendimentos
        if (clienteDTO?.plano) {
            const { valor, limiteAtendimentos } = clienteDTO.plano
            return limiteAtendimentos > 0 ? valor / limiteAtendimentos : 0
        }

        const nomes = Array.isArray(fila.servico) ? fila.servico : [fila.servico]
        return nomes.reduce((acc, nome) => {
            const servico = servicos.find(s => s.servico === nome)
            return servico ? acc + servico.valor : acc
        }, 0)
    }, [fila, clienteDTO, servicos])

    const formatBRL = (valor: number) =>
        valor.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })

    const handleFinalizar = () => {
        if (!fila) return
        onFinalizar(fila.id, valorCalculado, observacao)
        if (clienteDTO?.vezesRetorno === 0) atualizarRetorno(clienteDTO?.id ?? 0)
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
                        <p className="text-white text-sm font-medium">
                            Cliente: <span className="font-bold">{fila?.nomeCliente}</span>
                        </p>

                        {/* Badge do plano se existir */}
                        {clienteDTO?.plano && (
                            <span className="self-start bg-orange-500/20 text-orange-400 text-xs font-semibold px-2 py-0.5 rounded-full border border-orange-500/30">
                                {clienteDTO.plano.planoDescricao}
                            </span>
                        )}

                        <div className="flex flex-wrap gap-1">
                            {Array.isArray(fila?.servico)
                                ? fila.servico.map(s => (
                                    <span key={s} className="bg-primary text-black text-sm px-2 py-0.5 rounded-xl">{s}</span>
                                ))
                                : <span className="text-gray-300 text-sm">{fila?.servico}</span>
                            }
                        </div>

                        <p className="text-gray-300 text-sm">
                            Forma de Pagamento: <span className="font-bold">{fila?.formaPagamento}</span>
                        </p>

                        <p className="text-orange-400 font-semibold mt-1">
                            Total: {formatBRL(valorCalculado)}
                            {clienteDTO?.plano && (
                                <span className="ml-2 text-gray-500 text-xs font-normal">
                                    (plano: {formatBRL(clienteDTO.plano.valor)} ÷ {clienteDTO.plano.limiteAtendimentos} atend.)
                                </span>
                            )}
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