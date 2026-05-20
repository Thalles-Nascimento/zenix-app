import { useEffect, useState } from "react"
import { toast } from "sonner"
import { buscarTodosPlanosService, criarPlanoService, atualizarPlanoService, deletarPlanoService } from "../services/cliente-plano-service"
import type { PlanoDTO } from "../types/cliente"

export function usePlanos() {
    const [planos, setPlanos] = useState<PlanoDTO[]>([])
    const [carregando, setCarregando] = useState(true)

    const buscar = async () => {
        try {
            const dados = await buscarTodosPlanosService()
            setPlanos(dados)
        } catch {
            toast.error("Erro ao buscar planos.")
        } finally {
            setCarregando(false)
        }
    }

    useEffect(() => {
        buscar()
    }, [])

    const criarPlano = async (planoDescricao: string, valor: number, servico: string[], limiteAtendimentos: number) => {
        await criarPlanoService({ planoDescricao, valor, servico, limiteAtendimentos })
        await buscar()
    }

    const atualizarPlano = async (id: number, planoDescricao: string, valor: number, servico: string[], limiteAtendimentos: number) => {
        await atualizarPlanoService(id, { planoDescricao, valor, servico, limiteAtendimentos })
        await buscar()
    }

    const deletarPlano = async (id: number) => {
        await deletarPlanoService(id)
        await buscar()
    }

    return { planos, carregando, criarPlano, atualizarPlano, deletarPlano }
}