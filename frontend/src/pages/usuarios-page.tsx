import { useUsuarios } from "../hooks/use-usuarios"
import { Spinner } from "../components/ui/spinner"
import { Badge } from "../components/ui/badge"
import { ModalEditarUsuario } from "./modals/usuario-editar-modal"
import { useState } from "react"
import { Toaster } from "sonner"
import type { UserProps } from "../types/usuario"
import { ModalNovoUsuario } from "./modals/usuario-novo-modal"
import { formatarCPF } from "../utils/formatter"
import { Paginacao } from "../components/pagination"
import { usePaginacao } from "../hooks/use-pagination"

export default function UsersPage() {
    const { dados, carregando, filtro, setFiltro, criarUsuario, atualizarUsuario, deletarUsuario, reativarUsuario } = useUsuarios()
    const [usuarioSelecionado, setUsuarioSelecionado] = useState<UserProps | null>(null)
    const { itensPagina, paginaAtual, totalPaginas, totalItens, setPaginaAtual } = usePaginacao(dados, 7)

    const grupoConfig: Record<string, { label: string; className: string }> = {
        ADMIN: { label: "Admin",    className: "text-orange-500 font-semibold" },
        USER:  { label: "Barbeiro", className: "text-blue-400 font-semibold" },
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
            <Toaster richColors position="top-right" />

            {/* Topbar */}
            <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-4 notranslate">
                <h1 className="text-white text-xl font-bold">Usuários</h1>
                <div className="flex flex-wrap items-center gap-2">
                    <button
                        onClick={() => setFiltro("ativos")}
                        className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors
                            ${filtro === "ativos" ? "bg-orange-700 text-white" : "bg-gray-800 text-gray-400 hover:text-white"}`}
                    >
                        Ativos
                    </button>
                    <button
                        onClick={() => setFiltro("todos")}
                        className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors
                            ${filtro === "todos" ? "bg-gray-600 text-white" : "bg-gray-800 text-gray-400 hover:text-white"}`}
                    >
                        Todos
                    </button>
                    <ModalNovoUsuario onConfirmar={criarUsuario} />
                </div>
            </div>

            {/* Tabela */}
            <div className="overflow-x-auto rounded-xl border border-gray-700 notranslate">
                <table className="w-full text-sm text-left text-gray-300 min-w-[700px] notranslate">
                    <thead className="text-xs text-gray-400 uppercase bg-gray-800 border-b border-gray-700 notranslate">
                        <tr>
                            <th className="px-4 py-3 notranslate">USUÁRIO</th>
                            <th className="px-4 py-3 notranslate">E-MAIL</th>
                            <th className="px-4 py-3 notranslate">CPF</th>
                            <th className="px-4 py-3 notranslate">PERMISSÕES</th>
                            <th className="px-4 py-3 notranslate">UNIDADE</th>
                            <th className="px-4 py-3 notranslate">STATUS</th>
                        </tr>
                    </thead>
                    <tbody>
                        {itensPagina.map(items => (
                            <tr
                                key={items.id}
                                className="bg-gray-900 border-b border-gray-700 hover:bg-gray-800 cursor-pointer notranslate"
                                onClick={() => setUsuarioSelecionado(items)}
                            >
                                <td className="px-4 py-4 notranslate font-medium text-white">{items.nome}</td>
                                <td className="px-4 py-4 notranslate">{items.email}</td>
                                <td className="px-4 py-4 notranslate">{formatarCPF(items.cpf)}</td>
                                <td className="px-4 py-4 notranslate">
                                    <span className={grupoConfig[items.grupo]?.className ?? ""}>
                                        {grupoConfig[items.grupo]?.label ?? items.grupo}
                                    </span>
                                </td>
                                <td className="px-4 py-4 notranslate">{items.unidade?.nomeUnidade ?? '--'}</td>
                                <td className="px-4 py-4 notranslate">
                                    <span className={items.status === 1 ? "text-orange-500 font-semibold" : "text-gray-500"}>
                                        {items.status === 1 ? "ATIVO" : "EXCLUÍDO"}
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

            <ModalEditarUsuario
                usuario={usuarioSelecionado}
                open={usuarioSelecionado !== null}
                onFechar={() => setUsuarioSelecionado(null)}
                onConfirmar={atualizarUsuario}
                onDeletar={deletarUsuario}
                onReativar={reativarUsuario}
            />
        </>
    )
}
