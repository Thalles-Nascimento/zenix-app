import { useEffect, useState } from "react"
import { buscarServicosService, criarServicoService, atualizarServicoService, deletarServicoService } from "../services/servicos-service"
import { toast } from "sonner"
import type { ServicoDTO } from "@/types/servico"

export function useServicos() {
    const [servicos, setServicos] = useState<ServicoDTO[]>([])
    const [carregando, setCarregando] = useState(true)

    const buscar = async () => {
        try {
            const dados = await buscarServicosService()
            setServicos(dados.filter(s => s.status === 1))
        } catch {
            toast.error("Erro ao buscar serviços.")
        } finally {
            setCarregando(false)
        }
    }

    useEffect(() => {
        buscar()
    }, [])

    const criarServico = async (servico: string, valor: number) => {
        await criarServicoService(servico, valor)
        await buscar()
    }

    const atualizarServico = async (id: number, servico: string, valor: number) => {
        await atualizarServicoService(id, servico, valor)
        await buscar()
    }

    const deletarServico = async (id: number) => {
        await deletarServicoService(id)
        await buscar()
    }

    return { servicos, carregando, criarServico, atualizarServico, deletarServico, recarregar: buscar }
}
