import { Dialog, DialogContent, DialogHeader, DialogTitle } from "../ui/dialog"
import { Botao } from "./botao"

interface Props {
    open: boolean
    titulo?: string
    mensagem: string
    onConfirmar: () => void
    onCancelar: () => void
}

export function ModalConfirmacao({ open, titulo = "Confirmar ação", mensagem, onConfirmar, onCancelar }: Props) {
    return (
        <Dialog open={open} onOpenChange={onCancelar}>
            <DialogContent className="bg-black border-gray-700 text-white w-[calc(100vw-2rem)] max-w-sm notranslate">
                <DialogHeader>
                    <DialogTitle className="text-white notranslate">{titulo}</DialogTitle>
                </DialogHeader>
                <p className="text-white text-sm mt-1">{mensagem}</p>
                <div className="flex flex-col gap-2 mt-4">
                    <Botao texto="Confirmar" color="sucess" click={onConfirmar} />
                    <Botao texto="Cancelar" color="cancel" click={onCancelar} />
                </div>
            </DialogContent>
        </Dialog>
    )
}