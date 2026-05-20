import type { ServicoDTO } from "@/types/servico"
import api_url from "../enviroments/enviroments"

export async function buscarServicosService(): Promise<ServicoDTO[]> {
    const response = await api_url.get('/servicos')
    return response.data
}

export async function criarServicoService(servico: string, valor: number): Promise<void> {
    await api_url.post('/servicos', { servico, valor })
}

export async function atualizarServicoService(id: number, servico: string, valor: number): Promise<void> {
    await api_url.put(`/servicos/${id}`, { servico, valor })
}

export async function deletarServicoService(id: number): Promise<void> {
    await api_url.delete(`/servicos/${id}`)
}