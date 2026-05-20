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
        const servicosPlano = cliente.plano?.servico ?? []
        const valorPlano = cliente.plano?.valor ?? 0
        const limitePlano = cliente.plano?.limiteAtendimentos ?? 0
        const totalPlano = valorPlano/limitePlano
        setIdCliente(cliente.id)

        setForm({
            ...form,
            descricao: cliente.nomeCliente,
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
                <Button>NOVO ATENDIMENTO</Button>
            </DialogTrigger>
            <DialogContent className="bg-black border-gray-700 text-white w-[calc(100vw-2rem)] max-w-md">
                <DialogHeader>
                    <DialogTitle className="text-white">Novo Atendimento</DialogTitle>
                </DialogHeader>
                <div className="flex flex-col gap-4 mt-2 max-h-[75vh] overflow-y-auto pr-1">

                    {/* Campo cliente com sugestões */}
                    <div ref={dropdownRef} className="relative">
                        <Label className="text-gray-300">
                            Cliente
                            {buscando && <span className="ml-2 text-gray-500 text-xs">Buscando...</span>}
                        </Label>
                        <Input
                            className="mt-2 bg-gray-900 border-gray-700 text-white"
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
                                            <span className="text-white font-medium">{cliente.nomeCliente}</span>
                                            {cliente.plano && (
                                                <span className="text-orange-400 text-xs">
                                                    {cliente.plano.planoDescricao}
                                                </span>
                                            )}
                                        </div>
                                        <span className="text-gray-500 text-xs">{cliente.vezesRetorno} visitas</span>
                                    </button>
                                ))}
                            </div>
                        )}
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
                    <TextareaField
                        value={form.observacao ?? ""}
                        onChange={(e) => setForm({ ...form, observacao: e.target.value })}
                    />
                    <div className="flex gap-3 mt-2">
                        <Botao color="primary" texto="Confirmar" click={handleConfirmar} />
                        <Botao color="secondary" texto="Cancelar" click={() => setOpen(false)} />
                    </div>
                </div>
            </DialogContent>
        </Dialog>
    )
}