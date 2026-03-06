import { useEffect, useState } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "../../components/ui/dialog"
import { Input } from "../../components/ui/input"
import { Label } from "../../components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "../../components/ui/select"
import type { UserProps, UsuarioFormProps } from "../../types/usuario"
import { Botao } from "../../components/common/botao"
import { formatarCPF, limparCPF } from "../../utils/formatter"
import { validarSenha } from "../../utils/validators"
import { useUnidades } from "../../hooks/use-unidades"

interface Props {
    usuario: UserProps | null
    open: boolean
    onFechar: () => void
    onConfirmar: (id: number, form: UsuarioFormProps) => void
    onDeletar: (id: number) => void
    onReativar: (id: number) => void
}

export function ModalEditarUsuario({ usuario, open, onFechar, onConfirmar, onDeletar, onReativar }: Props) {
    const [form, setForm] = useState<UsuarioFormProps>({ nome: "", email: "", cpf: "", unidade: 0, senha: "", grupo: "" })
    const [cpfFormatado, setCpfFormatado] = useState("")
    const [erro, setErro] = useState<string | null>(null)
    const [reativar, setReativar] = useState(false)
    const { unidades } = useUnidades()

    useEffect(() => {
        if (usuario) {
            setCpfFormatado(formatarCPF(usuario.cpf))
            setReativar(false)
            setForm({
                nome: usuario.nome,
                email: usuario.email,
                cpf: limparCPF(usuario.cpf),
                unidade: usuario.unidade.id,
                senha: "",
                grupo: usuario.grupo
            })
        }
    }, [usuario])

    const handleConfirmar = async () => {
        if (form.senha !== "" && !validarSenha(form.senha)) {
            setErro("A senha deve ter no mínimo 6 caracteres.")
            return
        }
        if (!usuario) return
        if (reativar && usuario.status === -1) {
            await onReativar(usuario.id)
        } else {
            await onConfirmar(usuario.id, form)
        }
        onFechar()
    }

    const handleCPF = (e: React.ChangeEvent<HTMLInputElement>) => {
        const formatado = formatarCPF(e.target.value)
        setCpfFormatado(formatado)
        setForm({ ...form, cpf: limparCPF(formatado) })
    }

    const handleDeletar = () => {
        if (!usuario) return
        onDeletar(usuario.id)
        onFechar()
    }

    return (
        <Dialog open={open} onOpenChange={onFechar}>
            <DialogContent className="bg-gray-900 border-gray-700 text-white w-[calc(100vw-2rem)] max-w-md max-h-[90vh] overflow-y-auto">
                <DialogHeader>
                    <DialogTitle className="text-white">Editar Usuário</DialogTitle>
                </DialogHeader>
                <div className="flex flex-col gap-4 mt-2">
                    <div>
                        <Label className="text-gray-300">Nome</Label>
                        <Input className="mt-1 bg-gray-800 border-gray-700 text-white"
                            value={form.nome} onChange={(e) => setForm({ ...form, nome: e.target.value })} />
                    </div>
                    <div>
                        <Label className="text-gray-300">E-mail</Label>
                        <Input className="mt-1 bg-gray-800 border-gray-700 text-white" type="email"
                            value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
                    </div>
                    <div>
                        <Label className="text-gray-300">CPF</Label>
                        <Input className="mt-1 bg-gray-800 border-gray-700 text-white"
                            value={cpfFormatado} onChange={handleCPF} maxLength={14} placeholder="000.000.000-00" />
                    </div>
                    <div>
                        <Label className="text-gray-300">Nova Senha</Label>
                        <Input className="mt-1 bg-gray-800 border-gray-700 text-white" type="password"
                            placeholder="***************"
                            value={form.senha} onChange={(e) => setForm({ ...form, senha: e.target.value })} />
                        {erro && <p className="text-red-500 text-xs mt-1">{erro}</p>}
                    </div>
                    <div>
                        <Label className="text-gray-300">Usuário</Label>
                        <Select value={form.grupo} onValueChange={(v: "ADMIN" | "USER") => setForm({ ...form, grupo: v })}>
                            <SelectTrigger className="mt-1 w-full bg-gray-800 border-gray-700 text-white">
                                <SelectValue placeholder="Selecione" />
                            </SelectTrigger>
                            <SelectContent className="bg-gray-800 border-gray-700 text-white">
                                <SelectItem value="ADMIN">Administrador</SelectItem>
                                <SelectItem value="USER">Barbeiro</SelectItem>
                            </SelectContent>
                        </Select>
                    </div>
                    <div>
                        <Label className="text-gray-300">Unidade</Label>
                        <Select value={String(form.unidade)} onValueChange={(v) => setForm({ ...form, unidade: Number(v) })}>
                            <SelectTrigger className="mt-1 w-full bg-gray-800 border-gray-700 text-white">
                                <SelectValue placeholder="Selecione a unidade" />
                            </SelectTrigger>
                            <SelectContent className="bg-gray-800 border-gray-700 text-white">
                                {unidades.map(u => (
                                    <SelectItem key={u.id} value={String(u.id)}>{u.nomeUnidade}</SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                    </div>
                    {usuario?.status === -1 && (
                        <div className="flex items-center gap-2 py-1">
                            <input
                                type="checkbox"
                                id="reativar"
                                checked={reativar}
                                className="w-4 h-4 accent-orange-500 cursor-pointer"
                                onChange={(e) => setReativar(e.target.checked)}
                            />
                            <label htmlFor="reativar" className="text-gray-300 text-sm cursor-pointer">
                                Reativar usuário
                            </label>
                        </div>
                    )}
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
