import { useState, useEffect } from "react";
import { usuariosService, criarUsuarioService, atualizarUsuarioService, deletarUsuarioService, reativarUsuarioService } from "../services/usuario-service";
import type { UserProps, UsuarioFormProps } from "../types/usuario";
import { toast } from "sonner"


export function useUsuarios(){
    const [dados, setDados] = useState<UserProps[]>([]);
    const [carregando, setCarregando] = useState(true);

    const [filtro, setFiltro] = useState<'ativos' | 'todos'>('ativos')

    const dadosFiltrados = filtro === 'ativos'
        ? dados.filter(u => u.status === 1)
        : dados

    const buscarUsuarios = async () => {
        try {
            setCarregando(true)
            const usuarios = await usuariosService()
            setDados(usuarios)
        } catch (error) {
            toast.error("Erro ao buscar usuários.")
        } finally {
            setCarregando(false)
        }
    }

    useEffect(() => {
        buscarUsuarios()
        }, [])

    const criarUsuario = async (form: UsuarioFormProps) => {
        try {
            await criarUsuarioService(form)
            toast.success("Usuário criado com sucesso!")
            await buscarUsuarios()
        } catch (error) {
            toast.error("Erro ao criar usuário.")
        }
    }

    const atualizarUsuario = async (id: number, form: UsuarioFormProps) => {
        try {
            await atualizarUsuarioService(id, form)
            console.log(form)
            toast.success("Usuário atualizado com sucesso!")
            await buscarUsuarios()
        } catch (error) {
            toast.error("Erro ao atualizar usuário.")
        }
    }

    const deletarUsuario = async (id: number) => {
        try {
            await deletarUsuarioService(id)
            toast.success("Usuário deletado com sucesso!")
            await buscarUsuarios()
        } catch (error) {
            toast.error("Erro ao deletar usuário.")
        }
    }

    const reativarUsuario = async (id: number) => {
        try {
            await reativarUsuarioService(id)
            toast.success("Usuário reativado com sucesso!")
            await buscarUsuarios()
        } catch (error) {
            toast.error("Erro ao reativar usuário.")
        }
    }

    return { dados: dadosFiltrados, carregando, filtro, setFiltro, criarUsuario, atualizarUsuario, deletarUsuario, reativarUsuario }
}