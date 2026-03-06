import { useUsuarios } from "../hooks/use-usuarios";
import { Spinner } from "../components/ui/spinner";
import { Badge } from "../components/ui/badge"
import { ModalEditarUsuario } from "./modals/usuario-editar-modal";
import { useState } from "react";
import { Toaster } from "sonner"
import type { UserProps } from "../types/usuario";
import { ModalNovoUsuario } from "./modals/usuario-novo-modal";
import { formatarCPF } from "../utils/formatter"
import { Paginacao } from "../components/pagination";
import { usePaginacao } from "../hooks/use-pagination";



export default function UsersPage(){
    const { dados, carregando, criarUsuario, atualizarUsuario, deletarUsuario, reativarUsuario } = useUsuarios()
    const [usuarioSelecionado, setUsuarioSelecionado] = useState<UserProps | null>(null)
    const { itensPagina, paginaAtual, totalPaginas, totalItens, setPaginaAtual } = usePaginacao(dados, 7)

    const grupoConfig: Record<string, { label: string, className: string }> = {
        ADMIN: { label: 'Admin',    className: 'text-orange-500 font-semibold' },
        USER:  { label: 'Barbeiro', className: 'text-blue-400 font-semibold' }
    }

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
                <Toaster richColors position="top-right" />

                <div className="flex justify-between items-center mx-9">
                    <h1 className="text-white text-xl font-bold">Usuários</h1>
                    <ModalNovoUsuario onConfirmar={criarUsuario} />
                </div>
            </div>
            <div className="bg-gray-950 p-6">
                <div className="relative overflow-x-auto rounded-xl border border-gray-700">
                    <table className="w-full text-sm text-left text-gray-300">
                        <thead className="text-xs text-gray-400 uppercase bg-gray-800 border-b border-gray-700">
                            <tr>
                                <th scope="col" className="px-6 py-3 font-medium">
                                    USUÁRIO
                                </th>
                                <th scope="col" className="px-6 py-3 font-medium">
                                    E-MAIL
                                </th>
                                <th scope="col" className="px-6 py-3 font-medium">
                                    CPF
                                </th>
                                <th scope="col" className="px-6 py-3 font-medium">
                                    PERMISSÕES
                                </th>
                                <th scope="col" className="px-6 py-3 font-medium">
                                    UNIDADE
                                </th>
                                <th scope="col" className="px-6 py-3 font-medium">
                                    STATUS
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            {itensPagina.map(items => <tr className="bg-gray-900 border-b border-gray-700 hover:bg-gray-800" key={items.id} onClick={() => setUsuarioSelecionado(items)}>
                                <th scope="row" className="px-6 py-4 font-medium text-white">{items.nome}</th>
                                <td className="px-6 py-4">{items.email}</td>
                                <td className="px-6 py-4">{formatarCPF(items.cpf)}</td>
                                <td className="px-6 py-4">
                                    <span className={grupoConfig[items.grupo]?.className ?? ''}>
                                        {grupoConfig[items.grupo]?.label ?? items.grupo}
                                    </span>
                                </td>
                                <td className="px-6 py-4">{items.unidade.nomeUnidade}</td>
                                <td className="px-6 py-4">
                                    <span className={items.status === 1 ? 'text-orange-500 font-semibold' : 'text-gray-500'}>
                                        {items.status === 1 ? 'ATIVO' : 'EXCLUÍDO'}
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
                <ModalEditarUsuario
                    usuario={usuarioSelecionado}
                    open={usuarioSelecionado !== null}
                    onFechar={() => setUsuarioSelecionado(null)}
                    onConfirmar={atualizarUsuario}
                    onDeletar={deletarUsuario}
                    onReativar={reativarUsuario}
                />
            </div>
        </>

    )
}