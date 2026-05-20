import { useState } from "react"
import { useParams, Navigate } from "react-router-dom"
import { Input } from "../components/ui/input"
import { Button } from "../components/ui/button"
import { Label } from "../components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "../components/ui/select"
import { useBarbeiros } from "../hooks/use-barber"
import { useCliente } from "../hooks/use-cliente"
import { useServicos } from "../hooks/use-servicos"
import { entrarFilaService } from "../services/fila-service"
import { toast, Toaster } from "sonner"
import { SuccessScreen } from "../components/successScreen-component"
import { InputTelefone } from "../components/common/input-telefone"
import { ServicosMultiSelect } from "../components/common/servicos-multiselect-component"
import { PagamentoSelect } from "@/components/common/pagamento-select"

export default function LoginClient() {
    const { unidadeId } = useParams()

    if (!unidadeId || isNaN(Number(unidadeId))) {
        return <Navigate to="/*" />
    }

    const { barbeiros } = useBarbeiros(Number(unidadeId))
    const { clientes, buscarClientes, criarCliente, buscando, atualizarRetorno } = useCliente()
    const { servicos } = useServicos()

    const [nome, setNome] = useState("")
    const [nomeNovo, setNomeNovo] = useState("")
    const [servicosSelecionados, setServicosSelecionados] = useState<string[]>([])
    const [formaPagamento, setFormaPagamento] = useState("")
    const [idBarbeiro, setIdBarbeiro] = useState<number | null>(null)
    const [semPreferencia, setSemPreferencia] = useState(false)
    const [carregando, setCarregando] = useState(false)
    const [sucesso, setSucesso] = useState(false)
    const [telefone, setTelefone] = useState("")

    const handleTelefoneChange = async (valor: string) => {
        setTelefone(valor)
        setNome("")
        setNomeNovo("")
        await buscarClientes(valor)
    }

    const handleNomeSelecionado = (nomeCliente: string) => {
        setNome(nomeCliente)
        if (nomeCliente === "__novo__") return

        const clienteSelecionado = clientes.find(c => c.nomeCliente === nomeCliente)
        if (clienteSelecionado?.plano?.servico?.length) {
            setServicosSelecionados(clienteSelecionado.plano.servico)
        }
    }

    const handleBarbeiroSelecionado = (value: string) => {
        if (value === "__sem_preferencia__") {
            setIdBarbeiro(null)
            setSemPreferencia(true)
        } else {
            setIdBarbeiro(Number(value))
            setSemPreferencia(false)
        }
    }

    const nomeEfetivo = nome === "__novo__" ? nomeNovo : nome

    const valorTotal = servicosSelecionados.reduce((acc, nome) => {
        const servico = servicos.find(s => s.servico === nome)
        return servico ? acc + servico.valor : acc
    }, 0)

    const entrarFila = async () => {
        if (!nomeEfetivo || servicosSelecionados.length === 0 || !telefone || (!idBarbeiro && !semPreferencia)) {
            toast.error("Preencha todos os campos!")
            return
        }

        if (telefone.length != 13){
            toast.error("Telefone inválido!")
            return
        }

        try {
            setCarregando(true)
            const telefoneNumeros = telefone.replace(/\D/g, "")

            const clienteSelecionado = clientes.find(c => c.nomeCliente === nomeEfetivo)

            if (clienteSelecionado) {
                try {
                    await atualizarRetorno(clienteSelecionado.id)
                } catch {}
            } else {
                await criarCliente(nomeEfetivo, telefoneNumeros)
            }

            await entrarFilaService({
                nomeCliente: nomeEfetivo,
                servico: servicosSelecionados,
                formaPagamento,
                telefoneCliente: telefoneNumeros,
                idBarbeiro: semPreferencia ? null : idBarbeiro,
                semPreferencia,
                idUnidade: Number(unidadeId)
            })
            setSucesso(true)
        } catch (error) {
            toast.error("Erro ao entrar na fila. Tente novamente.")
        } finally {
            setCarregando(false)
        }
    }

    if (sucesso) {
        return <SuccessScreen nome={nomeEfetivo} />
    }

    return (
        <section className="min-h-screen bg-black notranslate">
            <Toaster richColors position="top-center" />
            <div className="flex flex-col items-center justify-center px-6 py-8 mx-auto md:h-screen lg:py-0 notranslate">
                <div className=" items-center mb-2 text-4xl font-semibold notranslate">
                    <img className="w-88" src="/assets/imagens/logo.png" alt="logo" />
                </div>
                <div className="w-full bg-black rounded-xl shadow border md:mt-0 sm:max-w-md xl:p-0 border-gray-500 notranslate">
                    <div className="p-6 space-y-4 md:space-y-6 sm:p-8 notranslate">
                        <h1 className="text-xl font-bold leading-tight tracking-tight text-white md:text-2xl">
                            Entre na Fila de Atendimento
                        </h1>
                        <form className="space-y-4 md:space-y-6" onSubmit={(e) => e.preventDefault()}>

                            <div className="notranslate">
                                <Label className="text-white">Telefone</Label>
                                <InputTelefone value={telefone} onChange={handleTelefoneChange} />
                                {buscando && (
                                    <p className="text-gray-400 text-xs mt-1 notranslate">Buscando...</p>
                                )}
                            </div>

                            {clientes.length > 0 ? (
                                <div className="notranslate">
                                    <Label className="text-white">
                                        Selecione um cliente ou insira um novo
                                    </Label>
                                    <Select onValueChange={handleNomeSelecionado}>
                                        <SelectTrigger className="mt-2 bg-gray-900 border-gray-300 text-white w-full notranslate">
                                            <SelectValue placeholder="Selecione seu nome" />
                                        </SelectTrigger>
                                        <SelectContent className="bg-black border-gray-300 text-white notranslate">
                                            {clientes.map(c => (
                                                <SelectItem key={c.id} value={c.nomeCliente}>
                                                    {c.nomeCliente}
                                                    {c.plano && (
                                                        <span className="ml-auto text-primary text-xs shrink-0">
                                                            {c.plano?.planoDescricao}
                                                        </span>
                                                    )}
                                                </SelectItem>
                                            ))}
                                            <SelectItem value="__novo__">Cadastrar novo cliente</SelectItem>
                                        </SelectContent>
                                    </Select>
                                    {nome === "__novo__" && (
                                        <Input
                                            type="text"
                                            placeholder="Digite seu nome"
                                            className="mt-2 bg-gray-900 text-white notranslate"
                                            value={nomeNovo}
                                            onChange={(e) => setNomeNovo(e.target.value)}
                                        />
                                    )}
                                </div>
                            ) : (
                                <div>
                                    <label className="block mb-2 text-sm font-medium text-white">Seu Nome</label>
                                    <Input
                                        type="text"
                                        placeholder="Nome completo"
                                        className="text-white bg-gray-900 notranslate"
                                        value={nome}
                                        onChange={(e) => setNome(e.target.value)}
                                    />
                                </div>
                            )}

                            <div>
                                <Label className="text-white">
                                    Serviços
                                    {servicosSelecionados.length > 0 && (
                                        <span className="ml-2 text-orange-500 text-xs">
                                            {servicosSelecionados.length} selecionado(s) — {valorTotal.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })}
                                        </span>
                                    )}
                                </Label>
                                <ServicosMultiSelect selecionados={servicosSelecionados} onChange={setServicosSelecionados} />
                            </div>

                            <div>
                                <Label className="text-white">Forma de Pagamento</Label>
                                <PagamentoSelect
                                    value={formaPagamento}
                                    onValueChange={setFormaPagamento}
                                    className="mt-2 border-gray-300"
                                />
                            </div>

                            <div>
                                <Label className="text-white">Barbeiro</Label>
                                <Select onValueChange={handleBarbeiroSelecionado}>
                                    <SelectTrigger className="bg-gray-900 mt-2 border-gray-300 text-white w-full notranslate">
                                        <SelectValue placeholder="Selecione o barbeiro" />
                                    </SelectTrigger>
                                    <SelectContent className="bg-black border-gray-300 text-white notranslate">
                                        {barbeiros.map(b => (
                                            <SelectItem key={b.id} value={String(b.id)}>
                                                {b.nome}
                                            </SelectItem>
                                        ))}
                                        <SelectItem value="__sem_preferencia__">
                                            Não tenho preferência
                                        </SelectItem>
                                    </SelectContent>
                                </Select>
                            </div>

                            <Button
                                type="button"
                                variant="default"
                                className="w-full text-white notranslate"
                                disabled={carregando}
                                onClick={entrarFila}
                            >
                                {carregando ? "Entrando..." : "Entrar na Fila"}
                            </Button>
                        </form>
                    </div>
                </div>
            </div>
        </section>
    )
}