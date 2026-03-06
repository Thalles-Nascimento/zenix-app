import api_url from "../enviroments/enviroments"
import type { UnidadeFormProps } from "../types/usuario"

const getToken = () => localStorage.getItem("token")
const headers = () => ({ Authorization: `Bearer ${getToken()}` })

export async function buscarUnidadeById(id: number) {
    const response = await api_url.get(`/unidades/${id}`, { headers: headers() })

    return response.data
}

export async function buscarUnidades() {
    const response = await api_url.get('/unidades', { headers: headers() })
    return response.data
}

export async function criarUnidadeService(dados: UnidadeFormProps) {
    const response = await api_url.post('/unidades', dados, { headers: headers() })
    return response.data
}

export async function atualizarUnidadeService(id: number, dados: UnidadeFormProps) {
    const response = await api_url.put(`/unidades/${id}`, dados, { headers: headers() })
    return response.data
}

export async function deletarUnidadeService(id: number) {
    const response = await api_url.delete(`/unidades/${id}`, { headers: headers() })
    return response.data
}

export async function buscarUnidadeDetalheService(id: number) {
    const response = await api_url.get(`/unidades/user/${id}`, { headers: headers() })
    return response.data
}

export async function reativarUnidadeService(id: number) {
    const response = await api_url.patch(`/unidades/${id}`,{}, { headers: headers() })
    return response.data
}
