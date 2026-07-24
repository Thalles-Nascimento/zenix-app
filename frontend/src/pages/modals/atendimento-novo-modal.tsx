import { useState, useRef, useEffect } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "../../components/ui/dialog"
import { Button } from "../../components/ui/button"
import { Input } from "../../components/ui/input"
import { Label } from "../../components/ui/label"
import type { AtendimentoFormProps } from "../../types/atendimento"
import { Botao } from "../../components/common/botao"
import { ServicosMultiSelect } from "../../components/common/servicos-multiselect-component"
import { PagamentoSelect } from "../../components/common/pagamento-select"
import { useServicos } from "../../hooks/use-servicos"
import { buscarClientesPorNomeService } from "../../services/cliente-service"
import type { ClienteDTO } from "../../types/cliente"
import { useCliente } from "@/hooks/use-cliente"
import { TextareaField } from "@/components/common/textarea"
import { UserPlus } from "lucide-react"

interface Props {
    onConfirmar: (form: AtendimentoFormProps) => void
}

export function ModalNovoAtendimento({ onConfirmar }: Props) {
    const [open, setOpen] = useState(false)
    const [form, setForm] = useState<AtendimentoFormProps>({
        descricao: "", servico: [], valor: 0, formaPagamento: "", observacao: ""
    })
    const [idCliente, setIdCliente] = useState(0)
    const { atualizarRetorno } = useCliente()

    const [sugestoes, setSugestoes] = useState<ClienteDTO[]>([])
    const [buscando, setBuscando] = useState(false)
    const [dropdownAberto, setDropdownAberto] = useState(false)
    const dropdownRef = useRef<HTMLDivElement>(null)
    const timerRef = useRef<ReturnType<typeof setTimeout> | null>(null)

    const { servicos } = useServicos()

    useEffect(() => {
        const handler = (e: MouseEvent) => {
            if (dropdownRef.current && !dropdownRef.current.contains(e.target as Node)) {
                setDropdownAberto(false)
            }
        }
        document.addEventListener("mousedown", handler)
        return () => document.removeEventListener("mousedown", handler)
    }, [])

    const calcularTotal = (nomes: string[]): number => {
        return nomes.reduce((acc, nome) => {
            const servico = servicos.find(s => s.servico === nome)
            return servico ? acc + servico.valor : acc
        }, 0)
    }

    const handleServicosChange = (nomes: string[]) => {
        setForm({ ...form, servico: nomes, valor: calcularTotal(nomes) })
    }

    const handleNomeChange = (valor: string) => {
        setForm({ ...form, descricao: valor })

        if (timerRef.current) clearTimeout(timerRef.current)

        if (valor.trim().length < 2) {
            setSugestoes([])
            setDropdownAberto(false)
            return
        }

        timerRef.current = setTimeout(async () => {
            setBuscando(true)
            const resultado = await buscarClientesPorNomeService(valor)
            setSugestoes(resultado)
            setDropdownAberto(resultado.length > 0)
            setBuscando(false)
        }, 400)
    }

    const selecionarCliente = (cliente: ClienteDTO) => {
        const servicosPlano = cliente.planoId?.servico ?? []
        const valorPlano = cliente.planoId?.valor ?? 0
        const limitePlano = cliente.planoId?.atendimentos ?? 0
        const totalPlano = valorPlano/limitePlano
        setIdCliente(cliente.id)

        setForm({
            ...form,
            descricao: cliente.nome,
            servico: servicosPlano.length > 0 ? servicosPlano : form.servico,
            valor: servicosPlano.length > 0 ? totalPlano : form.valor,
        })

        setSugestoes([])
        setDropdownAberto(false)
    }

    const handleConfirmar = async () => {
        if (form.servico.length === 0) return
        if (idCliente){
            try {
                await atualizarRetorno(idCliente)
            } catch {}
        }
        onConfirmar(form)
        setOpen(false)
        setForm({ descricao: "", servico: [], valor: 0, formaPagamento: "", observacao: "" })
        setSugestoes([])
    }

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
                <Button variant="default"><UserPlus /></Button>
            </DialogTrigger>
            <DialogContent className="notranslate bg-black border border-gray-700 text-white w-[calc(100vw-2rem)] max-w-md max-h-[70vh] overflow-y-auto rounded-lg shadow-sm">
                <div className="px-4 py-3 border-b border-gray-800">
                    <DialogHeader>
                        <DialogTitle className="text-white notranslate text-lg font-semibold">
                            Novo Atendimento
                        </DialogTitle>
                    </DialogHeader>
                </div>
                <div className="p-4 flex flex-col gap-3 notranslate">

                    {/* Campo cliente com sugestões */}
                    <div ref={dropdownRef} className="relative">
                        <Label className="text-white">
                            Cliente
                            {buscando && <span className="ml-2 text-gray-500 text-xs">Buscando...</span>}
                        </Label>
                        <Input
                            className="mt-2 w-full bg-gray-900 border border-gray-700 text-white rounded-md px-3 py-2"
                            placeholder="Digite o nome do cliente"
                            value={form.descricao}
                            onChange={(e) => handleNomeChange(e.target.value)}
                            autoComplete="off"
                        />
                        {dropdownAberto && sugestoes.length > 0 && (
                            <div className="absolute z-50 mt-1 w-full bg-gray-900 border border-gray-700 rounded-lg shadow-lg max-h-48 overflow-y-auto">
                                {sugestoes.map(cliente => (
                                    <button
                                        key={cliente.id}
                                        type="button"
                                        onClick={() => selecionarCliente(cliente)}
                                        className="w-full flex items-center justify-between px-3 py-2.5 text-sm text-left hover:bg-gray-800 transition-colors"
                                    >
                                        <div className="flex flex-col">
                                            <span className="text-white font-medium">{cliente.nome}</span>
                                            {cliente.planoId && (
                                                <span className="text-orange-400 text-xs">
                                                    {cliente.planoId.descricao}
                                                </span>
                                            )}
                                        </div>
                                        <span className="text-gray-500 text-xs">{cliente.retorno} visitas</span>
                                    </button>
                                ))}
                            </div>
                        )}
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

                    <div className="mt-2 flex items-end justify-between gap-4">
                        <div>
                            <Label className="text-white text-sm">Valor</Label>
                            <Input
                                disabled
                                className="mt-1 w-28 bg-gray-600 border border-gray-600 text-orange-400 font-semibold cursor-not-allowed rounded-md px-2 py-1.5 text-sm"
                                value={form.servico.length === 0 ? "" : `R$ ${form.valor.toFixed(2).replace(".", ",")}`}
                                placeholder="R$ 0,00"
                            />
                        </div>
                        <div className="text-right">
                            <Label className="text-white text-sm">Forma de Pagamento</Label>
                            <div className="mt-2 w-48">
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
                        <Botao texto="Salvar" color="sucess" click={handleConfirmar} />
                        <Botao texto="Cancelar" color="cancel" click={() => setOpen(false)} />
                    </div>
                </div>
            </DialogContent>
        </Dialog>
    )
}