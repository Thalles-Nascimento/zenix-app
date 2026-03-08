import { useState } from "react"
import { buscarClientesPorTelefoneService, criarClienteService } from "../services/cliente-service"
import type { ClienteDTO } from "../services/cliente-service"

export function useCliente() {
    const [buscando, setBuscando] = useState(false)
    const [clientes, setClientes] = useState<ClienteDTO[]>([])

    const buscarClientes = async (telefone: string) => {
        const numeroCliente = telefone.replace(/\D/g, "")

        if (numeroCliente.length < 11) {
            setClientes([])
            return
        }

        try {
            setBuscando(true)
            const lista = await buscarClientesPorTelefoneService(numeroCliente)
            setClientes(lista)
        } catch (error) {
            setClientes([])
        } finally {
            setBuscando(false)
        }
    }

    const criarCliente = async (nomeCliente: string, telefoneCliente: string) => {
        await criarClienteService(nomeCliente, telefoneCliente.replace(/\D/g, ""))
    }

    const atualizarRetorno = async (id: number) => {
        await atualizarRetorno(id) 
    }

    return { clientes, buscarClientes, criarCliente, buscando, atualizarRetorno }
}
