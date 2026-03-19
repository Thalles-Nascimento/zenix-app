import type { ClienteDTOGeral } from "@/types/cliente"
import api_url from "../enviroments/enviroments"



// Verificar endpoint
export async function buscarClientesPorTelefoneService(telefone: string): Promise<ClienteDTOGeral[]> {
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
