import { useState } from "react"
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

export default function DashboardPage() {
    const {
        carregando, totalDia, totalAtendimentos, ticketMedio,
        porBarbeiro, atendimentos,
        filtroInicio, filtroFim, setFiltroInicio, setFiltroFim, recarregar
    } = useDashboard()

    const { atualizarAtendimentoAdmin, deletarAtendimento } = useAtendimentos()

    const [barbeiroSelecionado, setBarbeiroSelecionado] = useState<string | null>(null)
    const [atendimentoSelecionado, setAtendimentoSelecionado] = useState<DadosProps | null>(null)

    const atendimentosBarbeiro = atendimentos.filter(a => a.barbeiro === barbeiroSelecionado)
    const { itensPagina, paginaAtual, totalPaginas, totalItens, setPaginaAtual } = usePaginacao(atendimentos, 4)

    const formatBRL = (valor: number) =>
        valor.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })

    const isPeriodoHoje = filtroInicio === filtroFim && filtroInicio === hoje()

    // Adapta AtendimentoAdminProps → DadosProps para o modal de edição
    const abrirEdicao = (item: AtendimentoAdminProps) => {
        setAtendimentoSelecionado({
            id: item.id,
            descricao: item.descricao,
            servico: Array.isArray(item.servico) ? item.servico : item.servico.split(", "),
            valor: item.valor,
            formaPagamento: item.formaPagamento,
            date: item.date,
            status: item.status
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
                    <p className="text-gray-400 text-xs uppercase tracking-widest mb-1">Atendimentos por Barbeiro</p>
                    <p className="text-gray-500 text-xs mb-4">Clique na barra para ver detalhes</p>
                    <ResponsiveContainer width="100%" height={220}>
                        <BarChart
                            data={porBarbeiro}
                            onClick={(data) => {
                                const payload = (data as any)?.activePayload?.[0]
                                if (payload) setBarbeiroSelecionado(payload.payload.barbeiro)
                            }}
                            style={{ cursor: "pointer" }}
                        >
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

            {/* Tabela principal */}
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
                                        onClick={() => items.status !== -1 && abrirEdicao(items)}
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

            {/* Modal detalhes por barbeiro */}
            <Dialog open={barbeiroSelecionado !== null} onOpenChange={() => setBarbeiroSelecionado(null)}>
                <DialogContent className="bg-gray-900 border-gray-700 text-white w-full max-w-lg mx-4 notranslate">
                    <DialogHeader>
                        <DialogTitle className="text-white notranslate">
                            Atendimentos de <span className="text-orange-500 notranslate">{barbeiroSelecionado}</span>
                        </DialogTitle>
                    </DialogHeader>
                    <div className="grid grid-cols-2 gap-3 mt-2 notranslate">
                        <div className="bg-gray-800 rounded-lg p-3 notranslate">
                            <p className="text-gray-400 text-xs uppercase tracking-widest mb-1">Atendimentos</p>
                            <p className="text-orange-500 text-xl font-bold">{atendimentosBarbeiro.length}</p>
                        </div>
                        <div className="bg-gray-800 rounded-lg p-3 notranslate">
                            <p className="text-gray-400 text-xs uppercase tracking-widest mb-1">Total</p>
                            <p className="text-orange-500 text-xl font-bold">
                                {formatBRL(atendimentosBarbeiro.reduce((acc, a) => acc + a.valor, 0))}
                            </p>
                        </div>
                    </div>
                    <div className="overflow-x-auto rounded-lg border border-gray-700 mt-2 notranslate">
                        <table className="w-full text-sm text-left text-gray-300 min-w-[360px] notranslate">
                            <thead className="text-xs text-gray-400 uppercase bg-gray-800 border-b border-gray-700 notranslate">
                                <tr>
                                    <th className="px-4 py-3 notranslate">CLIENTE</th>
                                    <th className="px-4 py-3 notranslate">SERVIÇO</th>
                                    <th className="px-4 py-3 notranslate">VALOR</th>
                                    <th className="px-4 py-3 notranslate">PAGAMENTO</th>
                                </tr>
                            </thead>
                            <tbody>
                                {atendimentosBarbeiro.map(item => (
                                    <tr
                                        key={item.id}
                                        className="border-b border-gray-700 hover:bg-gray-800 cursor-pointer notranslate"
                                        onClick={() => { setBarbeiroSelecionado(null); abrirEdicao(item) }}
                                    >
                                        <td className="px-4 py-3 notranslate font-medium text-white">{item.descricao}</td>
                                        <td className="px-4 py-3 notranslate">{item.servico}</td>
                                        <td className="px-4 py-3 notranslate text-orange-500 font-bold">{formatBRL(item.valor)}</td>
                                        <td className="px-4 py-3 notranslate">{item.formaPagamento}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
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
            />
        </div>
    )
}