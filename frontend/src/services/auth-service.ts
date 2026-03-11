import api_url from "../enviroments/enviroments";


export default async function loginService(email:string, senha: string) {
    const response = await api_url.post('/users/login', { email, senha });
    
    return response.data;
    
}