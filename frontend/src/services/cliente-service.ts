import api_url from "../enviroments/enviroments-dev"

export interface ClienteDTO {
    id: number
    nomeCliente: string
    vezesRetorno: number
}

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
