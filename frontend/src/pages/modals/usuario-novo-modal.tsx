import { useState } from "react"
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


interface Props {
    onConfirmar: (form: UsuarioFormProps) => void
}

const formInicial: UsuarioFormProps = {
    nome: "", email: "", cpf: "", unidade: 0, senha: "", grupo: ""
}

export function ModalNovoUsuario({ onConfirmar }: Props) {
    const [open, setOpen] = useState(false)
    const [form, setForm] = useState<UsuarioFormProps>(formInicial)
    const [cpfFormatado, setCpfFormatado] = useState("")
    const [erro, setErro] = useState<string | null>(null)
    const { unidades } = useUnidades()


    const handleConfirmar = () => {
        if (!validarSenha(form.senha)) {
            setErro("A senha deve ter no mínimo 6 caracteres.")
            return
        }
        onConfirmar(form)
        setOpen(false)
        setForm(formInicial)
    }

    const handleCPF = (e: React.ChangeEvent<HTMLInputElement>) => {
        const formatado = formatarCPF(e.target.value)
        setCpfFormatado(formatado)
        // Salva no form sem formatação
        setForm({ ...form, cpf: limparCPF(formatado) })
    }


    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
                <Button>NOVO USUÁRIO</Button>
            </DialogTrigger>
            <DialogContent className="bg-gray-900 border-gray-700 text-white">
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
                            value={cpfFormatado} onChange={handleCPF} maxLength={14}/>
                    </div>
                    <div>
                        <Label className="text-gray-300">Senha</Label>
                        <Input className="mt-1 bg-gray-800 border-gray-700 text-white" type="password" placeholder="********"
                            value={form.senha} onChange={(e) => setForm({ ...form, senha: e.target.value })} />
                            {erro && <p className="text-red-500 text-xs mt-1">{erro}</p>}
                    </div>
                    <div>
                        
                        <Label className="text-gray-300">Unidade</Label>
                        <Select onValueChange={(value) => setForm({ ...form, unidade: Number(value) })}>
                            <SelectTrigger className="mt-1 w-full bg-gray-800 border-gray-700 text-white">
                                <SelectValue placeholder="Selecione" />
                            </SelectTrigger>
                            <SelectContent className="bg-gray-800 border-gray-700 text-white">
                                {unidades.map(unidade => (
                                    <SelectItem key={unidade.id} value={String(unidade.id)}>{unidade.nomeUnidade}</SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                    
                    </div>
                    <div>
                        <Label className="text-gray-300">Usuário</Label>
                        <Select onValueChange={(value: 'ADMIN' | 'USER') => setForm({ ...form, grupo: value })}>
                            <SelectTrigger className="mt-1 w-full bg-gray-800 border-gray-700 text-white">
                                <SelectValue placeholder="Selecione" />
                            </SelectTrigger>
                            <SelectContent className="bg-gray-800 border-gray-700 text-white">
                                <SelectItem value="ADMIN">Administrador</SelectItem>
                                <SelectItem value="USER">Barbeiro</SelectItem>
                            </SelectContent>
                        </Select>
                    </div>
                    <div className="flex gap-3 mt-2">
                        <Botao color="primary" texto="Confirmar" click={handleConfirmar}></Botao>
                        <Botao color="secondary" texto="Cancelar" click={() => setOpen(false)}></Botao>
                    </div>
                </div>
            </DialogContent>
        </Dialog>
    )
}