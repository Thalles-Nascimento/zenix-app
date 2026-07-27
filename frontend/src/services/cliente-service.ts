import type { ClienteDTO } from "@/types/cliente"
import api_url from "../enviroments/enviroments"

export async function buscarClientesPorTelefoneService(telefone: string): Promise<ClienteDTO[]> {
    try {
        const response = await api_url.get(`/clientes/telefone/${telefone}`)
        return response.data
    } catch {
        return []
    }
}

export async function buscarClientesPorNomeService(nome: string): Promise<ClienteDTO[]> {
    try {
        const response = await api_url.get(`/clientes/nome?nome=${encodeURIComponent(nome.trim())}`)
        return response.data
    } catch {
        return []
    }
}

export async function criarClienteService(nomeCliente: string, telefoneCliente: string): Promise<void> {
    await api_url.post('/clientes', { nomeCliente, telefoneCliente })
}

export async function atualizarRetornoService(id: string): Promise<void> {
    await api_url.patch(`/clientes/retorno/${id}`, {})
}