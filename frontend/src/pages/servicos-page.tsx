import { useState } from "react"
import { useServicos } from "../hooks/use-servicos"
import { Badge } from "../components/ui/badge"
import { Spinner } from "../components/ui/spinner"
import { Toaster, toast } from "sonner"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "../components/ui/dialog"
import { Label } from "../components/ui/label"
import { Input } from "../components/ui/input"
import { Botao } from "../components/common/botao"
import { ModalConfirmacao } from "@/components/common/modal-confirmacao-component"
import type { ServicoDTO } from "@/types/servico"
import TableLayout from "@/components/common/TableLayout"
import CardItem from "@/components/common/CardItem"
import { usePaginacao } from "../hooks/use-pagination"
import { LayersPlus } from "lucide-react"

interface FormServico {
    servico: string
    valor: string
}

export default function ServicosPage() {
    const { servicos, carregando, criarServico, atualizarServico, deletarServico } = useServicos()

    const [modalAberto, setModalAberto] = useState(false)
    const [editando, setEditando] = useState<{ id: number } | null>(null)
    const [form, setForm] = useState<FormServico>({ servico: "", valor: "" })
    const [servicoSelecionado, setServicoSelecionado] = useState<ServicoDTO | null>(null)
    const [confirmacaoAberta, setConfirmacaoAberta] = useState(false)

    const formatBRL = (valor: number) =>
        valor.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })

    const { itensPagina, paginaAtual, totalPaginas, totalItens, setPaginaAtual } = usePaginacao(servicos, 7)

    const abrirNovo = () => {
        setEditando(null)
        setServicoSelecionado(null)
        setForm({ servico: "", valor: "" })
        setModalAberto(true)
    }

    // now receives the full item so we can keep a reference for deletion inside modal
    const abrirEdicao = (item: ServicoDTO) => {
        setEditando({ id: item.id })
        setServicoSelecionado(item)
        setForm({ servico: item.servico, valor: String(item.valor) })
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
            setConfirmacaoAberta(false)
            // close edit modal if it was open for this service
            if (editando && editando.id === id) {
                fecharModal()
            }
        } catch {
            toast.error("Erro ao excluir serviço.")
        }
    }

    if (carregando) {
        return (
            <div className="w-full bg-black flex items-center justify-center py-20">
                <Badge variant="secondary"><Spinner />Carregando...</Badge>
            </div>
        )
    }

    return (
        <div className="notranslate">
            <Toaster richColors position="top-center" />

            <div className="max-w-[1100px] mx-auto px-4 sm:px-6">
                <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
                    <div>
                        <h1 className="text-white text-2xl font-bold">Serviços</h1>
                    </div>

                    <div className="flex items-center gap-3">
                        <button
                            onClick={abrirNovo}
                            className="bg-orange-600 hover:bg-orange-500 text-white text-sm font-bold px-4 py-2 rounded-lg transition-colors"
                        >
                            <LayersPlus size={18}/>
                        </button>
                    </div>
                </div>

                <TableLayout
                    table={(
                        <table className="w-full text-sm text-left text-white min-w-150 table-fixed md:table">
                            <thead className="uppercase bg-gray-850 border-b border-gray-700">
                                <tr className="text-gray-300">
                                    <th scope="col" className="px-4 py-3">SERVIÇO</th>
                                    <th scope="col" className="px-4 py-3">VALOR</th>
                                </tr>
                            </thead>
                            <tbody>
                                {servicos.length === 0 ? (
                                    <tr>
                                        <td colSpan={2} className="px-6 py-12 text-center text-gray-500 align-middle">Nenhum serviço cadastrado.</td>
                                    </tr>
                                ) : (
                                    itensPagina.map(item => (
                                        <tr
                                            key={item.id}
                                            className={`bg-black border-b border-gray-800 hover:bg-gray-900 transition-colors cursor-pointer`}
                                            onClick={() => abrirEdicao(item)}
                                        >
                                            <td className="px-4 py-4 text-gray-300 truncate max-w-[420px]">{item.servico}</td>
                                            <td className="px-4 py-4 text-orange-500 font-bold">{formatBRL(item.valor)}</td>
                                        </tr>
                                    ))
                                )}
                            </tbody>
                        </table>
                    )}
                    cards={(
                        <>
                            {servicos.length === 0 ? (
                                <div className="px-6 py-12 text-center text-gray-500">Nenhum serviço cadastrado.</div>
                            ) : (
                                itensPagina.map(item => (
                                    <CardItem
                                        key={item.id}
                                        title={item.servico}
                                        rightBottom={<span className="text-sm text-orange-500 font-bold">{formatBRL(item.valor)}</span>}
                                        onClick={() => abrirEdicao(item)}
                                    />
                                ))
                            )}
                        </>
                    )}
                    pagination={{ paginaAtual, totalPaginas, totalItens, itensPorPagina: 7, onPaginaChange: setPaginaAtual }}
                />

                {/* Modal criar/editar - responsive widths and responsive buttons layout */}
                <Dialog open={modalAberto} onOpenChange={fecharModal}>
                    <DialogContent className="bg-black border-gray-700 text-white w-[40vh] max-w-sm mx-auto max-h-[80vh] sm:max-h-[70vh] overflow-y-auto overflow-x-hidden">
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

                            {/* Buttons: on small screens stack vertically, on md+ show inline */}
                            <div className="flex flex-row justify-center gap-2 mt-2">
                                <div>
                                    <Botao compact texto="Salvar" color="sucess" click={handleSalvar} />
                                </div>
                                {editando && servicoSelecionado && (
                                    <div>
                                        <Botao compact texto="Deletar" color="delete" click={() => setConfirmacaoAberta(true)} />
                                        
                                    </div>
                                )}

                                <div>
                                    <Botao compact texto="Cancelar" color="cancel" click={fecharModal} />
                                </div>
                                
                            </div>

                        </div>
                    </DialogContent>
                </Dialog>

                {/* Confirmation modal reused for delete confirmation */}
                <ModalConfirmacao
                    open={confirmacaoAberta}
                    titulo="Excluir Serviço"
                    mensagem={`Deseja excluir o serviço "${servicoSelecionado?.servico}"? Esta ação não pode ser desfeita.`}
                    onConfirmar={() => {
                        if (!servicoSelecionado) return
                        handleDeletar(servicoSelecionado?.id as number)
                    }}
                    onCancelar={() => setConfirmacaoAberta(false)}
                />

            </div>
        </div>
    )
}
