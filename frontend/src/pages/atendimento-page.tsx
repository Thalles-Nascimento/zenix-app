import { useAtendimentos } from "../hooks/use-atendimentos"
import { Spinner } from "../components/ui/spinner"
import { Badge } from "../components/ui/badge"
import { useState } from "react"
import type { AtendimentoProps } from "../types/atendimento"
import { Toaster } from "sonner"
import { ModalEditarAtendimento } from "./modals/atendimento-editar-modal"
import { usePaginacao } from "../hooks/use-pagination"
import { Paginacao } from "../components/pagination"
import { useAuth } from "../contexts/auth-context"
import { MessageSquareWarning } from "lucide-react"
import { ModalNovoAtendimento } from "./modals/atendimento-novo-modal"

export default function Atendimentos() {
    const { dados, carregando, periodo, setPeriodo, criarAtendimento, atualizarAtendimentoAdmin, deletarAtendimento, ativarAtendimento } = useAtendimentos()
    const [atendimentoSelecionado, setAtendimentoSelecionado] = useState<AtendimentoProps | null>(null)
    const { itensPagina, paginaAtual, totalPaginas, totalItens, setPaginaAtual } = usePaginacao(dados, 7)
    const { permissao } = useAuth()

    if (carregando) {
        return (
            <div className="w-full bg-black flex items-center justify-center py-20">
                <Badge variant="secondary"><Spinner />Carregando...</Badge>
            </div>
        )
    }

    return (
        <>
            <Toaster richColors position="top-center" />

            {/* Central container para alinhar com outras telas */}
            <div className="max-w-[1100px] mx-auto px-4 sm:px-6">

                {/* Topbar */}
                <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6 notranslate">
                    <div className="flex items-center gap-4">
                        <h1 className="text-white text-2xl font-bold">Atendimentos</h1>
                    </div>

                    <div className="flex flex-wrap items-center gap-3">
                        <ModalNovoAtendimento onConfirmar={criarAtendimento} />
                        <div className="hidden sm:flex items-center gap-2 bg-gray-900 rounded-full p-1">
                            <button
                                onClick={() => setPeriodo("hoje")}
                                className={`px-3 py-1 rounded-full text-sm font-medium transition-colors ${periodo === "hoje" ? "bg-orange-700 text-white" : "text-gray-300 hover:text-white"}`}
                            >
                                Hoje
                            </button>
                            <button
                                onClick={() => setPeriodo("historico")}
                                className={`px-3 py-1 rounded-full text-sm font-medium transition-colors ${periodo === "historico" ? "bg-yellow-500 text-white" : "text-gray-300 hover:text-white"}`}
                            >
                                Histórico
                            </button>
                        </div>

                        <div className="flex items-center gap-2">
                            <div className="sm:hidden flex items-center gap-2">
                                <button
                                    onClick={() => setPeriodo("hoje")}
                                    className={`px-3 py-1 rounded-lg text-sm font-medium transition-colors ${periodo === "hoje" ? "bg-orange-700 text-white" : "bg-gray-800 text-gray-400 hover:text-white"}`}
                                >
                                    Hoje
                                </button>
                                <button
                                    onClick={() => setPeriodo("historico")}
                                    className={`px-3 py-1 rounded-lg text-sm font-medium transition-colors ${periodo === "historico" ? "bg-yellow-500 text-white" : "bg-gray-800 text-gray-400 hover:text-white"}`}
                                >
                                    Histórico
                                </button>
                            </div>

                        </div>
                    </div>
                </div>

                {/* Lista responsiva: cards em mobile, tabela em desktop */}
                <div className="overflow-x-auto rounded-xl border border-gray-700 notranslate bg-black p-2">

                    {/* Tabela para desktop */}
                    <table className="w-full text-sm text-left text-gray-300 min-w-150 table-fixed hidden md:table">
                        <thead className="text-xs uppercase bg-gray-850 border-b border-gray-700">
                            <tr className="text-gray-300">
                                <th scope="col" className="px-4 py-3">CLIENTE</th>
                                <th scope="col" className="px-4 py-3">SERVIÇO</th>
                                <th scope="col" className="px-4 py-3">VALOR</th>
                                <th scope="col" className="px-4 py-3">PAGAMENTO</th>
                                <th scope="col" className="px-4 py-3">DATA</th>
                                <th scope="col" className="px-4 py-3">STATUS</th>
                            </tr>
                        </thead>
                        <tbody>
                            {dados.length === 0 ? (
                                <tr>
                                    <td colSpan={6} className="px-6 py-12 text-center text-gray-500 align-middle">
                                        Nenhum atendimento encontrado.
                                    </td>
                                </tr>
                            ) : (
                                itensPagina.map(items => (
                                    <tr
                                        key={items.id}
                                        className={`bg-black border-b border-gray-800 hover:bg-gray-900 transition-colors ${permissao === "ADMIN" ? "cursor-pointer" : "cursor-default"}`}
                                        onClick={() => { if (permissao === "ADMIN") setAtendimentoSelecionado(items) }}
                                    >
                                        <td className="px-4 py-4 font-medium text-white">
                                            <div className="flex flex-col">
                                                <span className="text-sm font-semibold text-white truncate max-w-[220px]">{items.descricao}</span>
                                                {items.observacao && (
                                                    <span className="text-xs text-orange-300 flex items-center gap-1" title={items.observacao}><MessageSquareWarning size={14} /></span>
                                                )}
                                            </div>
                                        </td>
                                        <td className="px-4 py-4 text-gray-300">{Array.isArray(items.servico) ? items.servico.join(" + ") : items.servico}</td>
                                        <td className="px-4 py-4 text-orange-500 font-bold">{items.valor.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })}</td>
                                        <td className="px-4 py-4 text-gray-300">{items.formaPagamento}</td>
                                        <td className="px-4 py-4 text-gray-300">{items.data}</td>
                                        <td className="px-4 py-4">
                                            {items.status === 1 ? (
                                                <span className="inline-flex items-center px-2 py-1 rounded-full text-xs bg-orange-700 text-white font-semibold">Finalizado</span>
                                            ) : (
                                                <span className="inline-flex items-center px-2 py-1 rounded-full text-xs bg-gray-800 text-gray-300">Excluído</span>
                                            )}
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>

                    {/* Cards para mobile */}
                    <div className="md:hidden">
                        {dados.length === 0 ? (
                            <div className="px-6 py-12 text-center text-gray-500">Nenhum atendimento encontrado.</div>
                        ) : (
                            itensPagina.map(items => (
                                <div
                                    key={items.id}
                                    className={`bg-gray-900 rounded-lg p-4 mb-3 ${permissao === "ADMIN" ? "cursor-pointer" : ""}`}
                                    onClick={() => { if (permissao === "ADMIN") setAtendimentoSelecionado(items) }}
                                >
                                    <div className="flex justify-between items-start">
                                        <div className="flex-1 min-w-0">
                                            <div className="flex items-center justify-between gap-3">
                                                <div className="flex flex-col">
                                                    <span className="text-white font-semibold truncate max-w-[220px]">{items.descricao}</span>
                                                    <span className="text-sm text-gray-300 truncate">{Array.isArray(items.servico) ? items.servico.join(" + ") : items.servico}</span>
                                                </div>
                                                <div className="flex flex-col items-end">
                                                    <span className="text-orange-500 font-bold">{items.valor.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })}</span>
                                                    <span className="text-xs text-gray-400">{items.data}</span>
                                                </div>
                                            </div>
                                            {items.observacao && (
                                                <div className="text-xs text-orange-500 flex items-center gap-1 mt-2"><MessageSquareWarning size={14}    /> {items.observacao}</div>
                                            )}
                                        </div>
                                    </div>

                                    <div className="mt-3 flex items-center justify-between">
                                        <div>
                                            <span className={items.status === 1 ? 'inline-flex items-center px-2 py-1 rounded-full text-xs bg-orange-700 text-white' : 'inline-flex items-center px-2 py-1 rounded-full text-xs bg-gray-800 text-gray-300'}>
                                                {items.status === 1 ? 'Finalizado' : 'Excluído'}
                                            </span>
                                        </div>
                                        <div className="flex items-center gap-2">
                                            <span className="text-sm text-gray-300">{items.formaPagamento}</span>
                                        </div>
                                    </div>
                                </div>
                            ))
                        )}
                    </div>

                </div>

                <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 p-4">
                    <div className="text-sm text-gray-400">Mostrando <span className="text-white font-semibold">{itensPagina.length}</span> de <span className="text-white font-semibold">{totalItens}</span> atendimentos</div>
                    <Paginacao
                        paginaAtual={paginaAtual}
                        totalPaginas={totalPaginas}
                        totalItens={totalItens}
                        itensPorPagina={7}
                        onPaginaChange={setPaginaAtual}
                    />
                </div>

            </div>

            <ModalEditarAtendimento
                atendimento={atendimentoSelecionado}
                open={atendimentoSelecionado !== null}
                onFechar={() => setAtendimentoSelecionado(null)}
                onConfirmar={atualizarAtendimentoAdmin}
                onDeletar={deletarAtendimento}
                onReativar={ativarAtendimento}
            />
        </>
    )
}
