import { useNavigate } from "react-router-dom";
import loginService from "../services/auth-service";
import { useAuth } from "../contexts/auth-context";
import { validarEmail, validarSenha } from "../utils/validators"
import { useState } from "react";
import { toast } from "sonner";


export function useLogin(){

    const navigate = useNavigate()
    const { login } = useAuth();
    const [erro, setErro] = useState<string | null>(null)

    const logar = async (email: string, senha: string) => {
        if (!validarEmail(email)) {
            setErro("E-mail inválido.")
            return
        }
        if (!validarSenha(senha)) {
            setErro("A senha deve ter no mínimo 6 caracteres.")
            return
        }

        try {
            const dados = await loginService(email, senha)
            login(dados.token, dados.username, dados.permissao)
            navigate('/atendimentos')

        } catch(error){
            toast.error(`Usuário Inválido`)
        }
    }

    return { logar, erro };

}