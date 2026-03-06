
import { useAtendimentos } from "../hooks/use-atendimentos";
import { Spinner } from "../components/ui/spinner";
import { Badge } from "../components/ui/badge"
import { useState } from "react";
import type { DadosProps } from "../types/atendimento";
import { Toaster } from "sonner";
import { ModalNovoAtendimento } from "./modals/atendimento-novo-modal";
import { ModalEditarAtendimento } from "./modals/atendimento-editar-modal";
import { usePaginacao } from "../hooks/use-pagination"
import { Paginacao } from "../components/pagination"



export default function Atendimentos(){

    const { dados, carregando, periodo, setPeriodo, criarAtendimento, atualizarAtendimento, deletarAtendimento } = useAtendimentos()
    const [atendimentoSelecionado, setAtendimentoSelecionado] = useState<DadosProps | null>(null)
    const { itensPagina, paginaAtual, totalPaginas, totalItens, setPaginaAtual } = usePaginacao(dados, 7)


    if (carregando){
        return (
            <div className="min-h-screen bg-gray-950 w-full flex items-center justify-center">
                <Badge variant="secondary">
                    <Spinner data-icon="inline-start" />
                    Carregando...
                </Badge>
            </div>
        )
    }

    return (
        <>
        <div className="flex flex-col gap-4">
            <Toaster richColors position="top-center"/>

            {/* Topbar */}
            <div className="flex justify-between items-center mx-9">
                <h1 className="text-white text-xl font-bold">Atendimentos</h1>
                <div className="flex items-center gap-4">
                    <div className="flex gap-2">
                        <button
                            onClick={() => setPeriodo('hoje')}
                            className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors
                                ${periodo === 'hoje'
                                    ? 'bg-orange-700 text-white'
                                    : 'bg-gray-800 text-gray-400 hover:text-white'
                                }`}
                        >
                            Hoje
                        </button>
                        <button
                            onClick={() => setPeriodo('historico')}
                            className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors
                                ${periodo === 'historico'
                                    ? 'bg-red-600 text-white'
                                    : 'bg-gray-800 text-gray-400 hover:text-white'
                                }`}
                        >
                            Histórico
                        </button>
                    </div>
                    <ModalNovoAtendimento onConfirmar={criarAtendimento} />
                </div>
            </div>
        </div>
        <div className="bg-gray-950 p-6">
                <div className="relative overflow-x-auto rounded-xl border border-gray-700">
                    <table className="w-full text-sm text-left text-gray-300">
                        <thead className="text-xs text-gray-400 uppercase bg-gray-800 border-b border-gray-700">
                            <tr>
                                <th scope="col" className="px-6 py-3 font-medium">
                                    CLIENTE
                                </th>
                                <th scope="col" className="px-6 py-3 font-medium">
                                    SERVIÇO
                                </th>
                                <th scope="col" className="px-6 py-3 font-medium">
                                    VALOR
                                </th>
                                <th scope="col" className="px-6 py-3 font-medium">
                                    FORMA DE PAGAMENTO
                                </th>
                                <th scope="col" className="px-6 py-3 font-medium">
                                    DATA
                                </th>
                                <th scope="col" className="px-6 py-3 font-medium">
                                    STATUS
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            {itensPagina.map(items => <tr className="bg-gray-900 border-b border-gray-700 hover:bg-gray-800" key={items.id} onClick={() => items.status !== -1 && setAtendimentoSelecionado(items)}>
                                <th scope="row" className="px-6 py-4 font-medium text-white">{items.descricao}</th>
                                <td className="px-6 py-4">{items.servico}</td>
                                <td className="px-6 py-4 text-orange-500 font-bold">{items.valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}</td>
                                <td className="px-6 py-4">{items.formaPagamento}</td>
                                <td className="px-6 py-4">{items.date}</td>
                                <td className="px-6 py-4">
                                    <span className={items.status === 1 ? 'text-orange-500 font-semibold' : 'text-gray-500'}>
                                        {items.status === 1 ? 'REALIZADO' : 'EXCLUÍDO'}
                                    </span>
                                </td>
                            </tr>)}
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
            </div>
            <div>
            <ModalEditarAtendimento
                atendimento={atendimentoSelecionado}
                open={atendimentoSelecionado !== null}
                onFechar={() => setAtendimentoSelecionado(null)}
                onConfirmar={atualizarAtendimento}
                onDeletar={deletarAtendimento}
            />
            </div>
            </>

    )

}
