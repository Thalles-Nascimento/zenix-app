import { useState } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "../../components/ui/dialog"
import { Button } from "../../components/ui/button"
import { Input } from "../../components/ui/input"
import { Label } from "../../components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "../../components/ui/select"
import type { AtendimentoFormProps } from "../../types/atendimento"
import { Botao } from "../../components/common/botao"
import { ServicosMultiSelect } from "../../components/common/servicos-multiselect-component"

interface Props {
    onConfirmar: (form: AtendimentoFormProps) => void
}

export function ModalNovoAtendimento({ onConfirmar }: Props) {
    const [open, setOpen] = useState(false)
    const [form, setForm] = useState<AtendimentoFormProps>({
        descricao: "", servico: [], valor: "", formaPagamento: ""
    })

    const handleConfirmar = () => {
        if (form.servico.length === 0) return
        onConfirmar(form)
        setOpen(false)
        setForm({ descricao: "", servico: [], valor: "", formaPagamento: "" })
    }

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
                <Button>NOVO ATENDIMENTO</Button>
            </DialogTrigger>
            <DialogContent className="bg-gray-900 border-gray-700 text-white w-[calc(100vw-2rem)] max-w-md max-h-[90vh] overflow-y-auto">
                <DialogHeader>
                    <DialogTitle className="text-white">Novo Atendimento</DialogTitle>
                </DialogHeader>
                <div className="flex flex-col gap-4 mt-2">
                    <div>
                        <Label className="text-gray-300">Cliente</Label>
                        <Input className="mt-1 bg-gray-800 border-gray-700 text-white"
                            placeholder="Nome do cliente"
                            value={form.descricao}
                            onChange={(e) => setForm({ ...form, descricao: e.target.value })} />
                    </div>
                    <div>
                        <Label className="text-gray-300">
                            Serviços
                            {form.servico.length > 0 && (
                                <span className="ml-2 text-orange-500 text-xs">{form.servico.length} selecionado(s)</span>
                            )}
                        </Label>
                        <ServicosMultiSelect
                            selecionados={form.servico}
                            onChange={(v) => setForm({ ...form, servico: v })}
                        />
                    </div>
                    <div>
                        <Label className="text-gray-300">Valor</Label>
                        <Input className="mt-1 bg-gray-800 border-gray-700 text-white"
                            placeholder="0,00"
                            value={form.valor}
                            onChange={(e) => setForm({ ...form, valor: e.target.value })} />
                    </div>
                    <div>
                        <Label className="text-gray-300">Forma de Pagamento</Label>
                        <Select onValueChange={(v) => setForm({ ...form, formaPagamento: v })}>
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
                    <div className="flex gap-3 mt-2">
                        <Botao color="primary" texto="Confirmar" click={handleConfirmar} />
                        <Botao color="secondary" texto="Cancelar" click={() => setOpen(false)} />
                    </div>
                </div>
            </DialogContent>
        </Dialog>
    )
}
