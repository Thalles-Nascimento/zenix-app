import api_url from "../enviroments/enviroments-dev"
import type { UnidadeFormProps } from "../types/usuario"

export async function buscarUnidadeById(id: number) {
    const response = await api_url.get(`/unidades/${id}`)

    return response.data
}

export async function buscarUnidades() {
    const response = await api_url.get('/unidades')
    return response.data
}

export async function criarUnidadeService(dados: UnidadeFormProps) {
    const response = await api_url.post('/unidades', dados)
    return response.data
}

export async function atualizarUnidadeService(id: number, dados: UnidadeFormProps) {
    const response = await api_url.put(`/unidades/${id}`, dados)
    return response.data
}

export async function deletarUnidadeService(id: number) {
    const response = await api_url.delete(`/unidades/${id}`)
    return response.data
}

export async function buscarUnidadeDetalheService(id: number) {
    const response = await api_url.get(`/unidades/user/${id}`)
    return response.data
}

export async function reativarUnidadeService(id: number) {
    const response = await api_url.patch(`/unidades/${id}`,{})
    return response.data
}
