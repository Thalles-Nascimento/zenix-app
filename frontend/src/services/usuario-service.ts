import api_url from "../enviroments/enviroments";
import type { UsuarioFormProps } from "../types/usuario";


export async function usuariosService() {
    
    const response = await api_url.get('/users');
    
    return response.data

} 

export async function criarUsuarioService(dados: UsuarioFormProps) {
    const response = await api_url.post('/users/register', dados)
    return response.data
}

export async function atualizarUsuarioService(id: number, dados: UsuarioFormProps) {
    const response = await api_url.put(`/users/${id}`, dados)
    return response.data
}

export async function deletarUsuarioService(id: number) {
    const response = await api_url.delete(`/users/${id}`)
    return response.data
}

export async function reativarUsuarioService(id: number) {
    const response = await api_url.patch(`/users/${id}`, {})
    return response.data
}

export async function getMeuPerfilService() {
    const response = await api_url.get('/users/me')
    return response.data
}

export async function trocarSenhaService(id: number, senha: string) {
    const response = await api_url.put(`/users/${id}`, { senha })
    return response.data
}

export async function barbeirosPorUnidadeService(unidadeId: number) {
    const response = await api_url.get(`/users/barbeiros/${unidadeId}`)
    return response.data
}