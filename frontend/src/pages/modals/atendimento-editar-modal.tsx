import { useEffect, useState } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "../../components/ui/dialog"
import { Label } from "../../components/ui/label"
import type { AtendimentoProps, AtendimentoFormProps } from "../../types/atendimento"
import { Botao } from "../../components/common/botao"
import { ServicosMultiSelect } from "../../components/common/servicos-multiselect-component"
import { Input } from "@/components/ui/input"
import { TextareaField } from "@/components/common/textarea"
import { useServicos } from "../../hooks/use-servicos"
import { PagamentoSelect } from "@/components/common/pagamento-select"

interface Props {
    atendimento: AtendimentoProps | null
    open: boolean
    onFechar: () => void
    onConfirmar: (id: string, form: AtendimentoFormProps) => void
    onDeletar: (id: string) => void
    onReativar?: (id: string) => void
}

export function ModalEditarAtendimento({ atendimento, open, onFechar, onConfirmar, onDeletar, onReativar }: Props) {
    const { servicos } = useServicos()

    const [form, setForm] = useState<AtendimentoFormProps>({
        descricao: "", servico: [], valor: 0, formaPagamento: "", observacao: ""
    })

    const deletado = atendimento?.status === -1

    const calcularTotal = (nomes: string[]): number => {
        return nomes.reduce((acc, nome) => {
            const servico = servicos.find(s => s.servico === nome)
            return servico ? acc + servico.valor : acc
        }, 0)
    }

    useEffect(() => {
        if (atendimento) {
            const lista = Array.isArray(atendimento.servico) ? atendimento.servico : [atendimento.servico]
            setForm({
                descricao: atendimento.descricao,
                servico: lista,
                valor: atendimento.valor,
                formaPagamento: atendimento.formaPagamento,
                observacao: atendimento.observacao ?? ""
            })
        }
    }, [atendimento])

    const handleServicosChange = (lista: string[]) => {
        setForm({ ...form, servico: lista, valor: calcularTotal(lista) })
    }

    const handleConfirmar = () => {
        if (!atendimento) return
        onConfirmar(atendimento.id, form)
        onFechar()
    }

    const handleDeletar = () => {
        if (!atendimento) return
        onDeletar(atendimento.id)
        onFechar()
    }

    const handleReativar = () => {
        if (!atendimento || !onReativar) return
        onReativar(atendimento.id)
        onFechar()
    }

    return (
        <Dialog open={open} onOpenChange={onFechar}>
            <DialogContent className="notranslate bg-black border border-gray-700 text-white w-[90vw] max-w-sm mx-auto max-h-[80vh] sm:max-h-[70vh] overflow-y-auto overflow-x-hidden rounded-lg shadow-sm">
                <div className="px-3 py-3 border-b border-gray-800">
                    <DialogHeader>
                        <DialogTitle className="text-white notranslate text-lg font-semibold">
                            Editar Atendimento {deletado && (<span className="ml-2 text-red-400 font-bold">— Deletado</span>)}
                        </DialogTitle>
                    </DialogHeader>
                </div>
                <div className="p-4 flex flex-col gap-3 notranslate">
                    <div>
                        <Label className="text-white">Cliente</Label>
                        <Input
                            className="mt-2 w-full bg-gray-900 border border-gray-700 text-white rounded-md px-3 py-2"
                            value={form.descricao}
                            onChange={(e) => setForm({ ...form, descricao: e.target.value })}
                        />
                    </div>

                    <div>
                        <div className="flex items-center justify-between">
                            <Label className="text-white">Serviços</Label>
                            {form.servico.length > 0 && (
                                <span className="text-orange-500 text-xs">{form.servico.length} selecionado(s)</span>
                            )}
                        </div>
                        <div className="mt-2">
                            <ServicosMultiSelect
                                selecionados={form.servico}
                                onChange={handleServicosChange}
                            />
                        </div>
                    </div>

                    <div className="mt-2 flex flex-col sm:flex-row items-end sm:justify-between gap-4">
                        <div className="w-full sm:w-auto">
                            <Label className="text-white text-sm">Valor</Label>
                            <Input
                                className="mt-1 w-full sm:w-28 bg-gray-900 border border-gray-600 text-orange-400 font-semibold rounded-md px-2 py-1.5 text-sm"
                                value={form.valor === 0 ? "" : String(form.valor)}
                                placeholder="R$ 0,00"
                                type="text"
                                onChange={(e) => setForm({ ...form, valor: parseFloat(e.target.value) || 0 })}
                            />
                        </div>
                        <div className="text-right w-full sm:w-48">
                            <Label className="text-white text-sm">Forma de Pagamento</Label>
                            <div className="mt-2 w-full">
                                <PagamentoSelect
                                    value={form.formaPagamento}
                                    onValueChange={(v) => setForm({ ...form, formaPagamento: v })}
                                />
                            </div>
                        </div>
                    </div>

                    <div>
                        <TextareaField
                            value={form.observacao ?? ""}
                            onChange={(e) => setForm({ ...form, observacao: e.target.value })}
                        />
                    </div>

                    <div className="flex items-center justify-center gap-6 mt-6">
                        {deletado ? (
                            <>
                                {onReativar && <Botao texto="Reativar" color="sucess" click={handleReativar} />}
                                <Botao texto="Fechar" color="cancel" click={onFechar} />
                            </>
                        ) : (
                            <>
                                <div className="flex items-center gap-4">
                                    <Botao texto="Salvar" color="sucess" click={handleConfirmar} />
                                    <Botao texto="Deletar" color="secondary" click={handleDeletar} />
                                </div>
                            </>
                        )}
                    </div>
                </div>
            </DialogContent>
        </Dialog>          
    )}