import api_url from "../enviroments/enviroments";
import type { AtendimentoFormProps } from "../types/atendimento";

export async function atendimentoService() {
    const response = await api_url.get('/atendimentos');
    return response.data
}

export async function criarAtendimentoService(dados: AtendimentoFormProps) {
    const response = await api_url.post('/atendimentos', dados)
    return response.data
}

export async function atualizarAtendimentoService(id: number, dados: AtendimentoFormProps) {
    const response = await api_url.put(`/atendimentos/usuario/${id}`, dados)
    return response.data
}

export async function atualizarAtendimentoAdminService(id: number, dados: AtendimentoFormProps) {
    const response = await api_url.put(`/atendimentos/${id}`, dados)
    return response.data
}

export async function deletarAtendimentoService(id: number) {
    const response = await api_url.delete(`/atendimentos/${id}`)
    return response.data
}

export async function ativarAtendimentoService(id: number) {
    const response = await api_url.patch(`/atendimentos/${id}`)
    return response.data
}

export async function listarAtendimentosAdminService() {
    const response = await api_url.get('/atendimentos/admin')
    return response.data
}

export async function listarHistorico() {
    const response = await api_url.get('/atendimentos/historico')
    return response.data
}