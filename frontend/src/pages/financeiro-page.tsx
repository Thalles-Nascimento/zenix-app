import { Badge } from "../components/ui/badge"
import { Spinner } from "../components/ui/spinner"
import { useFinanceiro } from "../hooks/use-financeiro"
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from "recharts"
import { usePaginacao } from "../hooks/use-pagination"
import { Paginacao } from "../components/pagination"
import { hoje } from "@/utils/date"

const formatBRL = (valor: number) =>
    valor.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })

export default function FinanceiroPage() {
    const {
        carregando, totalDia, totalSemana, totalMes, totalPeriodo,
        ticketMedio, porFormaPagamento, atendimentosFiltrados,
        dadosGrafico, filtroInicio, filtroFim, setFiltroInicio, setFiltroFim, 
        comissaoBarbeiro, comissaoDia, comissaoSemana, comissaoMes
    } = useFinanceiro()
    
    const isFiltroInicio = filtroInicio === ""
    const isFiltroFim = filtroFim === ""
    const { itensPagina, paginaAtual, totalPaginas, totalItens, setPaginaAtual } = usePaginacao(atendimentosFiltrados, 4)

    if (carregando) {
        return (
            <div className="w-full flex items-center justify-center py-20">
                <Badge variant="secondary"><Spinner />Carregando...</Badge>
            </div>
        )
    }

    return (
        <div className="bg-black flex flex-col gap-6 notranslate">
            <h1 className="text-white text-xl font-bold">Financeiro</h1>

            {/* Filtro por período */}
            <div className="flex flex-wrap items-center gap-3 bg-gray-900 rounded-xl border border-gray-700 p-4 notranslate">
                <span className="text-white text-sm font-medium w-full sm:w-auto">Filtrar por período:</span>
                <div className="flex items-center gap-2 notranslate">
                    <label className="text-gray-400 text-sm">De</label>
                    <input type="date" value={filtroInicio}
                        onChange={(e) => setFiltroInicio(e.target.value)}
                        className="bg-gray-800 border border-gray-700 text-white text-sm rounded-lg px-1 py-1 outline-none" />
                </div>
                <div className="flex items-center gap-2 notranslate">
                    <label className="text-gray-400 text-sm">Até</label>
                    <input type="date" value={filtroFim}
                        onChange={(e) => setFiltroFim(e.target.value)}
                        className="bg-gray-800 border border-gray-700 text-white text-sm rounded-lg px-1 py-1 outline-none" />
                </div>
                {(filtroInicio || filtroFim) && (
                    <button onClick={() => { setFiltroInicio(""); setFiltroFim("") }}
                        className="text-orange-500 text-sm hover:underline">
                        Limpar filtro
                    </button>
                )}
            </div>

            {/* Cards fixos */}
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 notranslate">
                <div className="bg-black rounded-xl border border-orange-500 p-5 notranslate">
                    <p className="text-white text-xs uppercase tracking-widest mb-1">Hoje</p>
                    <p className="text-orange-500 text-2xl font-bold">{formatBRL(totalDia)}</p>
                </div>
                <div className="bg-black rounded-xl border border-orange-500 p-5 notranslate">
                    <p className="text-white text-xs uppercase tracking-widest mb-1">Esta Semana</p>
                    <p className="text-orange-500 text-2xl font-bold">{formatBRL(totalSemana)}</p>
                </div>
                <div className="bg-black rounded-xl border border-orange-500 p-5 notranslate">
                    <p className="text-white text-xs uppercase tracking-widest mb-1">Este Mês</p>
                    <p className="text-orange-500 text-2xl font-bold">{formatBRL(totalMes)}</p>
                </div>
                <div className="bg-black rounded-xl border border-green-700 p-5 notranslate">
                    <p className="text-white text-xs uppercase tracking-widest mb-1">Minha Comissão (Dia)</p>
                    <p className="text-green-400 text-2xl font-bold">{formatBRL(comissaoDia)}</p>
                    <p className="text-gray-500 text-xs mt-1">50% do total</p>
                </div>
                <div className="bg-black rounded-xl border border-green-700 p-5 notranslate">
                    <p className="text-white text-xs uppercase tracking-widest mb-1">Minha Comissão (Semana)</p>
                    <p className="text-green-400 text-2xl font-bold">{formatBRL(comissaoSemana)}</p>
                    <p className="text-gray-500 text-xs mt-1">50% do total</p>
                </div>
                <div className="bg-black rounded-xl border border-green-700 p-5 notranslate">
                    <p className="text-white text-xs uppercase tracking-widest mb-1">Minha Comissão (Mês)</p>
                    <p className="text-green-400 text-2xl font-bold">{formatBRL(comissaoMes)}</p>
                    <p className="text-gray-500 text-xs mt-1">50% do total</p>
                </div>
            </div>

            {/* Cards período filtrado */}
            {(filtroInicio || filtroFim) && (
                <>
                    <h1 className="text-white text-xl font-bold">Filtro de: {isFiltroInicio ? hoje() : filtroInicio} até {isFiltroFim ? hoje() : filtroFim}</h1>
                    <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 notranslate">                    
                        <div className="bg-black rounded-xl border border-orange-700 p-5 notranslate">
                            <p className="text-gray-400 text-xs uppercase tracking-widest mb-1">Total do Período</p>
                            <p className="text-orange-500 text-2xl font-bold">{formatBRL(totalPeriodo)}</p>
                        </div>
                        <div className="bg-black rounded-xl border border-orange-700 p-5 notranslate">
                            <p className="text-gray-400 text-xs uppercase tracking-widest mb-1">Ticket Médio</p>
                            <p className="text-orange-500 text-2xl font-bold">{formatBRL(ticketMedio)}</p>
                        </div>
                        <div className="bg-black rounded-xl border border-orange-700 p-5 notranslate">
                            <p className="text-gray-400 text-xs uppercase tracking-widest mb-1">Atendimentos</p>
                            <p className="text-orange-500 text-2xl font-bold">{atendimentosFiltrados.length}</p>
                        </div>
                        <div className="bg-black rounded-xl border border-green-700 p-5 notranslate">
                            <p className="text-gray-400 text-xs uppercase tracking-widest mb-1">Minha Comissão</p>
                            <p className="text-green-400 text-2xl font-bold">{formatBRL(comissaoBarbeiro)}</p>
                            <p className="text-gray-500 text-xs mt-1">50% do período</p>
                        </div>
                    </div>
                </>
            )}

            {/* Gráfico + Formas de pagamento */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-4 notranslate">
                <div className="lg:col-span-2 bg-black rounded-xl border border-gray-500 p-5 notranslate">
                    <p className="text-white text-xs uppercase tracking-widest mb-4">Arrecadado por Dia</p>
                    <ResponsiveContainer width="100%" height={220}>
                        <AreaChart data={dadosGrafico}>
                            <defs>
                                <linearGradient id="colorTotal" x1="0" y1="0" x2="0" y2="1">
                                    <stop offset="5%" stopColor="#ea580c" stopOpacity={1} />
                                    <stop offset="95%" stopColor="#ea580c" stopOpacity={0.2} />
                                </linearGradient>
                            </defs>
                            <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
                            <XAxis dataKey="data" stroke="#9ca3af" tick={{ fontSize: 11 }} />
                            <YAxis stroke="#9ca3af" tick={{ fontSize: 11 }} tickFormatter={(v) => `R$ ${v}`} />
                            <Tooltip
                                contentStyle={{ backgroundColor: "#111827", border: "1px solid #374151", borderRadius: "8px" }}
                                labelStyle={{ color: "white" }}
                                formatter={(value: number | undefined) => [formatBRL(value ?? 0), "Total"]}
                            />
                            <Area type="monotone" dataKey="total" stroke="#ea580c" strokeWidth={2} fill="url(#colorTotal)" />
                        </AreaChart>
                    </ResponsiveContainer>
                </div>

                <div className="bg-black rounded-xl border border-gray-500 p-5 notranslate">
                    <p className="text-white text-xs uppercase tracking-widest mb-4">Por Forma de Pagamento</p>
                    {porFormaPagamento.length === 0 ? (
                        <p className="text-gray-500 text-sm">Nenhum dado</p>
                    ) : (
                        <div className="flex flex-col gap-4 notranslate">
                            {porFormaPagamento.map(item => (
                                <div key={item.forma}>
                                    <div className="flex justify-between mb-1 notranslate">
                                        <span className="text-white text-sm">{item.forma}</span>
                                        <span className="text-orange-500 text-sm font-bold">{formatBRL(item.total)}</span>
                                    </div>
                                    <div className="w-full bg-gray-700 rounded-full h-1.5 notranslate">
                                        <div
                                            className="bg-orange-500 h-1.5 rounded-full"
                                            style={{
                                                width: `${(item.total / (atendimentosFiltrados.reduce((acc, a) => acc + a.valor, 0) || 1)) * 100}%`
                                            }}
                                        />
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </div>

            {/* Tabela */}
            <div className="overflow-x-auto rounded-xl border border-gray-700 notranslate">
                <table className="w-full text-sm text-left text-gray-300 min-w-[500px] notranslate">
                    <thead className="text-xs text-white uppercase bg-gray-800 border-b border-gray-700 notranslate">
                        <tr>
                            <th className="px-4 py-3 notranslate">CLIENTE</th>
                            <th className="px-4 py-3 notranslate">SERVIÇO</th>
                            <th className="px-4 py-3 notranslate">VALOR</th>
                            <th className="px-4 py-3 notranslate">PAGAMENTO</th>
                            <th className="px-4 py-3 notranslate">DATA</th>
                        </tr>
                    </thead>
                    <tbody>
                        {atendimentosFiltrados.length === 0 ? (
                            <tr>
                                <td colSpan={5} className="px-6 py-8 text-center text-gray-500">
                                    Nenhum atendimento encontrado no período
                                </td>
                            </tr>
                        ) : (
                            itensPagina.map(item => (
                                <tr key={item.id} className="bg-black border-b border-gray-500 hover:bg-gray-900 notranslate">
                                    <td className="px-4 py-4 notranslate font-medium text-white">{item.descricao}</td>
                                    <td className="px-4 py-4 notranslate">{Array.isArray(item.servico) ? item.servico.join(" + ") : item.servico}</td>
                                    <td className="px-4 py-4 notranslate text-orange-500 font-bold">{formatBRL(item.valor)}</td>
                                    <td className="px-4 py-4 notranslate">{item.formaPagamento}</td>
                                    <td className="px-4 py-4 notranslate">{item.date}</td>
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
    )
}
