import { useState } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "../../components/ui/dialog"
import { Button } from "../../components/ui/button"
import { Input } from "../../components/ui/input"
import { Label } from "../../components/ui/label"
import { Botao } from "../../components/common/botao"
import type { UnidadeFormProps } from "../../types/usuario"

interface Props {
    onConfirmar: (form: UnidadeFormProps) => void
}

const formInicial: UnidadeFormProps = { nomeUnidade: "", endereco: "" }

export function ModalNovaUnidade({ onConfirmar }: Props) {
    const [open, setOpen] = useState(false)
    const [form, setForm] = useState<UnidadeFormProps>(formInicial)

    const handleConfirmar = () => {
        if (!form.nomeUnidade || !form.endereco) return
        onConfirmar(form)
        setOpen(false)
        setForm(formInicial)
    }

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
                <Button>NOVA UNIDADE</Button>
            </DialogTrigger>
            <DialogContent className="bg-gray-900 border-gray-700 text-white">
                <DialogHeader>
                    <DialogTitle className="text-white">Nova Unidade</DialogTitle>
                </DialogHeader>
                <div className="flex flex-col gap-4 mt-2">
                    <div>
                        <Label className="text-gray-300">Nome da Unidade</Label>
                        <Input className="mt-1 bg-gray-800 border-gray-700 text-white"
                            placeholder="Ex: WN Barbearia Centro"
                            value={form.nomeUnidade}
                            onChange={(e) => setForm({ ...form, nomeUnidade: e.target.value })} />
                    </div>
                    <div>
                        <Label className="text-gray-300">Endereço</Label>
                        <Input className="mt-1 bg-gray-800 border-gray-700 text-white"
                            placeholder="Ex: Centro"
                            value={form.endereco}
                            onChange={(e) => setForm({ ...form, endereco: e.target.value })} />
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