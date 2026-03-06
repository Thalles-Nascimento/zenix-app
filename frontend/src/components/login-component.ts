import { useNavigate } from "react-router-dom";
import LoginService from "../services/login-service";
import { useAuth } from "../contexts/auth-context";

export default function LoginComponent({email, senha}: {email:string, senha:string}){

    const navigate = useNavigate()
    const { login } = useAuth();
    
    const clickButtonLogin = async () => {
        try {
            const dados = await LoginService(email, senha)
            login(dados.token, dados.username, dados.permissoes)
            navigate('/atendimentos')

        } catch(error){
            alert(error)
        }
    }

    return clickButtonLogin;

}