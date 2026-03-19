import { useState } from "react"
import { usePlanos } from "../hooks/use-planos"
import { Badge } from "../components/ui/badge"
import { Spinner } from "../components/ui/spinner"
import { Toaster, toast } from "sonner"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "../components/ui/dialog"
import { Label } from "../components/ui/label"
import { Input } from "../components/ui/input"
import { Botao } from "../components/common/botao"
import { ServicosMultiSelect } from "@/components/common/servicos-multiselect-component"

interface FormPlano {
    planoDescricao: string
    valor: string
    servico: string[]
    limiteAtendimentos: string
}

export default function PlanosPage() {
    const { planos, carregando, criarPlano, atualizarPlano, deletarPlano } = usePlanos()

    const [modalAberto, setModalAberto] = useState(false)
    const [editando, setEditando] = useState<{ id: number } | null>(null)
    const [form, setForm] = useState<FormPlano>({ planoDescricao: "", valor: "", servico: [], limiteAtendimentos: "" })

    const formatBRL = (valor: number) =>
        valor.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })

    const abrirNovo = () => {
        setEditando(null)
        setForm({ planoDescricao: "", valor: "", servico: [], limiteAtendimentos: "" })
        setModalAberto(true)
    }

    const abrirEdicao = (id: number, planoDescricao: string, valor: number, servico: string[], limiteAtendimentos: number) => {
        setEditando({ id })
        setForm({ planoDescricao, valor: String(valor), servico: servico ?? [], limiteAtendimentos: String(limiteAtendimentos) })
        setModalAberto(true)
    }

    const fecharModal = () => {
        setModalAberto(false)
        setEditando(null)
        setForm({ planoDescricao: "", valor: "", servico: [], limiteAtendimentos: "" })
    }

    const handleServicosChange = (servicos: string[]) => {
        setForm({ ...form, servico: servicos })
    }

    const handleSalvar = async () => {
        if (!form.planoDescricao.trim() || !form.valor || !form.limiteAtendimentos) {
            toast.error("Preencha todos os campos!")
            return
        }

        const valor = parseFloat(form.valor.replace(",", "."))
        const limite = parseInt(form.limiteAtendimentos)

        if (isNaN(valor) || valor <= 0) { toast.error("Valor inválido!"); return }
        if (isNaN(limite) || limite <= 0) { toast.error("Limite inválido!"); return }

        try {
            if (editando) {
                await atualizarPlano(editando.id, form.planoDescricao.trim(), valor, form.servico, limite)
                toast.success("Plano atualizado!")
            } else {
                await criarPlano(form.planoDescricao.trim(), valor, form.servico, limite)
                toast.success("Plano criado!")
            }
            fecharModal()
        } catch {
            toast.error("Erro ao salvar plano.")
        }
    }

    const handleDeletar = async (id: number) => {
        try {
            await deletarPlano(id)
            toast.success("Plano excluído!")
        } catch {
            toast.error("Erro ao excluir plano.")
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
                <h1 className="text-white text-xl font-bold">Planos</h1>
                <button
                    onClick={abrirNovo}
                    className="bg-orange-600 hover:bg-orange-500 text-white text-sm font-bold px-4 py-2 rounded-lg transition-colors"
                >
                    + Novo Plano
                </button>
            </div>

            <div className="bg-black rounded-xl border border-gray-700 notranslate">
                <div className="overflow-x-auto rounded-xl notranslate">
                    <table className="w-full text-sm text-left text-gray-300 notranslate">
                        <thead className="text-xs text-white uppercase bg-gray-800 border-b border-gray-700 notranslate">
                            <tr>
                                <th className="px-4 py-3 w-1/4 notranslate">PLANO</th>
                                <th className="px-4 py-3 w-1/4 notranslate">SERVIÇOS</th>
                                <th className="px-4 py-3 w-1/6 notranslate">VALOR/MÊS</th>
                                <th className="px-4 py-3 w-1/6 notranslate">LIMITE</th>
                                <th className="px-4 py-3 w-1/6 notranslate">AÇÕES</th>
                            </tr>
                        </thead>
                        <tbody>
                            {planos.length === 0 ? (
                                <tr>
                                    <td colSpan={5} className="px-6 py-8 text-center text-gray-500 notranslate">
                                        Nenhum plano cadastrado
                                    </td>
                                </tr>
                            ) : (
                                planos.map(item => (
                                    <tr key={item.id} className="bg-black border-b border-gray-700 notranslate">
                                        <td className="px-4 py-4 notranslate font-medium text-white">{item.planoDescricao}</td>
                                        <td className="px-4 py-4 font-medium text-white notranslate">
                                            {item.servico?.length > 0
                                                ? item.servico.join(" + ")
                                                : <span className="text-gray-500">—</span>
                                            }
                                        </td>
                                        <td className="px-4 py-4 notranslate text-orange-500 font-bold">{formatBRL(item.valor)}</td>
                                        <td className="px-4 py-4 notranslate text-gray-300">{item.limiteAtendimentos} atend./mês</td>
                                        <td className="px-4 py-4 notranslate">
                                            <div className="flex gap-2">
                                                <button
                                                    onClick={() => abrirEdicao(item.id, item.planoDescricao, item.valor, item.servico ?? [], item.limiteAtendimentos)}
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

            <Dialog open={modalAberto} onOpenChange={fecharModal}>
                <DialogContent className="bg-black border-gray-700 text-white w-[calc(100vw-2rem)] max-w-md notranslate">
                    <DialogHeader>
                        <DialogTitle className="text-white notranslate">
                            {editando ? "Editar Plano" : "Novo Plano"}
                        </DialogTitle>
                    </DialogHeader>
                    <div className="flex flex-col gap-4 mt-2 notranslate">
                        <div>
                            <Label className="text-gray-300">Nome do Plano</Label>
                            <Input
                                className="mt-1 bg-gray-900 border-gray-700 text-white"
                                placeholder="Ex: Mensal Corte"
                                value={form.planoDescricao}
                                onChange={(e) => setForm({ ...form, planoDescricao: e.target.value })}
                            />
                        </div>
                        <div>
                            <Label className="text-gray-300">Valor Mensal (R$)</Label>
                            <Input
                                className="mt-1 bg-gray-900 border-gray-700 text-orange-400 font-semibold"
                                placeholder="Ex: 89.90"
                                value={form.valor}
                                onChange={(e) => setForm({ ...form, valor: e.target.value })}
                            />
                        </div>
                        <div>
                            <Label className="text-gray-300">
                                Serviços do Plano
                                {form.servico.length > 0 && (
                                    <span className="ml-2 text-orange-500 text-xs">
                                        {form.servico.length} selecionado(s)
                                    </span>
                                )}
                            </Label>
                            <ServicosMultiSelect
                                selecionados={form.servico}
                                onChange={handleServicosChange}
                            />
                        </div>
                        <div>
                            <Label className="text-gray-300">Limite de Atendimentos/Mês</Label>
                            <Input
                                className="mt-1 bg-gray-900 border-gray-700 text-white"
                                placeholder="Ex: 4"
                                type="number"
                                value={form.limiteAtendimentos}
                                onChange={(e) => setForm({ ...form, limiteAtendimentos: e.target.value })}
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