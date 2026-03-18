import { useEffect, useState } from "react"
import { toast } from "sonner"
import {
    buscarTodosClientesService,
    criarClienteAdminService,
    atualizarClienteService,
    deletarClienteService,
    vincularPlanoService,
    desvincularPlanoService
} from "../services/cliente-plano-service"
import type { ClienteDTO } from "../types/cliente"

export function useClientes() {
    const [clientes, setClientes] = useState<ClienteDTO[]>([])
    const [carregando, setCarregando] = useState(true)

    const buscar = async () => {
        try {
            const dados = await buscarTodosClientesService()
            setClientes(dados)
        } catch {
            toast.error("Erro ao buscar clientes.")
        } finally {
            setCarregando(false)
        }
    }

    useEffect(() => {
        buscar()
    }, [])

    const criarCliente = async (nomeCliente: string, telefoneCliente: string) => {
        await criarClienteAdminService(nomeCliente, telefoneCliente)
        await buscar()
    }

    const atualizarCliente = async (id: number, nomeCliente: string, telefoneCliente: string) => {
        await atualizarClienteService(id, nomeCliente, telefoneCliente)
        await buscar()
    }

    const deletarCliente = async (id: number) => {
        await deletarClienteService(id)
        await buscar()
    }

    const vincularPlano = async (idCliente: number, idPlano: number) => {
        await vincularPlanoService(idCliente, idPlano)
        await buscar()
    }

    const desvincularPlano = async (idCliente: number) => {
        await desvincularPlanoService(idCliente)
        await buscar()
    }

    return { clientes, carregando, criarCliente, atualizarCliente, deletarCliente, vincularPlano, desvincularPlano, recarregar: buscar }
}
