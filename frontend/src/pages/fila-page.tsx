import { Toaster } from "sonner"
import { useFila } from "../hooks/use-fila"
import { ModalFinalizarAtendimento } from "./modals/finalizar-atendimento-modal"
import { criarAtendimentoService } from "../services/atendimento-service"
import { Badge } from "../components/ui/badge"
import { Spinner } from "../components/ui/spinner"
import { useState } from "react"
import type { FilaProps } from "@/types/fila"
import { ModalConfirmacao } from "@/components/common/modal-confirmacao-component"

export default function FilaPage() {
    const { fila, carregando, clienteSelecionado, setClienteSelecionado, chamarProximo, finalizarAtendimento, retirarClienteFila } = useFila()
    const [confirmacaoAberta, setConfirmacaoAberta] = useState(false)
    const [filaSelecionada, setFilaSelecionada] = useState<FilaProps | null>(null)
    const aguardando = fila.filter(c => c.status === "AGUARDANDO")
    const emAtendimento = fila.filter(c => c.status === "EM_ATENDIMENTO")

    const formatarHorario = (horario: string) => {
        if (!horario) return ""
        const [hora, minuto, segundo] = horario.split(":")
        const seg = segundo?.split(".")[0] ?? "00"
        const ms = segundo?.split(".")[1]?.slice(0, 3) ?? "000"
        return `${hora}:${minuto}:${seg}.${ms}`
    }

    const abrirRetirarFila = (fila: FilaProps) => {
        setFilaSelecionada(fila)
        setConfirmacaoAberta(true)
    }

    const handleFinalizar = async (id: number, valor: number, observacao: string) => {
        const cliente = fila.find(c => c.id === id)
        if (!cliente) return
        await criarAtendimentoService({
            descricao: cliente.nomeCliente,
            servico: cliente.servico,
            valor: valor,
            formaPagamento: cliente.formaPagamento,
            observacao: observacao
        })
        await finalizarAtendimento(id)
    }

    if (carregando) {
        return (
            <div className="w-full flex items-center justify-center py-20">
                <Badge variant="secondary"><Spinner />Carregando...</Badge>
            </div>
        )
    }

    return (
        <>
            <Toaster richColors position="top-center" />
            <div className="flex flex-col gap-6 notranslate">
                <h1 className="text-white text-xl font-bold">Fila de atendimentos</h1>

                {/* Cards resumo */}
                <div className="grid grid-cols-2 gap-4 notranslate">
                    <div className="bg-black rounded-xl border border-orange-700 p-4">
                        <p className="text-white text-xs uppercase tracking-widest mb-1">Aguardando</p>
                        <p className="text-orange-500 text-2xl font-bold">{aguardando.length}</p>
                    </div>
                    <div className="bg-black rounded-xl border border-green-700 p-4 notranslate">
                        <p className="text-white text-xs uppercase tracking-widest mb-1">Em Atendimento</p>
                        <p className="text-green-500 text-2xl font-bold">{emAtendimento.length}</p>
                    </div>
                </div>

                {/* Lista */}
                {fila.length === 0 ? (
                    <div className="bg-black rounded-xl border border-gray-500 p-10 text-center notranslate">
                        <p className="text-gray-500">Nenhum cliente na fila no momento</p>
                    </div>
                ) : (
                    <div className="flex flex-col gap-3 notranslate">
                        {fila.map((cliente, index) => (
                            <div
                                key={cliente.id}
                                className="notranslate bg-black rounded-xl border border-gray-500 p-4 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3"
                            >
                                {/* Info do cliente */}
                                <div className="flex items-center gap-3 notranslate">
                                    <div className="notranslate w-8 h-8 shrink-0 rounded-full bg-orange-600 flex items-center justify-center font-bold text-sm">
                                        {index + 1}
                                    </div>
                                    <div>
                                        <p className="text-white font-bold">{cliente.nomeCliente}</p>
                                        <p className="text-white text-sm">{Array.isArray(cliente.servico) ? cliente.servico.join(" + ") : cliente.servico} • {cliente.formaPagamento}</p>
                                        <p className="text-gray-300 text-xs">Entrou às {formatarHorario(cliente.horario)}</p>
                                        {cliente.semPreferencia === true && <p className="text-gray-300 text-xs">Cliente sem preferência</p>}
                                    </div>
                                </div>

                                {/* Ações */}
                                <div className="flex items-center gap-2 flex-wrap notranslate">
                                    <span className={`text-xs font-semibold px-2 py-1 rounded-full
                                        ${cliente.status === "AGUARDANDO"
                                            ? "bg-gray-800 text-gray-300"
                                            : "bg-orange-900 text-orange-400"
                                        }`}>
                                        {cliente.status === "AGUARDANDO" ? "Aguardando" : "Em Atendimento"}
                                    </span>

                                    {cliente.status === "AGUARDANDO" && (
                                    <>
                                        <button
                                            onClick={() => chamarProximo(cliente.id)}
                                            disabled={emAtendimento.length > 0}
                                            className="bg-orange-600 hover:bg-orange-500 text-white text-xs font-bold px-3 py-1.5 rounded-lg transition-colors disabled:bg-gray-600"
                                        >
                                            Chamar
                                        </button>
                                        <button
                                            onClick={() => abrirRetirarFila(cliente)}
                                            className="bg-gray-700 hover:bg-gray-600 text-white text-xs font-bold px-3 py-1.5 rounded-lg transition-colors"
                                        >
                                            Retirar
                                        </button>
                                    </>
                                    )}

                                    {cliente.status === "EM_ATENDIMENTO" && (
                                        <button
                                            onClick={() => setClienteSelecionado(cliente)}
                                            className="bg-green-700 hover:bg-green-600 text-white text-xs font-bold px-3 py-1.5 rounded-lg transition-colors"
                                        >
                                            Finalizar
                                        </button>
                                    )}
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>
            <ModalConfirmacao
                open={confirmacaoAberta}
                titulo="Retirar cliente da Fila"
                mensagem={`Deseja retirar o "${filaSelecionada?.nomeCliente}" da fila? Esta ação não pode ser desfeita.`}
                onConfirmar={() => {
                    if (!filaSelecionada) return
                    retirarClienteFila(filaSelecionada?.id)
                    setConfirmacaoAberta(false)                
                }}
                onCancelar={() => setConfirmacaoAberta(false)}
            />

            <ModalFinalizarAtendimento
                fila={clienteSelecionado}
                open={clienteSelecionado !== null}
                onFechar={() => setClienteSelecionado(null)}
                onFinalizar={handleFinalizar}
            />
        </>
    )
}
