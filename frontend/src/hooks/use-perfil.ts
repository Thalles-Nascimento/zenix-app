import { useState, useEffect } from "react"
import { getMeuPerfilService, trocarSenhaService } from "../services/usuario-service"
import { toast } from "sonner"

export function usePerfil() {
    const [perfil, setPerfil] = useState<{ id: number, nome: string } | null>(null)

    useEffect(() => {
        const buscar = async () => {
            try {
                const dados = await getMeuPerfilService()
                setPerfil(dados)
            } catch (error) {
                console.error(error)
            }
        }
        buscar()
    }, [])

    const trocarSenha = async (novaSenha: string) => {
        if (!perfil) return
        if (novaSenha.length < 6) {
            toast.error("Senha deve ter no mínimo 6 caracteres.")
            return
        }
        try {
            await trocarSenhaService(perfil.id, novaSenha)
            toast.success("Senha alterada com sucesso!")
        } catch (error) {
            toast.error("Erro ao alterar senha.")
        }
    }

    return { perfil, trocarSenha }
}