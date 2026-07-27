import { useEffect, useState } from "react"
import { toast } from "sonner"
import {
    buscarTodosClientesService,
    criarClienteAdminService,
    atualizarClienteService,
    deletarClienteService,
    vincularPlanoService,
    desvincularPlanoService,
    ativarClienteService
} from "../services/cliente-plano-service"
import type { ClienteDTO } from "../types/cliente"

export function useClientes() {
    const [clientes, setClientes] = useState<ClienteDTO[]>([])
    const [carregando, setCarregando] = useState(true)
    const [filtro, setFiltro] = useState<"ativos" | "todos">("ativos")

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

    const ativarCliente = async (id: string) => {
        await ativarClienteService(id)
        await buscar()
    }

    const clientesFiltrados = filtro === "ativos"
        ? clientes.filter(c => c.status === 1)
        : clientes

    const criarCliente = async (nomeCliente: string, telefoneCliente: string) => {
        await criarClienteAdminService(nomeCliente, telefoneCliente)
        await buscar()
    }

    const atualizarCliente = async (id: string, nomeCliente: string, telefoneCliente: string) => {
        await atualizarClienteService(id, nomeCliente, telefoneCliente)
        await buscar()
    }

    const deletarCliente = async (id: string) => {
        await deletarClienteService(id)
        await buscar()
    }

    const vincularPlano = async (idCliente: string, idPlano: string) => {
        await vincularPlanoService(idCliente, idPlano)
        await buscar()
    }

    const desvincularPlano = async (idCliente: string) => {
        await desvincularPlanoService(idCliente)
        await buscar()
    }

    return {
        clientes: clientesFiltrados,
        carregando,
        filtro,
        setFiltro,
        criarCliente,
        atualizarCliente,
        deletarCliente,
        vincularPlano,
        desvincularPlano,
        recarregar: buscar,
        ativarCliente
    }
}