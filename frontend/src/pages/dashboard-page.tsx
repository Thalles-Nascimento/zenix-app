import { useState, useEffect } from "react"
import { useDashboard } from "../hooks/use-dashboard"
import { useAtendimentos } from "../hooks/use-atendimentos"
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from "recharts"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "../components/ui/dialog"
import { hoje, parseData } from "../utils/date"
import { Badge } from "../components/ui/badge"
import { Spinner } from "../components/ui/spinner"
import { usePaginacao } from "../hooks/use-pagination"
import { Paginacao } from "../components/pagination"
import { ModalEditarAtendimento } from "./modals/atendimento-editar-modal"
import type { AtendimentoAdminProps } from "../types/dashboard"
import type { DadosProps } from "../types/atendimento"
import { Toaster } from "sonner"

export default function DashboardPage() {
    const {
        carregando, totalDia, totalAtendimentos, ticketMedio,
        porBarbeiro, atendimentos,
        filtroInicio, filtroFim, setFiltroInicio, setFiltroFim, recarregar
    } = useDashboard()

    const { atualizarAtendimentoAdmin, deletarAtendimento, ativarAtendimento } = useAtendimentos()

    const [barbeiroSelecionado, setBarbeiroSelecionado] = useState<string | null>(null)
    const [atendimentoSelecionado, setAtendimentoSelecionado] = useState<DadosProps | null>(null)

    const [modalFiltroInicio, setModalFiltroInicio] = useState("")
    const [modalFiltroFim, setModalFiltroFim] = useState("")

    const atendimentosBarbeiro = atendimentos
        .filter(a => a.barbeiro === barbeiroSelecionado && a.status === 1)
        .filter(a => {
            if (!modalFiltroInicio && !modalFiltroFim) return true
            const data = parseData(a.date)
            if (modalFiltroInicio && data < modalFiltroInicio) return false
            if (modalFiltroFim && data > modalFiltroFim) return false
            return true
        })

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
    } = usePaginacao(porBarbeiro, 5)

    // Paginação — modal do barbeiro
    const {
        itensPagina: itensPaginaModal,
        paginaAtual: paginaModal,
        totalPaginas: totalPaginasModal,
        totalItens: totalItensModal,
        setPaginaAtual: setPaginaModal
    } = usePaginacao(atendimentosBarbeiro, 10)

    // Reseta página do modal ao trocar barbeiro ou filtro
    useEffect(() => {
        setPaginaModal(1)
    }, [modalFiltroInicio, modalFiltroFim, barbeiroSelecionado])

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
            status: item.status
        })
    }

    const abrirModalBarbeiro = (nomeBarbeiro: string) => {
        setBarbeiroSelecionado(nomeBarbeiro)
        setModalFiltroInicio("")
        setModalFiltroFim("")
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
            <div className="flex flex-wrap items-center gap-3 bg-gray-900 rounded-xl border border-gray-700 p-4 notranslate">
                <span className="text-gray-400 text-sm font-medium w-full sm:w-auto">Período:</span>
                <div className="flex items-center gap-2 notranslate">
                    <label className="text-gray-400 text-sm">De</label>
                    <input
                        type="date"
                        value={filtroInicio}
                        onChange={(e) => setFiltroInicio(e.target.value)}
                        className="bg-gray-800 border border-gray-700 text-white text-sm rounded-lg px-3 py-2 outline-none"
                    />
                </div>
                <div className="flex items-center gap-2 notranslate">
                    <label className="text-gray-400 text-sm">Até</label>
                    <input
                        type="date"
                        value={filtroFim}
                        onChange={(e) => setFiltroFim(e.target.value)}
                        className="bg-gray-800 border border-gray-700 text-white text-sm rounded-lg px-3 py-2 outline-none"
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
                <div className="bg-gray-900 rounded-xl border border-gray-700 p-5 notranslate">
                    <p className="text-gray-400 text-xs uppercase tracking-widest mb-1">
                        {isPeriodoHoje ? "Total do Dia" : "Total do Período"}
                    </p>
                    <p className="text-orange-500 text-2xl font-bold">{formatBRL(totalDia)}</p>
                </div>
                <div className="bg-gray-900 rounded-xl border border-gray-700 p-5 notranslate">
                    <p className="text-gray-400 text-xs uppercase tracking-widest mb-1">Atendimentos</p>
                    <p className="text-orange-500 text-2xl font-bold">{totalAtendimentos}</p>
                </div>
                <div className="bg-gray-900 rounded-xl border border-gray-700 p-5 notranslate">
                    <p className="text-gray-400 text-xs uppercase tracking-widest mb-1">Ticket Médio</p>
                    <p className="text-orange-500 text-2xl font-bold">{formatBRL(ticketMedio)}</p>
                </div>
            </div>

            {/* Gráficos */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-4 notranslate">
                <div className="bg-gray-900 rounded-xl border border-gray-700 p-5 notranslate">
                    <p className="text-gray-400 text-xs uppercase tracking-widest mb-4">Atendimentos por Barbeiro</p>
                    <ResponsiveContainer width="100%" height={220}>
                        <BarChart data={porBarbeiro}>
                            <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
                            <XAxis dataKey="barbeiro" stroke="#9ca3af" tick={{ fontSize: 11 }} />
                            <YAxis stroke="#9ca3af" tick={{ fontSize: 11 }} />
                            <Tooltip
                                contentStyle={{ backgroundColor: "#111827", border: "1px solid #374151", borderRadius: "8px" }}
                                labelStyle={{ color: "#f97316" }}
                            />
                            <Bar dataKey="quantidade" fill="#ea580c" radius={[4, 4, 0, 0]} name="Atendimentos" />
                        </BarChart>
                    </ResponsiveContainer>
                </div>

                <div className="bg-gray-900 rounded-xl border border-gray-700 p-5 notranslate">
                    <p className="text-gray-400 text-xs uppercase tracking-widest mb-4">Ranking de Barbeiros</p>
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
                                                <span className="text-gray-400 text-sm">{item.quantidade}x</span>
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
                    <p className="text-gray-400 text-xs uppercase tracking-widest">Resumo por Barbeiro</p>
                    <p className="text-gray-500 text-xs mt-1">Clique em um barbeiro para ver os detalhes do período</p>
                </div>
                <div className="overflow-x-auto notranslate">
                    <table className="w-full text-sm text-left text-gray-300 min-w-[500px] notranslate">
                        <thead className="text-xs text-gray-400 uppercase bg-gray-800 border-b border-gray-700 notranslate">
                            <tr>
                                <th className="px-4 py-3 notranslate">BARBEIRO</th>
                                <th className="px-4 py-3 notranslate">ATENDIMENTOS</th>
                                <th className="px-4 py-3 notranslate">TOTAL GERADO</th>
                                <th className="px-4 py-3 notranslate">COMISSÃO (50%)</th>
                            </tr>
                        </thead>
                        <tbody>
                            {itensPaginaBarbeiros.length === 0 ? (
                                <tr>
                                    <td colSpan={4} className="px-6 py-8 text-center text-gray-500 notranslate">
                                        Nenhum atendimento no período
                                    </td>
                                </tr>
                            ) : (
                                itensPaginaBarbeiros.map(item => (
                                    <tr
                                        key={item.barbeiro}
                                        className="bg-gray-900 border-b border-gray-700 hover:bg-gray-800 cursor-pointer transition-colors notranslate"
                                        onClick={() => abrirModalBarbeiro(item.barbeiro)}
                                    >
                                        <td className="px-4 py-4 notranslate font-medium text-white">{item.barbeiro}</td>
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
                        itensPorPagina={5}
                        onPaginaChange={setPaginaBarbeiros}
                    />
                </div>
            </div>

            {/* Tabela principal de atendimentos */}
            <div className="bg-gray-900 rounded-xl border border-gray-700 notranslate">
                <div className="px-4 py-4 border-b border-gray-700 notranslate">
                    <p className="text-gray-400 text-xs uppercase tracking-widest">
                        {isPeriodoHoje ? "Atendimentos de Hoje" : `Atendimentos de ${filtroInicio} até ${filtroFim}`}
                    </p>
                </div>
                <div className="overflow-x-auto notranslate">
                    <table className="w-full text-sm text-left text-gray-300 min-w-[500px] notranslate">
                        <thead className="text-xs text-gray-400 uppercase bg-gray-800 border-b border-gray-700 notranslate">
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
                                        className={`border-b border-gray-700 hover:bg-gray-800 cursor-pointer
                                            ${items.status === -1 ? "opacity-50" : "bg-gray-900"}`}
                                        onClick={() => abrirEdicao(items)}
                                    >
                                        <td className="px-4 py-4 notranslate font-medium text-white">{items.descricao}</td>
                                        <td className="px-4 py-4 notranslate">{Array.isArray(items.servico) ? items.servico.join(" + ") : items.servico}</td>
                                        <td className={`px-4 py-4 notranslate ${items.status === -1 ? "text-gray-500" : "text-orange-500"}`}>
                                            {items.barbeiro}
                                        </td>
                                        <td className={`px-4 py-4 notranslate font-bold ${items.status === -1 ? "text-gray-500" : "text-orange-500"}`}>
                                            {formatBRL(items.valor)}
                                        </td>
                                        <td className="px-4 py-4 notranslate">{items.formaPagamento}</td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                    <Paginacao
                        paginaAtual={paginaAtual}
                        totalPaginas={totalPaginas}
                        totalItens={totalItens}
                        itensPorPagina={4}
                        onPaginaChange={setPaginaAtual}
                    />
                </div>
            </div>

            {/* Modal detalhes do barbeiro */}
            <Dialog open={barbeiroSelecionado !== null} onOpenChange={() => setBarbeiroSelecionado(null)}>
                <DialogContent className="bg-gray-900 border-gray-700 text-white w-full max-w-2xl mx-4 max-h-[90vh] overflow-y-auto notranslate">
                    <DialogHeader>
                        <DialogTitle className="text-white notranslate">
                            Atendimentos de <span className="text-orange-500">{barbeiroSelecionado}</span>
                        </DialogTitle>
                    </DialogHeader>

                    {/* Filtro extra */}
                    <div className="flex flex-wrap items-center gap-2 bg-gray-800 rounded-lg p-3 notranslate">
                        <span className="text-gray-400 text-xs font-medium">Refinar período:</span>
                        <input
                            type="date"
                            value={modalFiltroInicio}
                            onChange={(e) => setModalFiltroInicio(e.target.value)}
                            className="bg-gray-700 border border-gray-600 text-white text-xs rounded px-2 py-1 outline-none"
                        />
                        <span className="text-gray-400 text-xs">até</span>
                        <input
                            type="date"
                            value={modalFiltroFim}
                            onChange={(e) => setModalFiltroFim(e.target.value)}
                            className="bg-gray-700 border border-gray-600 text-white text-xs rounded px-2 py-1 outline-none"
                        />
                        {(modalFiltroInicio || modalFiltroFim) && (
                            <button
                                onClick={() => { setModalFiltroInicio(""); setModalFiltroFim("") }}
                                className="text-orange-500 text-xs hover:underline"
                            >
                                Limpar
                            </button>
                        )}
                    </div>

                    {/* Cards resumo */}
                    <div className="grid grid-cols-3 gap-3 notranslate">
                        <div className="bg-gray-800 rounded-lg p-3 notranslate">
                            <p className="text-gray-400 text-xs uppercase tracking-widest mb-1">Atendimentos</p>
                            <p className="text-orange-500 text-xl font-bold">{atendimentosBarbeiro.length}</p>
                        </div>
                        <div className="bg-gray-800 rounded-lg p-3 notranslate">
                            <p className="text-gray-400 text-xs uppercase tracking-widest mb-1">Total Gerado</p>
                            <p className="text-orange-500 text-xl font-bold">{formatBRL(totalBarbeiro)}</p>
                        </div>
                        <div className="bg-gray-800 rounded-lg p-3 border border-green-700 notranslate">
                            <p className="text-gray-400 text-xs uppercase tracking-widest mb-1">Comissão (50%)</p>
                            <p className="text-green-400 text-xl font-bold">{formatBRL(comissaoBarbeiro)}</p>
                        </div>
                    </div>

                    {/* Tabela de atendimentos do barbeiro */}
                    <div className="overflow-x-auto rounded-lg border border-gray-700 notranslate">
                        <table className="w-full text-sm text-left text-gray-300 min-w-[400px] notranslate">
                            <thead className="text-xs text-gray-400 uppercase bg-gray-800 border-b border-gray-700 notranslate">
                                <tr>
                                    <th className="px-4 py-3 notranslate">CLIENTE</th>
                                    <th className="px-4 py-3 notranslate">SERVIÇO</th>
                                    <th className="px-4 py-3 notranslate">VALOR</th>
                                    <th className="px-4 py-3 notranslate">PAGAMENTO</th>
                                    <th className="px-4 py-3 notranslate">DATA</th>
                                </tr>
                            </thead>
                            <tbody>
                                {itensPaginaModal.length === 0 ? (
                                    <tr>
                                        <td colSpan={5} className="px-6 py-8 text-center text-gray-500">
                                            Nenhum atendimento no período
                                        </td>
                                    </tr>
                                ) : (
                                    itensPaginaModal.map(item => (
                                        <tr
                                            key={item.id}
                                            className="border-b border-gray-700 hover:bg-gray-800 cursor-pointer notranslate"
                                            onClick={() => abrirEdicao(item)}
                                        >
                                            <td className="px-4 py-3 notranslate font-medium text-white">{item.descricao}</td>
                                            <td className="px-4 py-3 notranslate">{Array.isArray(item.servico) ? item.servico.join(" + ") : item.servico}</td>
                                            <td className="px-4 py-3 notranslate text-orange-500 font-bold">{formatBRL(item.valor)}</td>
                                            <td className="px-4 py-3 notranslate">{item.formaPagamento}</td>
                                            <td className="px-4 py-3 notranslate text-gray-400">{item.date}</td>
                                        </tr>
                                    ))
                                )}
                            </tbody>
                        </table>
                        <Paginacao
                            paginaAtual={paginaModal}
                            totalPaginas={totalPaginasModal}
                            totalItens={totalItensModal}
                            itensPorPagina={10}
                            onPaginaChange={setPaginaModal}
                        />
                    </div>
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