import api_url from "../enviroments/enviroments"

export interface ClienteDTO {
    id: number
    nomeCliente: string
    vezesRetorno: number
}

// Verificar endpoint
export async function buscarClientesPorTelefoneService(telefone: string): Promise<ClienteDTO[]> {
    try {
        const response = await api_url.get(`/clientes/telefone/${telefone}`)
        return response.data
    } catch (error) {
        return []
    }
}

export async function criarClienteService(nomeCliente: string, telefoneCliente: string): Promise<void> {
    await api_url.post('/clientes', { nomeCliente, telefoneCliente })
}

export async function atualizarRetornoService(id: number): Promise<void> {
    await api_url.patch(`/clientes/retorno/${id}`, {})
}
