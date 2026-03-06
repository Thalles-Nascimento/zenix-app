import { useState } from "react"
import { useUnidades } from "../hooks/use-unidade"
import { ModalNovaUnidade } from "./modals/unidade-novo-modal"
import { ModalEditarUnidade } from "./modals/unidade-editar-modal"
import { Badge } from "../components/ui/badge"
import { Spinner } from "../components/ui/spinner"
import { Toaster } from "sonner"
import type { UnidadeProps } from "../types/usuario"

export default function UnidadesPage() {
    const { unidades, carregando, criarUnidade, atualizarUnidade, deletarUnidade, reativarUnidade } = useUnidades()
    const [unidadeSelecionada, setUnidadeSelecionada] = useState<UnidadeProps | null>(null)

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
                <h1 className="text-white text-xl font-bold">Unidades</h1>
                <ModalNovaUnidade onConfirmar={criarUnidade} />
            </div>

            {/* Tabela */}
            <div className="overflow-x-auto rounded-xl border border-gray-700">
                <table className="w-full text-sm text-left text-gray-300 min-w-[400px]">
                    <thead className="text-xs text-gray-400 uppercase bg-gray-800 border-b border-gray-700">
                        <tr>
                            <th className="px-4 py-3">UNIDADE</th>
                            <th className="px-4 py-3">ENDEREÇO</th>
                            <th className="px-4 py-3">STATUS</th>
                        </tr>
                    </thead>
                    <tbody>
                        {unidades.length === 0 ? (
                            <tr>
                                <td colSpan={3} className="px-6 py-8 text-center text-gray-500">
                                    Nenhuma unidade cadastrada
                                </td>
                            </tr>
                        ) : (
                            unidades.map(u => (
                                <tr
                                    key={u.id}
                                    className="bg-gray-900 border-b border-gray-700 hover:bg-gray-800 cursor-pointer"
                                    onClick={() => setUnidadeSelecionada(u)}
                                >
                                    <td className="px-4 py-4 font-medium text-white">{u.nomeUnidade}</td>
                                    <td className="px-4 py-4">{u.endereco}</td>
                                    <td className="px-4 py-4">
                                        <span className={u.status === 1 ? "text-orange-500 font-semibold" : "text-gray-500"}>
                                            {u.status === 1 ? "ATIVA" : "INATIVA"}
                                        </span>
                                    </td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>

            <ModalEditarUnidade
                unidade={unidadeSelecionada}
                open={unidadeSelecionada !== null}
                onFechar={() => setUnidadeSelecionada(null)}
                onConfirmar={atualizarUnidade}
                onDeletar={deletarUnidade}
                onReativar={reativarUnidade}
            />
        </>
    )
}
