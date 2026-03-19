import api_url from "../enviroments/enviroments"
import type { ClienteDTO, PlanoDTO } from "../types/cliente"

// ===== CLIENTES =====
export async function buscarTodosClientesService(): Promise<ClienteDTO[]> {
    const response = await api_url.get('/clientes')
    return response.data
}

export async function criarClienteAdminService(nomeCliente: string, telefoneCliente: string): Promise<void> {
    await api_url.post('/clientes', { nomeCliente, telefoneCliente })
}

export async function atualizarClienteService(id: number, nomeCliente: string, telefoneCliente: string): Promise<void> {
    await api_url.put(`/clientes/${id}`, { nomeCliente, telefoneCliente })
}

export async function deletarClienteService(id: number): Promise<void> {
    await api_url.delete(`/clientes/${id}`)
}

export async function vincularPlanoService(idCliente: number, idPlano: number): Promise<void> {
    await api_url.patch(`/clientes/planos/${idCliente}`, idPlano, {
        headers: { "Content-Type": "application/json" }
    })
}

export async function desvincularPlanoService(idCliente: number): Promise<void> {
    await api_url.delete(`/clientes/planos/${idCliente}`)
}

export async function ativarClienteService(id: number): Promise<void> {
    await api_url.patch(`/clientes/ativar/${id}`)
}

// ===== PLANOS =====
export async function buscarTodosPlanosService(): Promise<PlanoDTO[]> {
    const response = await api_url.get('/planos')
    return response.data
}
 
export async function criarPlanoService(dados: {
    planoDescricao: string
    valor: number
    servico: string[]
    limiteAtendimentos: number
}): Promise<void> {
    await api_url.post('/planos', dados)
}
 
export async function atualizarPlanoService(id: number, dados: {
    planoDescricao: string
    valor: number
    servico: string[]
    limiteAtendimentos: number
}): Promise<void> {
    await api_url.put(`/planos/${id}`, dados)
}
 
export async function deletarPlanoService(id: number): Promise<void> {
    await api_url.delete(`/planos/${id}`)
}
