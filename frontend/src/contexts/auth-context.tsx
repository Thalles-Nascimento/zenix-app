import { createContext, useContext, useState } from 'react'

interface AuthContextProps {
    token: string | null
    userName: string | null
    permissao: string | null
    login: (token: string, userName: string, permissao: string) => void
    logout: () => void
}

const AuthContext = createContext<AuthContextProps | null>(null);

export function useAuth(){
    const context = useContext(AuthContext)
    if (!context) throw new Error('useAuth deve ser usado dentro do AuthProvider')
    return context
}

export default function AuthProvider({ children }: { children: React.ReactNode }) {
    const [token, setToken] = useState<string | null>(sessionStorage.getItem("token"));
    const [userName, setUserName] = useState<string | null>(sessionStorage.getItem("username"))
    const [permissao, setpermissao] = useState<string | null>(sessionStorage.getItem("permissao"))

    const login = (novoToken: string, novoUserName: string, grupo: string) => {
        setToken(novoToken)
        setUserName(novoUserName)
        setpermissao(grupo)
        sessionStorage.setItem("token", novoToken)
        sessionStorage.setItem("username", novoUserName)
        sessionStorage.setItem("permissao", grupo)
    }
    const logout = () => {
        setToken(null)
        setUserName(null)
        sessionStorage.removeItem("token")
        sessionStorage.removeItem("username")
        sessionStorage.removeItem("permissao")

    }

    return (
        <AuthContext.Provider value={{ token, userName, permissao, login, logout }}>
            {children}
        </AuthContext.Provider>
    )
}
