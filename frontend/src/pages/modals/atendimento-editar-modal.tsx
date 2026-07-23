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
            <DialogContent className="notranslate bg-black border-gray-700 text-white w-[calc(100vw-2rem)] max-w-md max-h-[90vh] overflow-y-auto">
                <DialogHeader>
                    <DialogTitle className="text-white notranslate">
                        Editar Atendimento
                        {deletado && (
                            <span className="ml-2 text-xs text-gray-500 font-bold">— Excluído</span>
                        )}
                    </DialogTitle>
                </DialogHeader>
                <div className="flex flex-col gap-4 mt-2 notranslate">
                    <div>
                        <Label className="text-gray-300">Cliente</Label>
                        <Input
                            className="mt-1 bg-gray-900 border-gray-700 text-white"
                            value={form.descricao}
                            onChange={(e) => setForm({ ...form, descricao: e.target.value })}
                        />
                    </div>
                    <div>
                        <Label className="text-gray-300">
                            Serviços
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
                        <Label className="text-gray-300">Valor Total</Label>
                        <Input
                            className="mt-1 bg-gray-900 border-gray-600 text-orange-400 font-semibold"
                            value={form.valor === 0 ? "" : form.valor}
                            placeholder="R$ 0,00"
                            type="number"
                            onChange={(e) => setForm({ ...form, valor: parseFloat(e.target.value) || 0 })}
                        />
                    </div>
                    <div>
                        <Label className="text-gray-300">Forma de Pagamento</Label>
                        <PagamentoSelect
                            value={form.formaPagamento}
                            onValueChange={(v) => setForm({ ...form, formaPagamento: v })}
                        />
                    </div>
                    <TextareaField
                        value={form.observacao ?? ""}
                        onChange={(e) => setForm({ ...form, observacao: e.target.value })}
                    />
                    <div className="flex flex-col gap-2 mt-2">
                        {!deletado && (
                            <>
                                <Botao texto="Salvar Alterações" color="sucess" click={handleConfirmar} />
                                <Botao texto="Deletar" color="delete" click={handleDeletar} />
                            </>
                        )}
                        {deletado && onReativar && (
                            <Botao texto="Reativar Atendimento" color="sucess" click={handleReativar} />
                        )}
                        <Botao texto="Cancelar" color="cancel" click={onFechar} />
                    </div>
                </div>
            </DialogContent>
        </Dialog>
    )
}