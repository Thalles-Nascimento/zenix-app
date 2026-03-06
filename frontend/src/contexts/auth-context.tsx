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
    const [token, setToken] = useState<string | null>(localStorage.getItem("token"));
    const [userName, setUserName] = useState<string | null>(localStorage.getItem("username"))
    const [permissao, setpermissao] = useState<string | null>(localStorage.getItem("permissao"))

    const login = (novoToken: string, novoUserName: string, grupo: string) => {
        setToken(novoToken)
        setUserName(novoUserName)
        setpermissao(grupo)
        localStorage.setItem("token", novoToken)
        localStorage.setItem("username", novoUserName)
        localStorage.setItem("permissao", grupo)
    }
    const logout = () => {
        setToken(null)
        setUserName(null)
        localStorage.removeItem("token")
        localStorage.removeItem("username")
        localStorage.removeItem("permissao")

    }

    return (
        <AuthContext.Provider value={{ token, userName, permissao, login, logout }}>
            {children}
        </AuthContext.Provider>
    )
}
