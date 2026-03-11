import { useAtendimentos } from "../hooks/use-atendimentos"
import { Spinner } from "../components/ui/spinner"
import { Badge } from "../components/ui/badge"
import { useState } from "react"
import type { DadosProps } from "../types/atendimento"
import { Toaster } from "sonner"
import { ModalNovoAtendimento } from "./modals/atendimento-novo-modal"
import { ModalEditarAtendimento } from "./modals/atendimento-editar-modal"
import { usePaginacao } from "../hooks/use-pagination"
import { Paginacao } from "../components/pagination"
import { useAuth } from "../contexts/auth-context"

export default function Atendimentos() {
    const { dados, carregando, periodo, setPeriodo, criarAtendimento, atualizarAtendimento, deletarAtendimento } = useAtendimentos()
    const [atendimentoSelecionado, setAtendimentoSelecionado] = useState<DadosProps | null>(null)
    const { itensPagina, paginaAtual, totalPaginas, totalItens, setPaginaAtual } = usePaginacao(dados, 7)
    const { permissao } = useAuth()

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

            {/* Topbar */}
            <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-4 notranslate">
                <h1 className="text-white text-xl font-bold">Atendimentos</h1>
                <div className="flex flex-wrap items-center gap-2">
                    <button
                        onClick={() => setPeriodo("hoje")}
                        className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors
                            ${periodo === "hoje" ? "bg-orange-700 text-white" : "bg-gray-800 text-gray-400 hover:text-white"}`}
                    >
                        Hoje
                    </button>
                    <button
                        onClick={() => setPeriodo("historico")}
                        className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors
                            ${periodo === "historico" ? "bg-red-600 text-white" : "bg-gray-800 text-gray-400 hover:text-white"}`}
                    >
                        Histórico
                    </button>
                    <ModalNovoAtendimento onConfirmar={criarAtendimento} />
                </div>
            </div>

            {/* Tabela */}
            <div className="overflow-x-auto rounded-xl border border-gray-700 notranslate">
                <table className="w-full text-sm text-left text-gray-300 min-w-[600px]">
                    <thead className="text-xs text-gray-400 uppercase bg-gray-800 border-b border-gray-700">
                        <tr>
                            <th className="px-4 py-3 notranslate">CLIENTE</th>
                            <th className="px-4 py-3 notranslate">SERVIÇO</th>
                            <th className="px-4 py-3 notranslate">VALOR</th>
                            <th className="px-4 py-3 notranslate">PAGAMENTO</th>
                            <th className="px-4 py-3 notranslate">DATA</th>
                            <th className="px-4 py-3 notranslate">STATUS</th>
                        </tr>
                    </thead>
                    <tbody>
                        {itensPagina.map(items => (
                            <tr
                                key={items.id}
                                className={`bg-gray-900 border-b border-gray-700 hover:bg-gray-800 
                                    ${permissao === "ADMIN" && items.status !== -1 ? "cursor-pointer" : "cursor-default"}`}
                                onClick={() => {
                                    if(items.status !== -1 && permissao === "ADMIN"){
                                        setAtendimentoSelecionado(items)
                                    }
                                }}
                            >
                                <td className="px-4 py-4 font-medium text-white">{items.descricao}</td>
                                <td className="px-4 py-4">{Array.isArray(items.servico) ? items.servico.join(" + ") : items.servico}</td>
                                <td className="px-4 py-4 text-orange-500 font-bold">
                                    {items.valor.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })}
                                </td>
                                <td className="px-4 py-4">{items.formaPagamento}</td>
                                <td className="px-4 py-4">{items.date}</td>
                                <td className="px-4 py-4">
                                    <span className={items.status === 1 ? "text-orange-500 font-semibold" : "text-gray-500"}>
                                        {items.status === 1 ? "REALIZADO" : "EXCLUÍDO"}
                                    </span>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
                <Paginacao
                    paginaAtual={paginaAtual}
                    totalPaginas={totalPaginas}
                    totalItens={totalItens}
                    itensPorPagina={7}
                    onPaginaChange={setPaginaAtual}
                />
            </div>

            <ModalEditarAtendimento
                atendimento={atendimentoSelecionado}
                open={atendimentoSelecionado !== null}
                onFechar={() => setAtendimentoSelecionado(null)}
                onConfirmar={atualizarAtendimento}
                onDeletar={deletarAtendimento}
            />
        </>
    )
}
