import type { FilaFormProps } from "../types/fila"
import api_url from "../enviroments/enviroments-dev"

// Público — cliente entra na fila (sem token)
export async function entrarFilaService(dados: FilaFormProps) {
    const response = await api_url.post('/fila', dados)
    return response.data
}

// Barbeiro busca a própria fila
export async function buscarFilaService() {
    const response = await api_url.get('/fila')
    return response.data
}

// Barbeiro chama o próximo
export async function chamarProximoService(id: number) {
    const response = await api_url.patch(`/fila/${id}`, {})
    return response.data
}

// Barbeiro finaliza atendimento
export async function finalizarFilaService(id: number) {
    const response = await api_url.delete(`/fila/${id}`)
    return response.data
}