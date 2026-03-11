import { useEffect, useState } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "../../components/ui/dialog"
import { Label } from "../../components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "../../components/ui/select"
import type { DadosProps, AtendimentoFormProps } from "../../types/atendimento"
import { Botao } from "../../components/common/botao"
import { ServicosMultiSelect } from "../../components/common/servicos-multiselect-component"
import { SERVICOS } from "../../utils/servicos"
import { Input } from "@/components/ui/input"

interface Props {
    atendimento: DadosProps | null
    open: boolean
    onFechar: () => void
    onConfirmar: (id: number, form: AtendimentoFormProps) => void
    onDeletar: (id: number) => void
}

const calcularTotal = (servicos: string[]): number => {
    return servicos.reduce((acc, nome) => {
        const servico = SERVICOS.find(s => s.nome === nome)
        if (!servico) return acc
        return acc + parseFloat(servico.valor.replace("R$", "").replace(",", ".").trim())
    }, 0)
}

export function ModalEditarAtendimento({ atendimento, open, onFechar, onConfirmar, onDeletar }: Props) {
    const [form, setForm] = useState<AtendimentoFormProps>({
        descricao: "", servico: [], valor: 0, formaPagamento: ""
    })

    useEffect(() => {
        if (atendimento) {
            const servicos = Array.isArray(atendimento.servico) ? atendimento.servico : [atendimento.servico]
            setForm({
                descricao: atendimento.descricao,
                servico: servicos,
                valor: calcularTotal(servicos),
                formaPagamento: atendimento.formaPagamento
            })
        }
    }, [atendimento])

    const handleServicosChange = (servicos: string[]) => {
        setForm({ ...form, servico: servicos, valor: calcularTotal(servicos) })
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

    return (
        <Dialog open={open} onOpenChange={onFechar}>
            <DialogContent className="notranslate bg-gray-900 border-gray-700 text-white w-[calc(100vw-2rem)] max-w-md max-h-[90vh] overflow-y-auto">
                <DialogHeader>
                    <DialogTitle className="text-white notranslate">Editar Atendimento</DialogTitle>
                </DialogHeader>
                <div className="flex flex-col gap-4 mt-2 notranslate">
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
                            disabled
                            className="mt-1 bg-gray-800 border-gray-600 text-orange-400 font-semibold cursor-not-allowed"
                            value={form.servico.length === 0 ? "R$ 0,00" : `R$ ${form.valor.toFixed(2).replace(".", ",")}`}
                        />
                    </div>
                    <div>
                        <Label className="text-gray-300">Forma de Pagamento</Label>
                        <Select value={form.formaPagamento} onValueChange={(v) => setForm({ ...form, formaPagamento: v })}>
                            <SelectTrigger className="mt-1 bg-gray-800 border-gray-700 text-white notranslate">
                                <SelectValue placeholder="Selecione" />
                            </SelectTrigger>
                            <SelectContent className="bg-gray-800 border-gray-700 text-white notranslate">
                                <SelectItem value="DINHEIRO">Dinheiro</SelectItem>
                                <SelectItem value="PIX">Pix</SelectItem>
                                <SelectItem value="CARTAO">Cartão</SelectItem>
                            </SelectContent>
                        </Select>
                    </div>
                    <div className="flex flex-col gap-2 mt-2">
                        <Botao texto="Salvar Alterações" color="sucess" click={handleConfirmar} />
                        <Botao texto="Deletar" color="delete" click={handleDeletar} />
                        <Botao texto="Cancelar" color="cancel" click={onFechar} />
                    </div>
                </div>
            </DialogContent>
        </Dialog>
    )
}