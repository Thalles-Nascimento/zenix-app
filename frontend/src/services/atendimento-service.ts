import api_url from "../enviroments/enviroments";
import type { AtendimentoFormProps } from "../types/atendimento";



const getToken = () => localStorage.getItem("token")

export async function atendimentoService() {
    
    const response = await api_url.get('/atendimentos', {
        headers: {
            Authorization: `Bearer ${getToken()}`
        }
    });
    
    return response.data

}

export async function criarAtendimentoService(dados: AtendimentoFormProps) {
    const response = await api_url.post('/atendimentos', dados, {
        headers: { Authorization: `Bearer ${getToken()}` }
    })
    return response.data
}

export async function atualizarAtendimentoService(id: number, dados: AtendimentoFormProps) {
    const response = await api_url.put(`/atendimentos/${id}`, dados, {
        headers: { Authorization: `Bearer ${getToken()}` }
    })
    return response.data
}

export async function deletarAtendimentoService(id: number) {
    const response = await api_url.delete(`/atendimentos/${id}`, {
        headers: { Authorization: `Bearer ${getToken()}` }
    })
    return response.data
}


export async function listarAtendimentosAdminService() {
    const response = await api_url.get('/atendimentos/admin', {
        headers: { Authorization: `Bearer ${getToken()}` }
    })
    return response.data
}

export async function listarHistorico() {
    const response = await api_url.get('/atendimentos/historico', {
        headers: { Authorization: `Bearer ${getToken()}` }
    })
    return response.data
}