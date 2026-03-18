import { useState } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "../../components/ui/dialog"
import { Button } from "../../components/ui/button"
import { Input } from "../../components/ui/input"
import { Label } from "../../components/ui/label"
import type { AtendimentoFormProps } from "../../types/atendimento"
import { Botao } from "../../components/common/botao"
import { ServicosMultiSelect } from "../../components/common/servicos-multiselect-component"
import { PagamentoSelect } from "../../components/common/pagamento-select"
import { useServicos } from "../../hooks/use-servicos"

interface Props {
    onConfirmar: (form: AtendimentoFormProps) => void
}

export function ModalNovoAtendimento({ onConfirmar }: Props) {
    const [open, setOpen] = useState(false)
    const [form, setForm] = useState<AtendimentoFormProps>({
        descricao: "", servico: [], valor: 0, formaPagamento: ""
    })

    const { servicos } = useServicos()

    const calcularTotal = (nomes: string[]): number => {
        return nomes.reduce((acc, nome) => {
            const servico = servicos.find(s => s.servico === nome)
            return servico ? acc + servico.valor : acc
        }, 0)
    }

    const handleServicosChange = (nomes: string[]) => {
        setForm({ ...form, servico: nomes, valor: calcularTotal(nomes) })
    }

    const handleConfirmar = () => {
        if (form.servico.length === 0) return
        onConfirmar(form)
        setOpen(false)
        setForm({ descricao: "", servico: [], valor: 0, formaPagamento: "" })
    }

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
                <Button>NOVO ATENDIMENTO</Button>
            </DialogTrigger>
            <DialogContent className="bg-black border-gray-700 text-white w-[calc(100vw-2rem)] max-w-md max-h-[90vh] overflow-y-auto">
                <DialogHeader>
                    <DialogTitle className="text-white">Novo Atendimento</DialogTitle>
                </DialogHeader>
                <div className="flex flex-col gap-4 mt-2">
                    <div>
                        <Label className="text-gray-300">Cliente</Label>
                        <Input
                            className="mt-2 bg-gray-900 border-gray-700 text-white"
                            placeholder="Nome do cliente"
                            value={form.descricao}
                            onChange={(e) => setForm({ ...form, descricao: e.target.value })}
                        />
                    </div>
                    <div>
                        <Label className="text-gray-300 mb-2">
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
                            className="mt-2 bg-gray-600 border-gray-600 text-orange-400 font-semibold cursor-not-allowed"
                            value={form.servico.length === 0 ? "R$ 0,00" : `R$ ${form.valor.toFixed(2).replace(".", ",")}`}
                        />
                    </div>
                    <div>
                        <Label className="text-gray-300 notranslate">Forma de Pagamento</Label>
                        <PagamentoSelect
                            value={form.formaPagamento}
                            onValueChange={(v) => setForm({ ...form, formaPagamento: v })}
                        />
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