import { useEffect, useState } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "../../components/ui/dialog"
import { Input } from "../../components/ui/input"
import { Label } from "../../components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "../../components/ui/select"
import type { DadosProps, AtendimentoFormProps } from "../../types/atendimento"
import { Botao } from "../../components/common/botao"
import { SERVICOS } from "../../utils/servicos"

interface Props {
    atendimento: DadosProps | null
    open: boolean
    onFechar: () => void
    onConfirmar: (id: number, form: AtendimentoFormProps) => void
    onDeletar: (id: number) => void
}

export function ModalEditarAtendimento({ atendimento, open, onFechar, onConfirmar, onDeletar }: Props) {
    const [form, setForm] = useState<AtendimentoFormProps>({
        descricao: "", servico: "", valor: "", formaPagamento: ""
    })

    useEffect(() => {
        if (atendimento) {
            setForm({
                descricao: atendimento.descricao,
                servico: atendimento.servico,
                valor: String(atendimento.valor),
                formaPagamento: atendimento.formaPagamento
            })
        }
    }, [atendimento])

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
            <DialogContent className="bg-gray-900 border-gray-700 text-white w-[calc(100vw-2rem)] max-w-md max-h-[90vh] overflow-y-auto">
                <DialogHeader>
                    <DialogTitle className="text-white">Editar Atendimento</DialogTitle>
                </DialogHeader>
                <div className="flex flex-col gap-4 mt-2">
                    <div>
                        <Label className="text-gray-300">Cliente</Label>
                        <Input className="mt-1 bg-gray-800 border-gray-700 text-white"
                            value={form.descricao}
                            onChange={(e) => setForm({ ...form, descricao: e.target.value })} />
                    </div>
                    <div>
                        <Label className="text-gray-300">Serviço</Label>
                        <Select value={form.servico} onValueChange={(v) => setForm({ ...form, servico: v })}>
                            <SelectTrigger className="mt-1 bg-gray-800 border-gray-700 text-white w-full">
                                <SelectValue placeholder="Selecione o serviço" />
                            </SelectTrigger>
                            <SelectContent className="bg-gray-800 border-gray-700 text-white">
                                {SERVICOS.map(s => <SelectItem key={s} value={s}>{s}</SelectItem>)}
                            </SelectContent>
                        </Select>
                    </div>
                    <div>
                        <Label className="text-gray-300">Valor</Label>
                        <Input className="mt-1 bg-gray-800 border-gray-700 text-white"
                            value={form.valor}
                            onChange={(e) => setForm({ ...form, valor: e.target.value })} />
                    </div>
                    <div>
                        <Label className="text-gray-300">Forma de Pagamento</Label>
                        <Select value={form.formaPagamento} onValueChange={(v) => setForm({ ...form, formaPagamento: v })}>
                            <SelectTrigger className="mt-1 bg-gray-800 border-gray-700 text-white">
                                <SelectValue placeholder="Selecione" />
                            </SelectTrigger>
                            <SelectContent className="bg-gray-800 border-gray-700 text-white">
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
