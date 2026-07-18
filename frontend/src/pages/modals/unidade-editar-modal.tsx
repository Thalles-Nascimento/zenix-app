import { useEffect, useState } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "../../components/ui/dialog"
import { Input } from "../../components/ui/input"
import { Label } from "../../components/ui/label"
import { Botao } from "../../components/common/botao"
import type { UnidadeProps, UnidadeFormProps, UnidadeDetalheProps } from "../../types/usuario"
import { buscarUnidadeDetalheService } from "../../services/unidade-service"

interface Props {
    unidade: UnidadeProps | null
    open: boolean
    onFechar: () => void
    onConfirmar: (id: number, form: UnidadeFormProps) => void
    onDeletar: (id: number) => void
    onReativar: (id: number) => void
}

const grupoConfig: Record<string, { label: string; className: string }> = {
    ADMIN: { label: "Admin",    className: "text-orange-500 font-semibold" },
    USER:  { label: "Barbeiro", className: "text-blue-400 font-semibold" },
}

export function ModalEditarUnidade({ unidade, open, onFechar, onConfirmar, onDeletar, onReativar }: Props) {
    const [form, setForm] = useState<UnidadeFormProps>({ nome: "", endereco: "" })
    const [detalhe, setDetalhe] = useState<UnidadeDetalheProps | null>(null)
    const [reativar, setReativar] = useState(false)

    useEffect(() => {
        if (unidade) {
            setForm({ nome: unidade.nome, endereco: unidade.endereco })
            setReativar(false)
            buscarUnidadeDetalheService(unidade.id).then(setDetalhe).catch(console.error)
        }
    }, [unidade])

    const handleConfirmar = async () => {
        if (!unidade) return
        if (reativar && unidade.status === -1) {
            await onReativar(unidade.id)
        } else {
            await onConfirmar(unidade.id, form)
        }
        onFechar()
    }

    const handleDeletar = () => {
        if (!unidade) return
        onDeletar(unidade.id)
        onFechar()
    }

    return (
        <Dialog open={open} onOpenChange={onFechar}>
            <DialogContent className="bg-black border-gray-500 text-white w-[calc(100vw-2rem)] max-w-md max-h-[90vh] overflow-y-auto">
                <DialogHeader>
                    <DialogTitle className="text-white">Editar Unidade</DialogTitle>
                </DialogHeader>
                <div className="flex flex-col gap-4 mt-2">
                    <div>
                        <Label className="text-white">Nome da Unidade</Label>
                        <Input className="mt-1 bg-gray-900 border-gray-700 text-white"
                            value={form.nome}
                            onChange={(e) => setForm({ ...form, nome: e.target.value })} />
                    </div>
                    <div>
                        <Label className="text-white">Endereço</Label>
                        <Input className="mt-1 bg-gray-900 border-gray-700 text-white"
                            value={form.endereco}
                            onChange={(e) => setForm({ ...form, endereco: e.target.value })} />
                    </div>

                    <Label className="text-white">Barbeiros</Label>
                    <div className="flex flex-col gap-2 max-h-40 overflow-y-auto notranslate">
                        {detalhe?.usuarios?.length === 0 ? (
                            <p className="text-gray-500 text-sm notranslate">Nenhum usuário nessa unidade</p>
                        ) : (
                            detalhe?.usuarios?.map(u => (
                                <div key={u.id} className="notranslate flex items-center justify-between bg-gray-900 rounded-lg px-3 py-2">
                                    <div>
                                        <p className="text-white text-sm font-bold notranslate">{u.nome}</p>
                                        <p className="text-gray-400 text-xs notranslate">{u.email}</p>
                                    </div>
                                    <div className="flex items-center gap-2 notranslate">
                                        <span className={`notranslate text-xs ${grupoConfig[u.grupo]?.className ?? ""}`}>
                                            {grupoConfig[u.grupo]?.label ?? u.grupo}
                                        </span>
                                        <span className={`notranslate text-xs font-bold ${u.status === 1 ? "text-orange-500" : "text-gray-500"}`}>
                                            {u.status === 1 ? "Ativo" : "Inativo"}
                                        </span>
                                    </div>
                                </div>
                            ))
                        )}
                    </div>

                    {unidade?.status === -1 && (
                        <div className="flex items-center gap-2 py-1 notranslate">
                            <input
                                type="checkbox"
                                id="reativar"
                                checked={reativar}
                                className="w-4 h-4 accent-orange-500 cursor-pointer notranslate"
                                onChange={(e) => setReativar(e.target.checked)}
                            />
                            <label htmlFor="reativar" className="text-gray-300 text-sm cursor-pointer notranslate">
                                Reativar unidade
                            </label>
                        </div>
                    )}

                    <div className="flex flex-col gap-2 mt-2 notranslate">
                        <Botao texto="Salvar Alterações" color="sucess" click={handleConfirmar} />
                        <Botao texto="Deletar" color="delete" click={handleDeletar} />
                        <Botao texto="Cancelar" color="cancel" click={onFechar} />
                    </div>
                </div>
            </DialogContent>
        </Dialog>
    )
}
