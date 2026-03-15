import { useState, useEffect } from "react"
import { useDashboard } from "../hooks/use-dashboard"
import { useAtendimentos } from "../hooks/use-atendimentos"
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from "recharts"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "../components/ui/dialog"
import { hoje } from "../utils/date"
import { Badge } from "../components/ui/badge"
import { Spinner } from "../components/ui/spinner"
import { usePaginacao } from "../hooks/use-pagination"
import { Paginacao } from "../components/pagination"
import { ModalEditarAtendimento } from "./modals/atendimento-editar-modal"
import type { AtendimentoAdminProps } from "../types/dashboard"
import type { DadosProps } from "../types/atendimento"
import { Toaster } from "sonner"
import { MessageSquareWarning } from "lucide-react"

export default function DashboardPage() {
    const {
        carregando, totalDia, totalAtendimentos, ticketMedio,
        porBarbeiro, atendimentos,
        filtroInicio, filtroFim, setFiltroInicio, setFiltroFim, recarregar
    } = useDashboard()

    const { atualizarAtendimentoAdmin, deletarAtendimento, ativarAtendimento } = useAtendimentos()

    const [barbeiroSelecionado, setBarbeiroSelecionado] = useState<string | null>(null)
    const [atendimentoSelecionado, setAtendimentoSelecionado] = useState<DadosProps | null>(null)

    // Atendimentos do barbeiro selecionado no período atual (apenas ativos)
    const atendimentosBarbeiro = atendimentos.filter(
        a => a.barbeiro === barbeiroSelecionado && a.status === 1
    )

    const totalBarbeiro = atendimentosBarbeiro.reduce((acc, a) => acc + a.valor, 0)
    const comissaoBarbeiro = totalBarbeiro * 0.5

    // Paginação — tabela principal
    const { itensPagina, paginaAtual, totalPaginas, totalItens, setPaginaAtual } = usePaginacao(atendimentos, 4)

    // Paginação — tabela resumo por barbeiro
    const {
        itensPagina: itensPaginaBarbeiros,
        paginaAtual: paginaBarbeiros,
        totalPaginas: totalPaginasBarbeiros,
        totalItens: totalItensBarbeiros,
        setPaginaAtual: setPaginaBarbeiros
    } = usePaginacao(porBarbeiro, 4)

    // Paginação — modal do barbeiro
    const {
        itensPagina: itensPaginaModal,
        paginaAtual: paginaModal,
        totalPaginas: totalPaginasModal,
        totalItens: totalItensModal,
        setPaginaAtual: setPaginaModal
    } = usePaginacao(atendimentosBarbeiro, 4)

    // Reseta página do modal ao trocar de barbeiro
    useEffect(() => {
        setPaginaModal(1)
    }, [barbeiroSelecionado])

    const formatBRL = (valor: number) =>
        valor.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })

    const isPeriodoHoje = filtroInicio === filtroFim && filtroInicio === hoje()

    const abrirEdicao = (item: AtendimentoAdminProps) => {
        setAtendimentoSelecionado({
            id: item.id,
            descricao: item.descricao,
            servico: Array.isArray(item.servico)
                ? item.servico
                : item.servico.split(",").map(s => s.trim()),
            valor: item.valor,
            formaPagamento: item.formaPagamento,
            date: item.date,
            status: item.status,
            observacao: item.observacao
        })
    }

    if (carregando) {
        return (
            <div className="w-full flex items-center justify-center py-20">
                <Badge variant="secondary"><Spinner />Carregando...</Badge>
            </div>
        )
    }

    return (
        <div className="flex flex-col gap-6 notranslate">
            <Toaster richColors position="top-center" />
            <h1 className="text-white text-xl font-bold">Dashboard</h1>

            {/* Filtro por período */}
            <div className="flex flex-wrap items-center gap-3 bg-black rounded-xl border border-gray-500 p-4 notranslate">
                <span className="text-white text-sm font-medium w-full sm:w-auto">Período:</span>
                <div className="flex items-center gap-2 notranslate">
                    <input
                        type="date"
                        value={filtroInicio}
                        onChange={(e) => setFiltroInicio(e.target.value)}
                        className="bg-gray-800 border border-gray-700 text-white text-sm rounded-lg px-1 py-1 outline-none"
                    />
                </div>
                <div className="flex items-center gap-2 notranslate">
                    <label className="text-gray-400 text-sm">Até</label>
                    <input
                        type="date"
                        value={filtroFim}
                        onChange={(e) => setFiltroFim(e.target.value)}
                        className="bg-gray-800 border border-gray-700 text-white text-sm rounded-lg px-1 py-1 outline-none"
                    />
                </div>
                {!isPeriodoHoje && (
                    <button
                        onClick={() => { setFiltroInicio(hoje()); setFiltroFim(hoje()) }}
                        className="text-orange-500 text-sm hover:underline notranslate"
                    >
                        Voltar para hoje
                    </button>
                )}
            </div>

            {/* Cards */}
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 notranslate">
                <div className="bg-black rounded-xl border border-orange-700 p-5 notranslate">
                    <p className="text-white text-xs uppercase tracking-widest mb-1">
                        {isPeriodoHoje ? "Total do Dia" : "Total do Período"}
                    </p>
                    <p className="text-orange-500 text-2xl font-bold">{formatBRL(totalDia)}</p>
                </div>
                <div className="bg-black rounded-xl border border-orange-700 p-5 notranslate">
                    <p className="text-white text-xs uppercase tracking-widest mb-1">Atendimentos</p>
                    <p className="text-orange-500 text-2xl font-bold">{totalAtendimentos}</p>
                </div>
                <div className="bg-black rounded-xl border border-orange-700 p-5 notranslate">
                    <p className="text-white text-xs uppercase tracking-widest mb-1">Ticket Médio</p>
                    <p className="text-orange-500 text-2xl font-bold">{formatBRL(ticketMedio)}</p>
                </div>
            </div>

            {/* Gráficos */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-4 notranslate">
                <div className="bg-black rounded-xl border border-gray-700 p-5 notranslate">
                    <p className="text-white text-xs uppercase tracking-widest mb-4">Atendimentos por Barbeiro</p>
                    <ResponsiveContainer width="100%" height={220}>
                        <BarChart data={porBarbeiro}>
                            <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
                            <XAxis dataKey="barbeiro" stroke="#9ca3af" tick={{ fontSize: 11 }} />
                            <YAxis stroke="#9ca3af" tick={{ fontSize: 11 }} />
                            <Tooltip
                                contentStyle={{ backgroundColor: "#111827", border: "1px solid #374151", borderRadius: "8px" }}
                                labelStyle={{ color: "white" }}
                            />
                            <Bar dataKey="quantidade" fill="#ea580c" radius={[4, 4, 0, 0]} name="Atendimentos" />
                        </BarChart>
                    </ResponsiveContainer>
                </div>

                <div className="bg-black rounded-xl border border-gray-700 p-5 notranslate">
                    <p className="text-white text-xs uppercase tracking-widest mb-4">Ranking de Barbeiros</p>
                    {porBarbeiro.length === 0 ? (
                        <p className="text-gray-500 text-sm">Nenhum dado no período</p>
                    ) : (
                        <div className="flex flex-col gap-3">
                            {porBarbeiro.map((item, index) => (
                                <div key={item.barbeiro} className="flex items-center gap-3">
                                    <span className="text-orange-500 font-bold w-5 text-sm">{index + 1}º</span>
                                    <div className="flex-1">
                                        <div className="flex justify-between mb-1">
                                            <span className="text-white text-sm">{item.barbeiro}</span>
                                            <div className="flex gap-3">
                                                <span className="text-gray-400 text-sm">Atendimentos: {item.quantidade}</span>
                                                <span className="text-orange-400 text-sm font-semibold">{formatBRL(item.total)}</span>
                                            </div>
                                        </div>
                                        <div className="w-full bg-gray-700 rounded-full h-1.5">
                                            <div
                                                className="bg-orange-500 h-1.5 rounded-full"
                                                style={{ width: `${(item.quantidade / totalAtendimentos) * 100}%` }}
                                            />
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </div>

            {/* Tabela resumo por barbeiro */}
            <div className="bg-gray-900 rounded-xl border border-gray-700 notranslate">
                <div className="px-4 py-4 border-b border-gray-700 notranslate">
                    <p className="text-white text-xs uppercase tracking-widest">Resumo por Barbeiro</p>
                </div>
                <div className="overflow-x-auto rounded-b-xl notranslate">
                    <table className="w-full text-sm text-left text-gray-300 min-w-[500px] notranslate">
                        <thead className="text-xs text-white uppercase bg-gray-800 border-b border-gray-500 notranslate">
                            <tr>
                                <th className="px-4 py-3 notranslate">BARBEIRO</th>
                                <th className="px-4 py-3 notranslate">ATENDIMENTOS</th>
                                <th className="px-4 py-3 notranslate">TOTAL GERADO</th>
                                <th className="px-4 py-3 notranslate">COMISSÃO DO BARBEIRO (50%)</th>
                            </tr>
                        </thead>
                        <tbody>
                            {itensPaginaBarbeiros.length === 0 ? (
                                <tr>
                                    <td colSpan={4} className="px-6 py-8 text-center text-gray-500 notranslate">
                                        Nenhum barbeiro no período
                                    </td>
                                </tr>
                            ) : (
                                itensPaginaBarbeiros.map(item => (
                                    <tr
                                        key={item.barbeiro}
                                        className="bg-black border-b border-gray-700 hover:bg-gray-900 cursor-pointer transition-colors notranslate"
                                        onClick={() => setBarbeiroSelecionado(item.barbeiro)}
                                    >
                                        <td className="px-4 py-4 notranslate font-bold text-white">{item.barbeiro}</td>
                                        <td className="px-4 py-4 notranslate text-gray-300">{item.quantidade}x</td>
                                        <td className="px-4 py-4 notranslate font-bold text-orange-500">{formatBRL(item.total)}</td>
                                        <td className="px-4 py-4 notranslate font-bold text-green-400">{formatBRL(item.total * 0.5)}</td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                    <Paginacao
                        paginaAtual={paginaBarbeiros}
                        totalPaginas={totalPaginasBarbeiros}
                        totalItens={totalItensBarbeiros}
                        itensPorPagina={4}
                        onPaginaChange={setPaginaBarbeiros}
                    />
                </div>
            </div>

            {/* Tabela principal de atendimentos */}
            <div className="bg-gray-900 rounded-xl border border-gray-700 notranslate">
                <div className="px-4 py-4 border-b border-gray-700 notranslate">
                    <p className="text-white text-xs uppercase tracking-widest">
                        {isPeriodoHoje ? "Atendimentos de Hoje" : `Atendimentos de ${filtroInicio} até ${filtroFim}`}
                    </p>
                </div>
                <div className="overflow-x-auto rounded-b-xl notranslate">
                    <table className="w-full text-sm text-left text-gray-300 min-w-[500px] notranslate">
                        <thead className="text-xs text-white uppercase bg-gray-800 border-b border-gray-700 notranslate">
                            <tr>
                                <th className="px-4 py-3 notranslate">CLIENTE</th>
                                <th className="px-4 py-3 notranslate">SERVIÇO</th>
                                <th className="px-4 py-3 notranslate">BARBEIRO</th>
                                <th className="px-4 py-3 notranslate">VALOR</th>
                                <th className="px-4 py-3 notranslate">PAGAMENTO</th>
                            </tr>
                        </thead>
                        <tbody>
                            {atendimentos.length === 0 ? (
                                <tr>
                                    <td colSpan={5} className="px-6 py-8 text-center text-gray-500 notranslate">
                                        Nenhum atendimento no período
                                    </td>
                                </tr>
                            ) : (
                                itensPagina.map(items => (
                                    <tr
                                        key={items.id}
                                        className={`border-b border-gray-700 hover:bg-gray-900 cursor-pointer
                                            ${items.status === -1 ? "opacity-50" : "bg-black"}`}
                                        onClick={() => abrirEdicao(items)}
                                    >
                                        <td className="px-4 py-4 notranslate font-medium text-white">
                                            <div className="flex items-center gap-2">
                                                {items.descricao}
                                                {items.observacao && (
                                                    <MessageSquareWarning color="#bd0000" />
                                                )}
                                            </div>
                                        </td>
                                        <td className="px-4 py-4 notranslate">{Array.isArray(items.servico) ? items.servico.join(" + ") : items.servico}</td>
                                        <td className={`px-4 py-4 notranslate ${items.status === -1 ? "text-gray-500" : "text-orange-500 font-bold"}`}>
                                            {items.barbeiro}
                                        </td>
                                        <td className={`px-4 py-4 notranslate font-bold ${items.status === -1 ? "text-gray-500" : "text-white"}`}>
                                            {formatBRL(items.valor)}
                                        </td>
                                        <td className="px-4 py-4 notranslate">{items.formaPagamento}</td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                    
                </div>
                <Paginacao
                        paginaAtual={paginaAtual}
                        totalPaginas={totalPaginas}
                        totalItens={totalItens}
                        itensPorPagina={4}
                        onPaginaChange={setPaginaAtual}
                    />
            </div>

            {/* Modal detalhes do barbeiro */}
            <Dialog open={barbeiroSelecionado !== null} onOpenChange={() => setBarbeiroSelecionado(null)}>
                <DialogContent className="bg-black border-gray-500 text-white w-[calc(100vw-2rem)] max-w-2xl h-[90vh] overflow-y-auto notranslate">
                    <DialogHeader>
                        <DialogTitle className="text-white notranslate">
                            Barbeiro: <span className="text-orange-500 font-bold">{barbeiroSelecionado}</span>
                        </DialogTitle>
                    </DialogHeader>

                    {/* Cards resumo */}
                    <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 notranslate">
                        <div className="bg-black rounded-lg p-3 border border-gray-500 notranslate">
                            <p className="text-white font-bold text-xs uppercase tracking-widest mb-1">Atendimentos</p>
                            <p className="text-orange-500 text-xl font-bold">{atendimentosBarbeiro.length}</p>
                        </div>
                        <div className="bg-black rounded-lg p-3 border border-orange-700 notranslate">
                            <p className="text-white font-bold text-xs uppercase tracking-widest mb-1">Total Gerado</p>
                            <p className="text-orange-500 text-xl font-bold">{formatBRL(totalBarbeiro)}</p>
                        </div>
                        <div className="bg-black rounded-lg p-3 border border-green-700 notranslate">
                            <p className="text-white font-bold text-xs uppercase tracking-widest mb-1">Comissão (50%)</p>
                            <p className="text-green-400 text-xl font-bold">{formatBRL(comissaoBarbeiro)}</p>
                        </div>
                    </div>

                    {/* Tabela de atendimentos do barbeiro */}
                    <div className="overflow-x-auto notranslate">
                        <span className="text-white font-bold">Atendimentos</span>
                    </div>
                    <div className="overflow-x-auto rounded-xl border border-gray-700 notranslate">
                        <table className="w-full text-sm text-left text-gray-300 min-w-[400px] notranslate">
                            <thead className="text-xs text-white uppercase bg-gray-800 border-b border-gray-700 notranslate">
                                <tr>
                                    <th className="px-4 py-3 notranslate">CLIENTE</th>
                                    <th className="px-4 py-3 notranslate">SERVIÇO</th>
                                    <th className="px-4 py-3 notranslate">VALOR</th>
                                    <th className="px-4 py-3 notranslate">PAGAMENTO</th>
                                </tr>
                            </thead>
                            <tbody>
                                {itensPaginaModal.length === 0 ? (
                                    <tr>
                                        <td colSpan={4} className="px-6 py-8 text-center text-gray-500">
                                            Nenhum atendimento no período
                                        </td>
                                    </tr>
                                ) : (
                                    itensPaginaModal.map(item => (
                                        <tr
                                            key={item.id}
                                            className="border-b border-gray-700 hover:bg-gray-900 cursor-pointer notranslate"
                                            onClick={() => abrirEdicao(item)}
                                        >
                                            <td className="px-4 py-4 notranslate font-medium text-white">
                                                <div className="flex items-center gap-2">
                                                    {item.descricao}
                                                    {item.observacao && (
                                                        <MessageSquareWarning color="#bd0000" />
                                                    )}
                                                </div>
                                            </td>
                                            <td className="px-4 py-3 notranslate">{Array.isArray(item.servico) ? item.servico.join(" + ") : item.servico}</td>
                                            <td className="px-4 py-3 notranslate text-orange-500 font-bold">{formatBRL(item.valor)}</td>
                                            <td className="px-4 py-3 notranslate">{item.formaPagamento}</td>
                                        </tr>
                                    ))
                                )}
                            </tbody>
                        </table>
                        
                    </div>
                    {/* Refatorei os itens por página da tabela de atendimentos no modal de Barbeiros */}
                        <Paginacao
                                paginaAtual={paginaModal}
                                totalPaginas={totalPaginasModal}
                                totalItens={totalItensModal}
                                itensPorPagina={4}
                                onPaginaChange={setPaginaModal}

                            />
                </DialogContent>
            </Dialog>

            {/* Modal editar atendimento */}
            <ModalEditarAtendimento
                atendimento={atendimentoSelecionado}
                open={atendimentoSelecionado !== null}
                onFechar={() => setAtendimentoSelecionado(null)}
                onConfirmar={async (id, form) => {
                    await atualizarAtendimentoAdmin(id, form)
                    recarregar()
                }}
                onDeletar={async (id) => {
                    await deletarAtendimento(id)
                    recarregar()
                }}
                onReativar={async (id) => {
                    await ativarAtendimento(id)
                    recarregar()
                }}
            />
        </div>
    )
}