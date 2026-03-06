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
    const { dados, carregando, criarUsuario, atualizarUsuario, deletarUsuario, reativarUsuario } = useUsuarios()
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
            <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-4">
                <h1 className="text-white text-xl font-bold">Usuários</h1>
                <ModalNovoUsuario onConfirmar={criarUsuario} />
            </div>

            {/* Tabela */}
            <div className="overflow-x-auto rounded-xl border border-gray-700">
                <table className="w-full text-sm text-left text-gray-300 min-w-[700px]">
                    <thead className="text-xs text-gray-400 uppercase bg-gray-800 border-b border-gray-700">
                        <tr>
                            <th className="px-4 py-3">USUÁRIO</th>
                            <th className="px-4 py-3">E-MAIL</th>
                            <th className="px-4 py-3">CPF</th>
                            <th className="px-4 py-3">PERMISSÕES</th>
                            <th className="px-4 py-3">UNIDADE</th>
                            <th className="px-4 py-3">STATUS</th>
                        </tr>
                    </thead>
                    <tbody>
                        {itensPagina.map(items => (
                            <tr
                                key={items.id}
                                className="bg-gray-900 border-b border-gray-700 hover:bg-gray-800 cursor-pointer"
                                onClick={() => setUsuarioSelecionado(items)}
                            >
                                <td className="px-4 py-4 font-medium text-white">{items.nome}</td>
                                <td className="px-4 py-4">{items.email}</td>
                                <td className="px-4 py-4">{formatarCPF(items.cpf)}</td>
                                <td className="px-4 py-4">
                                    <span className={grupoConfig[items.grupo]?.className ?? ""}>
                                        {grupoConfig[items.grupo]?.label ?? items.grupo}
                                    </span>
                                </td>
                                <td className="px-4 py-4">{items.unidade?.nomeUnidade ?? '--'}</td>
                                <td className="px-4 py-4">
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
