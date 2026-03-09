import { useState } from "react"
import { trace, SpanStatusCode } from "@opentelemetry/api"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "../../components/ui/dialog"
import { Button } from "../../components/ui/button"
import { Input } from "../../components/ui/input"
import { Label } from "../../components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "../../components/ui/select"
import type { UsuarioFormProps } from "../../types/usuario"
import { Botao } from "../../components/common/botao"
import { formatarCPF, limparCPF } from "../../utils/formatter"
import { validarSenha } from "../../utils/validators"
import { useUnidades } from "../../hooks/use-unidades"
import { ErrorBoundary } from "../../components/common/error-boundary"

interface Props {
    onConfirmar: (form: UsuarioFormProps) => void
}

const tracer = trace.getTracer("zenix-frontend")
const formInicial: UsuarioFormProps = { nome: "", email: "", cpf: "", unidade: 0, senha: "", grupo: "" }

// Componente interno do select de unidade — isolado no ErrorBoundary
function SelectUnidade({ value, onChange }: { value: number, onChange: (v: number) => void }) {
    const { unidades, loading, error } = useUnidades()

    const span = tracer.startSpan("select.unidade.render")
    span.setAttribute("unidades.count", unidades?.length ?? 0)
    span.setAttribute("unidades.loading", loading ?? false)
    span.setAttribute("unidades.error", error ?? "none")
    span.end()

    if (loading) {
        return <p className="text-gray-400 text-xs mt-1">Carregando unidades...</p>
    }

    if (error) {
        return <p className="text-red-500 text-xs mt-1">Erro ao carregar unidades: {error}</p>
    }

    if (!unidades || unidades.length === 0) {
        return <p className="text-yellow-500 text-xs mt-1">Nenhuma unidade disponível.</p>
    }

    return (
        // FIX MOBILE: container={document.body} evita problema do Portal dentro do Dialog em mobile
        <Select
            value={value ? String(value) : ""}
            onValueChange={(v) => {
                const span = tracer.startSpan("select.unidade.change")
                try {
                    const parsed = Number(v)
                    if (isNaN(parsed) || parsed === 0) {
                        throw new Error(`Valor inválido recebido: "${v}"`)
                    }
                    span.setAttribute("unidade.id", parsed)
                    span.setAttribute("unidade.nome", unidades.find(u => u.id === parsed)?.nomeUnidade ?? "desconhecida")
                    onChange(parsed)
                    span.setStatus({ code: SpanStatusCode.OK })
                } catch (err) {
                    const error = err as Error
                    span.recordException(error)
                    span.setStatus({ code: SpanStatusCode.ERROR, message: error.message })
                    console.error("[SelectUnidade] Erro ao processar seleção:", error)
                } finally {
                    span.end()
                }
            }}
        >
            <SelectTrigger className="mt-1 w-full bg-gray-800 border-gray-700 text-white">
                <SelectValue placeholder="Selecione" />
            </SelectTrigger>
            {/* FIX MOBILE: position="popper" é mais estável em mobile dentro de Dialog */}
            <SelectContent
                className="bg-gray-800 border-gray-700 text-white"
                position="popper"
            >
                {unidades.map(u => (
                    <SelectItem key={u.id} value={String(u.id)}>
                        {u.nomeUnidade}
                    </SelectItem>
                ))}
            </SelectContent>
        </Select>
    )
}

export function ModalNovoUsuario({ onConfirmar }: Props) {
    const [open, setOpen] = useState(false)
    const [form, setForm] = useState<UsuarioFormProps>(formInicial)
    const [cpfFormatado, setCpfFormatado] = useState("")
    const [erro, setErro] = useState<string | null>(null)

    const handleConfirmar = () => {
        const span = tracer.startSpan("modal.novo-usuario.confirmar")
        try {
            span.setAttribute("form.nome.preenchido", !!form.nome)
            span.setAttribute("form.email.preenchido", !!form.email)
            span.setAttribute("form.cpf.preenchido", !!form.cpf)
            span.setAttribute("form.unidade.id", form.unidade)
            span.setAttribute("form.grupo", form.grupo)

            if (!validarSenha(form.senha)) {
                setErro("A senha deve ter no mínimo 6 caracteres.")
                span.setAttribute("validacao.erro", "senha_invalida")
                span.setStatus({ code: SpanStatusCode.ERROR, message: "Senha inválida" })
                return
            }

            onConfirmar(form)
            setOpen(false)
            setForm(formInicial)
            span.setStatus({ code: SpanStatusCode.OK })
        } catch (err) {
            const error = err as Error
            span.recordException(error)
            span.setStatus({ code: SpanStatusCode.ERROR, message: error.message })
            console.error("[ModalNovoUsuario] Erro ao confirmar:", error)
        } finally {
            span.end()
        }
    }

    const handleCPF = (e: React.ChangeEvent<HTMLInputElement>) => {
        const formatado = formatarCPF(e.target.value)
        setCpfFormatado(formatado)
        setForm({ ...form, cpf: limparCPF(formatado) })
    }

    return (
        <Dialog open={open} onOpenChange={(isOpen) => {
            const span = tracer.startSpan("modal.novo-usuario.toggle")
            span.setAttribute("modal.open", isOpen)
            span.end()
            setOpen(isOpen)
            if (!isOpen) setForm(formInicial)
        }}>
            <DialogTrigger asChild>
                <Button>NOVO USUÁRIO</Button>
            </DialogTrigger>
            <DialogContent className="bg-gray-900 border-gray-700 text-white w-[calc(100vw-2rem)] max-w-md max-h-[90vh] overflow-y-auto">
                <DialogHeader>
                    <DialogTitle className="text-white">Novo Usuário</DialogTitle>
                </DialogHeader>
                <div className="flex flex-col gap-4 mt-2">
                    <div>
                        <Label className="text-gray-300">Nome</Label>
                        <Input className="mt-1 bg-gray-800 border-gray-700 text-white" placeholder="Nome completo"
                            value={form.nome} onChange={(e) => setForm({ ...form, nome: e.target.value })} />
                    </div>
                    <div>
                        <Label className="text-gray-300">E-mail</Label>
                        <Input className="mt-1 bg-gray-800 border-gray-700 text-white" type="email" placeholder="email@exemplo.com"
                            value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
                    </div>
                    <div>
                        <Label className="text-gray-300">CPF</Label>
                        <Input className="mt-1 bg-gray-800 border-gray-700 text-white" placeholder="000.000.000-00"
                            value={cpfFormatado} onChange={handleCPF} maxLength={14} />
                    </div>
                    <div>
                        <Label className="text-gray-300">Senha</Label>
                        <Input className="mt-1 bg-gray-800 border-gray-700 text-white" type="password" placeholder="********"
                            value={form.senha} onChange={(e) => setForm({ ...form, senha: e.target.value })} />
                        {erro && <p className="text-red-500 text-xs mt-1">{erro}</p>}
                    </div>
                    <div>
                        <Label className="text-gray-300">Unidade</Label>
                        {/* ErrorBoundary isola crash do SelectUnidade — evita tela branca */}
                        <ErrorBoundary
                            componentName="SelectUnidade"
                            fallback={
                                <p className="text-red-500 text-xs mt-1">
                                    Erro ao carregar unidades. Feche e tente novamente.
                                </p>
                            }
                        >
                            <SelectUnidade
                                value={form.unidade}
                                onChange={(v) => setForm({ ...form, unidade: v })}
                            />
                        </ErrorBoundary>
                    </div>
                    <div>
                        <Label className="text-gray-300">Usuário</Label>
                        <Select onValueChange={(v: "ADMIN" | "USER") => setForm({ ...form, grupo: v })}>
                            <SelectTrigger className="mt-1 w-full bg-gray-800 border-gray-700 text-white">
                                <SelectValue placeholder="Selecione" />
                            </SelectTrigger>
                            <SelectContent className="bg-gray-800 border-gray-700 text-white" position="popper">
                                <SelectItem value="ADMIN">Administrador</SelectItem>
                                <SelectItem value="USER">Barbeiro</SelectItem>
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