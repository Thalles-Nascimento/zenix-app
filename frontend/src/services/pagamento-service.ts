import type { PagamentoDTO } from "@/types/pagamento"
import api_url from "../enviroments/enviroments"

export async function buscarPagamentosService(): Promise<PagamentoDTO[]> {
    const response = await api_url.get('/pagamentos')
    return response.data
}

export async function criarPagamentoService(descricao: string): Promise<void> {
    await api_url.post('/pagamentos', { descricao })
}

export async function atualizarPagamentoService(id: number, descricao: string): Promise<void> {
    await api_url.put(`/pagamentos/${id}`, { descricao })
}

export async function deletarPagamentoService(id: number): Promise<void> {
    await api_url.delete(`/pagamentos/${id}`)
}
