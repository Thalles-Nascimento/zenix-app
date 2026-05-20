import { useState, useEffect } from "react"
import type { UnidadeProps, UnidadeFormProps } from "../types/usuario"
import { buscarUnidades, criarUnidadeService, atualizarUnidadeService, deletarUnidadeService, reativarUnidadeService } from "../services/unidade-service"
import { toast } from "sonner"

export function useUnidades() {
    const [unidades, setUnidades] = useState<UnidadeProps[]>([])
    const [carregando, setCarregando] = useState(true)

    const buscar = async () => {
        try {
            setCarregando(true)
            const dados = await buscarUnidades()
            setUnidades(dados)
        } catch (error) {
            toast.error("Erro ao buscar unidades.")
        } finally {
            setCarregando(false)
        }
        
    }

    useEffect(() => { buscar() }, [])

    const criarUnidade = async (form: UnidadeFormProps) => {
        try {
            await criarUnidadeService(form)
            toast.success("Unidade criada com sucesso!")
            await buscar()
        } catch (error) {
            toast.error("Erro ao criar unidade.")
        }
    }

    const atualizarUnidade = async (id: number, form: UnidadeFormProps) => {
        try {
            await atualizarUnidadeService(id, form)
            toast.success("Unidade atualizada com sucesso!")
            await buscar()
        } catch (error) {
            toast.error("Erro ao atualizar unidade.")
        }
    }

    const deletarUnidade = async (id: number) => {
        try {
            await deletarUnidadeService(id)
            toast.success("Unidade deletada com sucesso!")
            await buscar()
        } catch (error) {
            toast.error("Erro ao deletar unidade.")
        }
    }

    const reativarUnidade = async (id: number) => {
            try {
                console.log("Estou aqui")
                await reativarUnidadeService(id)
                toast.success("Unidade reativada com sucesso!")
                await buscar()
            } catch (error) {
                toast.error("Erro ao reativar unidade.")
            }
        }

    return { unidades, carregando, criarUnidade, atualizarUnidade, deletarUnidade, reativarUnidade }
}