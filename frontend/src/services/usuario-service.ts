import api_url from "../enviroments/enviroments";
import type { UsuarioFormProps } from "../types/usuario";


const token = () => localStorage.getItem("token")
const headers = () => ({Authorization: `Bearer ${token()}`})

export async function usuariosService() {
    
    const response = await api_url.get('/users');
    
    return response.data

} 

export async function criarUsuarioService(dados: UsuarioFormProps) {
    const response = await api_url.post('/users/register', dados, { headers: headers() })
    return response.data
}

export async function atualizarUsuarioService(id: number, dados: UsuarioFormProps) {
    const response = await api_url.put(`/users/${id}`, dados, { headers: headers() })
    return response.data
}

export async function deletarUsuarioService(id: number) {
    const response = await api_url.delete(`/users/${id}`, { headers: headers() })
    return response.data
}

export async function reativarUsuarioService(id: number) {
    const response = await api_url.patch(`/users/${id}`, {}, { headers: headers() })
    return response.data
}

export async function getMeuPerfilService() {
    const response = await api_url.get('/users/me', {
        headers: headers()
    })
    return response.data
}

export async function trocarSenhaService(id: number, senha: string) {
    const response = await api_url.put(`/users/${id}`, { senha }, {
        headers: headers()
    })
    return response.data
}