import { useState } from "react"
import { useDashboard } from "../hooks/use-dashboard"
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from "recharts"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "../components/ui/dialog"
import { hoje } from "../utils/date"
import { Badge } from "../components/ui/badge"
import { Spinner } from "../components/ui/spinner"
import { usePaginacao } from "../hooks/use-pagination"
import { Paginacao } from "../components/pagination"

export default function DashboardPage() {
    const {
        carregando, totalDia, totalAtendimentos, ticketMedio,
        rankingServicos, porBarbeiro, atendimentos,
        filtroInicio, filtroFim, setFiltroInicio, setFiltroFim
    } = useDashboard()

    const [barbeiroSelecionado, setBarbeiroSelecionado] = useState<string | null>(null)
    const atendimentosBarbeiro = atendimentos.filter(a => a.barbeiro === barbeiroSelecionado)
    const { itensPagina, paginaAtual, totalPaginas, totalItens, setPaginaAtual } = usePaginacao(atendimentos, 4)

    const formatBRL = (valor: number) =>
        valor.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })

    const isPeriodoHoje = filtroInicio === filtroFim && filtroInicio === hoje()

    if (carregando) {
        return (
            <div className="w-full flex items-center justify-center py-20">
                <Badge variant="secondary"><Spinner />Carregando...</Badge>
            </div>
        )
    }

    return (
        <div className="flex flex-col gap-6">
            <h1 className="text-white text-xl font-bold">Dashboard</h1>

            {/* Filtro por período */}
            <div className="flex flex-wrap items-center gap-3 bg-gray-900 rounded-xl border border-gray-700 p-4">
                <span className="text-white text-sm font-medium">Período:</span>
                <div className="flex items-center gap-2">
                    <label className="text-white text-sm">De</label>
                    <input
                        type="date"
                        value={filtroInicio}
                        onChange={(e) => setFiltroInicio(e.target.value)}
                        className="bg-gray-800 border border-gray-700 text-white text-sm rounded-lg px-3 py-2 outline-none"
                    />
                </div>
                <div className="flex items-center gap-2">
                    <label className="text-white text-sm">Até</label>
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
                        className="text-orange-500 text-sm hover:underline"
                    >
                        Voltar para hoje
                    </button>
                )}
            </div>

            {/* Cards */}
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                <div className="bg-gray-900 rounded-xl border border-gray-700 p-5">
                    <p className="text-gray-400 text-xs uppercase tracking-widest mb-1">
                        {isPeriodoHoje ? "Total do Dia" : "Total do Período"}
                    </p>
                    <p className="text-orange-500 text-2xl font-bold">{formatBRL(totalDia)}</p>
                </div>
                <div className="bg-gray-900 rounded-xl border border-gray-700 p-5">
                    <p className="text-gray-400 text-xs uppercase tracking-widest mb-1">Atendimentos</p>
                    <p className="text-orange-500 text-2xl font-bold">{totalAtendimentos}</p>
                </div>
                <div className="bg-gray-900 rounded-xl border border-gray-700 p-5">
                    <p className="text-gray-400 text-xs uppercase tracking-widest mb-1">Ticket Médio</p>
                    <p className="text-orange-500 text-2xl font-bold">{formatBRL(ticketMedio)}</p>
                </div>
            </div>

            {/* Gráficos */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
                <div className="bg-gray-900 rounded-xl border border-gray-700 p-5">
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

                <div className="bg-gray-900 rounded-xl border border-gray-700 p-5">
                    <p className="text-gray-400 text-xs uppercase tracking-widest mb-4">Ranking de Serviços</p>
                    {rankingServicos.length === 0 ? (
                        <p className="text-gray-500 text-sm">Nenhum dado no período</p>
                    ) : (
                        <div className="flex flex-col gap-3">
                            {rankingServicos.map((item, index) => (
                                <div key={item.servico} className="flex items-center gap-3">
                                    <span className="text-orange-500 font-bold w-5 text-sm">{index + 1}º</span>
                                    <div className="flex-1">
                                        <div className="flex justify-between mb-1">
                                            <span className="text-white text-sm">{item.servico}</span>
                                            <span className="text-gray-400 text-sm">{item.quantidade}x</span>
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

            {/* Tabela */}
            <div className="bg-gray-900 rounded-xl border border-gray-700">
                <div className="px-4 py-4 border-b border-gray-700">
                    <p className="text-gray-400 text-xs uppercase tracking-widest">
                        {isPeriodoHoje ? "Atendimentos de Hoje" : `Atendimentos de ${filtroInicio} até ${filtroFim}`}
                    </p>
                </div>
                <div className="overflow-x-auto">
                    <table className="w-full text-sm text-left text-gray-300 min-w-[500px]">
                        <thead className="text-xs text-gray-400 uppercase bg-gray-800 border-b border-gray-700">
                            <tr>
                                <th className="px-4 py-3">CLIENTE</th>
                                <th className="px-4 py-3">SERVIÇO</th>
                                <th className="px-4 py-3">BARBEIRO</th>
                                <th className="px-4 py-3">VALOR</th>
                                <th className="px-4 py-3">PAGAMENTO</th>
                            </tr>
                        </thead>
                        <tbody>
                            {atendimentos.length === 0 ? (
                                <tr>
                                    <td colSpan={5} className="px-6 py-8 text-center text-gray-500">
                                        Nenhum atendimento no período
                                    </td>
                                </tr>
                            ) : (
                                itensPagina.map(items => (
                                    <tr key={items.id} className="bg-gray-900 border-b border-gray-700 hover:bg-gray-800">
                                        <td className="px-4 py-4 font-medium text-white">{items.descricao}</td>
                                        <td className="px-4 py-4">{items.servico}</td>
                                        <td className="px-4 py-4 text-orange-500">{items.barbeiro}</td>
                                        <td className="px-4 py-4 font-bold text-orange-500">{formatBRL(items.valor)}</td>
                                        <td className="px-4 py-4">{items.formaPagamento}</td>
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

            {/* Modal barbeiro */}
            <Dialog open={barbeiroSelecionado !== null} onOpenChange={() => setBarbeiroSelecionado(null)}>
                <DialogContent className="bg-gray-900 border-gray-700 text-white w-full max-w-lg mx-4">
                    <DialogHeader>
                        <DialogTitle className="text-white">
                            Atendimentos de <span className="text-orange-500">{barbeiroSelecionado}</span>
                        </DialogTitle>
                    </DialogHeader>
                    <div className="grid grid-cols-2 gap-3 mt-2">
                        <div className="bg-gray-800 rounded-lg p-3">
                            <p className="text-gray-400 text-xs uppercase tracking-widest mb-1">Atendimentos</p>
                            <p className="text-orange-500 text-xl font-bold">{atendimentosBarbeiro.length}</p>
                        </div>
                        <div className="bg-gray-800 rounded-lg p-3">
                            <p className="text-gray-400 text-xs uppercase tracking-widest mb-1">Total</p>
                            <p className="text-orange-500 text-xl font-bold">
                                {formatBRL(atendimentosBarbeiro.reduce((acc, a) => acc + a.valor, 0))}
                            </p>
                        </div>
                    </div>
                    <div className="overflow-x-auto rounded-lg border border-gray-700 mt-2">
                        <table className="w-full text-sm text-left text-gray-300 min-w-[360px]">
                            <thead className="text-xs text-gray-400 uppercase bg-gray-800 border-b border-gray-700">
                                <tr>
                                    <th className="px-4 py-3">CLIENTE</th>
                                    <th className="px-4 py-3">SERVIÇO</th>
                                    <th className="px-4 py-3">VALOR</th>
                                    <th className="px-4 py-3">PAGAMENTO</th>
                                </tr>
                            </thead>
                            <tbody>
                                {atendimentosBarbeiro.map(item => (
                                    <tr key={item.id} className="border-b border-gray-700 hover:bg-gray-800">
                                        <td className="px-4 py-3 font-medium text-white">{item.descricao}</td>
                                        <td className="px-4 py-3">{item.servico}</td>
                                        <td className="px-4 py-3 text-orange-500 font-bold">{formatBRL(item.valor)}</td>
                                        <td className="px-4 py-3">{item.formaPagamento}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </DialogContent>
            </Dialog>
        </div>
    )
}
