import axios from "axios";


const api = axios.create({
    baseURL: 'http://localhost:9090/api/v1'
})

export default async function LoginService(email:string, senha: string) {
    const response = await api.post('/users/login', { email, senha });
    
    return response.data;
    
}