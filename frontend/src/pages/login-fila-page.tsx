import { useState } from "react"
import { useParams, Navigate } from "react-router-dom"
import { Input } from "../components/ui/input"
import { Button } from "../components/ui/button"
import { Label } from "../components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "../components/ui/select"
import { useBarbeiros } from "../hooks/use-barber"
import { entrarFilaService } from "../services/fila-service"
import { toast, Toaster } from "sonner"
import { SuccessScreen } from "../components/successScreen-component"
import { InputTelefone } from "../components/common/input-telefone"
import { ServicosMultiSelect } from "../components/common/servicos-multiselect-component"

export default function LoginClient() {
    const { unidadeId } = useParams()

    if (!unidadeId || isNaN(Number(unidadeId))) {
        return <Navigate to="/*" />
    }

    const { barbeiros } = useBarbeiros(Number(unidadeId))

    const [nome, setNome] = useState("")
    const [servicos, setServicos] = useState<string[]>([])
    const [formaPagamento, setFormaPagamento] = useState("")
    const [idBarbeiro, setIdBarbeiro] = useState<number | null>(null)
    const [carregando, setCarregando] = useState(false)
    const [sucesso, setSucesso] = useState(false)
    const [telefone, setTelefone] = useState("")

    const entrarFila = async () => {
        if (!nome || servicos.length === 0 || !formaPagamento || !telefone || !idBarbeiro) {
            toast.error("Preencha todos os campos!")
            return
        }

        try {
            setCarregando(true)
            await entrarFilaService({
                nomeCliente: nome,
                servico: servicos,
                formaPagamento,
                telefoneCliente: telefone.replace(/\D/g, ""),
                idBarbeiro
            })
            setSucesso(true)
        } catch (error) {
            toast.error("Erro ao entrar na fila. Tente novamente.")
        } finally {
            setCarregando(false)
        }
    }

    if (sucesso) {
        return <SuccessScreen nome={nome} />
    }

    return (
        <section className="min-h-screen bg-black">
            <Toaster richColors position="top-center" />
            <div className="flex flex-col items-center justify-center px-6 py-8 mx-auto md:h-screen lg:py-0">
                <div className="flex items-center mb-1">
                    <img className="w-48 h-48 mr-2" src="/assets/imagens/LogoWN.png" alt="logo" />
                </div>
                <div className="w-full bg-zinc-900 rounded-xl shadow border md:mt-0 sm:max-w-md xl:p-0 border-gray-500">
                    <div className="p-6 space-y-4 md:space-y-6 sm:p-8">
                        <h1 className="text-xl font-bold leading-tight tracking-tight text-white md:text-2xl">
                            Entre na lista
                        </h1>
                        <form className="space-y-4 md:space-y-6" onSubmit={(e) => e.preventDefault()}>

                            <div>
                                <label className="block mb-2 text-sm font-medium text-gray-300">Seu Nome</label>
                                <Input
                                    type="text"
                                    placeholder="Nome completo"
                                    className="text-white"
                                    value={nome}
                                    onChange={(e) => setNome(e.target.value)}
                                />
                            </div>

                            <div>
                                <Label className="text-gray-300">Telefone</Label>
                                <InputTelefone value={telefone} onChange={setTelefone} />
                            </div>

                            <div>
                                <Label className="text-gray-300">
                                    Serviços
                                    {servicos.length > 0 && (
                                        <span className="ml-2 text-orange-500 text-xs">{servicos.length} selecionado(s)</span>
                                    )}
                                </Label>
                                <ServicosMultiSelect selecionados={servicos} onChange={setServicos} />
                            </div>

                            <div>
                                <Label className="text-gray-300">Forma de Pagamento</Label>
                                <Select onValueChange={setFormaPagamento}>
                                    <SelectTrigger className="mt-2 border-gray-300 text-white w-full">
                                        <SelectValue placeholder="Selecione" />
                                    </SelectTrigger>
                                    <SelectContent className="border-gray-300 text-white">
                                        <SelectItem value="DINHEIRO">Dinheiro</SelectItem>
                                        <SelectItem value="PIX">Pix</SelectItem>
                                        <SelectItem value="CARTAO">Cartão</SelectItem>
                                    </SelectContent>
                                </Select>
                            </div>

                            <div>
                                <Label className="text-gray-300">Barbeiro</Label>
                                <Select onValueChange={(value) => setIdBarbeiro(Number(value))}>
                                    <SelectTrigger className="mt-2 border-gray-300 text-white w-full">
                                        <SelectValue placeholder="Selecione o barbeiro" />
                                    </SelectTrigger>
                                    <SelectContent className="bg-black border-gray-300 text-white">
                                        {barbeiros.map(b => (
                                            <SelectItem key={b.id} value={String(b.id)}>
                                                {b.nome}
                                            </SelectItem>
                                        ))}
                                    </SelectContent>
                                </Select>
                            </div>

                            <Button
                                type="button"
                                variant="default"
                                className="w-full"
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
