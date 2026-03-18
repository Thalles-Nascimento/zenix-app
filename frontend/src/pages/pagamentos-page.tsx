import { useState } from "react"
import { usePagamentos } from "../hooks/use-pagamentos"
import { Badge } from "../components/ui/badge"
import { Spinner } from "../components/ui/spinner"
import { Toaster, toast } from "sonner"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "../components/ui/dialog"
import { Label } from "../components/ui/label"
import { Input } from "../components/ui/input"
import { Botao } from "../components/common/botao"

export default function PagamentosPage() {
    const { pagamentos, carregando, criarPagamento, atualizarPagamento, deletarPagamento } = usePagamentos()

    const [modalAberto, setModalAberto] = useState(false)
    const [editando, setEditando] = useState<{ id: number } | null>(null)
    const [descricao, setDescricao] = useState("")

    const abrirNovo = () => {
        setEditando(null)
        setDescricao("")
        setModalAberto(true)
    }

    const abrirEdicao = (id: number, nome: string) => {
        setEditando({ id })
        setDescricao(nome)
        setModalAberto(true)
    }

    const fecharModal = () => {
        setModalAberto(false)
        setEditando(null)
        setDescricao("")
    }

    const handleSalvar = async () => {
        if (!descricao.trim()) {
            toast.error("Preencha o nome da forma de pagamento!")
            return
        }

        try {
            if (editando) {
                await atualizarPagamento(editando.id, descricao.trim())
                toast.success("Forma de pagamento atualizada!")
            } else {
                await criarPagamento(descricao.trim())
                toast.success("Forma de pagamento criada!")
            }
            fecharModal()
        } catch {
            toast.error("Erro ao salvar forma de pagamento.")
        }
    }

    const handleDeletar = async (id: number) => {
        try {
            await deletarPagamento(id)
            toast.success("Forma de pagamento excluída!")
        } catch {
            toast.error("Erro ao excluir forma de pagamento.")
        }
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

            <div className="flex items-center justify-between">
                <h1 className="text-white text-xl font-bold">Forma de Pagamento</h1>
                <button
                    onClick={abrirNovo}
                    className="bg-orange-600 hover:bg-orange-500 text-white text-sm font-bold px-4 py-2 rounded-lg transition-colors"
                >
                    + Nova Forma
                </button>
            </div>

            {/* Tabela */}
            <div className="bg-black rounded-xl border border-gray-700 notranslate">
                <div className="overflow-x-auto rounded-xl notranslate">
                    <table className="w-full text-sm text-left text-gray-300 notranslate">
                        <thead className="text-xs text-white uppercase bg-gray-800 border-b border-gray-700 notranslate">
                            <tr>
                                <th className="px-4 py-3 w-3/4 notranslate">FORMA DE PAGAMENTO</th>
                                <th className="px-4 py-3 w-1/4 notranslate">AÇÕES</th>
                            </tr>
                        </thead>
                        <tbody>
                            {pagamentos.length === 0 ? (
                                <tr>
                                    <td colSpan={2} className="px-6 py-8 text-center text-gray-500 notranslate">
                                        Nenhuma forma de pagamento cadastrada
                                    </td>
                                </tr>
                            ) : (
                                pagamentos.map(item => (
                                    <tr
                                        key={item.id}
                                        className="bg-black border-b border-gray-700 notranslate"
                                    >
                                        <td className="px-4 py-4 notranslate font-medium text-white">{item.formaPagamento}</td>
                                        <td className="px-4 py-4 notranslate">
                                            <div className="flex gap-2">
                                                <button
                                                    onClick={() => abrirEdicao(item.id, item.formaPagamento)}
                                                    className="bg-gray-700 hover:bg-gray-600 text-white text-xs font-bold px-3 py-1.5 rounded-lg transition-colors"
                                                >
                                                    Editar
                                                </button>
                                                <button
                                                    onClick={() => handleDeletar(item.id)}
                                                    className="bg-red-900 hover:bg-red-800 text-white text-xs font-bold px-3 py-1.5 rounded-lg transition-colors"
                                                >
                                                    Excluir
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            </div>

            {/* Modal criar/editar */}
            <Dialog open={modalAberto} onOpenChange={fecharModal}>
                <DialogContent className="bg-black border-gray-700 text-white w-[calc(100vw-2rem)] max-w-md notranslate">
                    <DialogHeader>
                        <DialogTitle className="text-white notranslate">
                            {editando ? "Editar Forma de Pagamento" : "Nova Forma de Pagamento"}
                        </DialogTitle>
                    </DialogHeader>
                    <div className="flex flex-col gap-4 mt-2 notranslate">
                        <div>
                            <Label className="text-gray-300">Descrição</Label>
                            <Input
                                className="mt-1 bg-gray-900 border-gray-700 text-white"
                                placeholder="Ex: PIX"
                                value={descricao}
                                onChange={(e) => setDescricao(e.target.value)}
                            />
                        </div>
                        <div className="flex flex-col gap-2 mt-2">
                            <Botao texto="Salvar" color="sucess" click={handleSalvar} />
                            <Botao texto="Cancelar" color="cancel" click={fecharModal} />
                        </div>
                    </div>
                </DialogContent>
            </Dialog>
        </div>
    )
}
