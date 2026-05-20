import api_url from '@/enviroments/enviroments'
import { createContext, useContext, useEffect, useState } from 'react'

interface AuthContextProps {
    userName: string | null
    permissao: string | null
    carregando: boolean
    login: (userName: string, permissao: string) => void
    logout: () => void
}

const AuthContext = createContext<AuthContextProps | null>(null);

export function useAuth(){
    const context = useContext(AuthContext)
    if (!context) throw new Error('useAuth deve ser usado dentro do AuthProvider')
    return context
}

export default function AuthProvider({ children }: { children: React.ReactNode }) {
    const [userName, setUserName] = useState<string | null>(null)
    const [permissao, setPermissao] = useState<string | null>(null)
    const [carregando, setCarregando] = useState(true)

    useEffect(() => {
        const recuperarSessao = async () => {
            try {
                const response = await api_url.get('/users/me')
                setUserName(response.data.nome)
                setPermissao(response.data.grupo)
            } catch (error) {
                // Cookie inexistente ou expirado — usuário precisa fazer login
                console.log(error)
                setUserName(null)
                setPermissao(null)
            } finally {
                setCarregando(false)
            }
        }

        recuperarSessao()
    }, [])

    const login = (novoUserName: string, grupo: string) => {
        setUserName(novoUserName)
        setPermissao(grupo)

    }

    const logout = async () => {
        // Chama o backend para apagar o cookie httpOnly
        await api_url.post('/users/logout')
        setUserName(null)
        setPermissao(null)
    }

    return (
        <AuthContext.Provider value={{ userName, permissao, carregando, login, logout }}>
            {children}
        </AuthContext.Provider>
    )
}
