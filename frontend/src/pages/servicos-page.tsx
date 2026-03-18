import { useState } from "react"
import { useServicos } from "../hooks/use-servicos"
import { Badge } from "../components/ui/badge"
import { Spinner } from "../components/ui/spinner"
import { Toaster, toast } from "sonner"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "../components/ui/dialog"
import { Label } from "../components/ui/label"
import { Input } from "../components/ui/input"
import { Botao } from "../components/common/botao"

interface FormServico {
    servico: string
    valor: string
}

export default function ServicosPage() {
    const { servicos, carregando, criarServico, atualizarServico, deletarServico } = useServicos()

    const [modalAberto, setModalAberto] = useState(false)
    const [editando, setEditando] = useState<{ id: number } | null>(null)
    const [form, setForm] = useState<FormServico>({ servico: "", valor: "" })

    const formatBRL = (valor: number) =>
        valor.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })

    const abrirNovo = () => {
        setEditando(null)
        setForm({ servico: "", valor: "" })
        setModalAberto(true)
    }

    const abrirEdicao = (id: number, nome: string, valor: number) => {
        setEditando({ id })
        setForm({ servico: nome, valor: String(valor) })
        setModalAberto(true)
    }

    const fecharModal = () => {
        setModalAberto(false)
        setEditando(null)
        setForm({ servico: "", valor: "" })
    }

    const handleSalvar = async () => {
        if (!form.servico.trim() || !form.valor) {
            toast.error("Preencha todos os campos!")
            return
        }

        const valor = parseFloat(form.valor.replace(",", "."))
        if (isNaN(valor) || valor <= 0) {
            toast.error("Valor inválido!")
            return
        }

        try {
            if (editando) {
                await atualizarServico(editando.id, form.servico.trim(), valor)
                toast.success("Serviço atualizado!")
            } else {
                await criarServico(form.servico.trim(), valor)
                toast.success("Serviço criado!")
            }
            fecharModal()
        } catch {
            toast.error("Erro ao salvar serviço.")
        }
    }

    const handleDeletar = async (id: number) => {
        try {
            await deletarServico(id)
            toast.success("Serviço excluído!")
        } catch {
            toast.error("Erro ao excluir serviço.")
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
                <h1 className="text-white text-xl font-bold">Serviços</h1>
                <button
                    onClick={abrirNovo}
                    className="bg-orange-600 hover:bg-orange-500 text-white text-sm font-bold px-4 py-2 rounded-lg transition-colors"
                >
                    + Novo Serviço
                </button>
            </div>

            {/* Tabela */}
            <div className="bg-black rounded-xl border border-gray-700 notranslate">
                <div className="overflow-x-auto rounded-xl notranslate">
                    <table className="w-full text-sm text-left text-gray-300 min-w-[400px] notranslate">
                        <thead className="text-xs text-white uppercase bg-gray-800 border-b border-gray-700 notranslate">
                            <tr>
                                <th className="px-4 py-3 notranslate">SERVIÇO</th>
                                <th className="px-4 py-3 notranslate">VALOR</th>
                                <th className="px-4 py-3 notranslate">AÇÕES</th>
                            </tr>
                        </thead>
                        <tbody>
                            {servicos.length === 0 ? (
                                <tr>
                                    <td colSpan={3} className="px-6 py-8 text-center text-gray-500 notranslate">
                                        Nenhum serviço cadastrado
                                    </td>
                                </tr>
                            ) : (
                                servicos.map(item => (
                                    <tr
                                        key={item.id}
                                        className="bg-black border-b border-gray-700 notranslate"
                                    >
                                        <td className="px-4 py-4 notranslate font-medium text-white">{item.servico}</td>
                                        <td className="px-4 py-4 notranslate text-orange-500 font-bold">{formatBRL(item.valor)}</td>
                                        <td className="px-4 py-4 notranslate">
                                            <div className="flex gap-2">
                                                <button
                                                    onClick={() => abrirEdicao(item.id, item.servico, item.valor)}
                                                    className="bg-gray-700 hover:bg-gray-600 text-white text-xs font-bold px-3 py-1.5 rounded-lg transition-colors"
                                                >
                                                    Editar
                                                </button>
                                                <button
                                                    onClick={() => handleDeletar(item.id)}
                                                    className="bg-red-700 hover:bg-red-800 text-white text-xs font-bold px-3 py-1.5 rounded-lg transition-colors"
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
                            {editando ? "Editar Serviço" : "Novo Serviço"}
                        </DialogTitle>
                    </DialogHeader>
                    <div className="flex flex-col gap-4 mt-2 notranslate">
                        <div>
                            <Label className="text-gray-300">Nome do Serviço</Label>
                            <Input
                                className="mt-1 bg-gray-900 border-gray-700 text-white"
                                placeholder="Ex: Corte"
                                value={form.servico}
                                onChange={(e) => setForm({ ...form, servico: e.target.value })}
                            />
                        </div>
                        <div>
                            <Label className="text-gray-300">Valor (R$)</Label>
                            <Input
                                className="mt-1 bg-gray-900 border-gray-700 text-orange-400 font-semibold"
                                placeholder="Ex: 35.00"
                                value={form.valor}
                                onChange={(e) => setForm({ ...form, valor: e.target.value })}
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