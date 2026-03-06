import { Navigate } from 'react-router-dom'

export default function RotaAdmin({ children }: { children: React.ReactNode }) {
    const permissao = localStorage.getItem("permissao")

    if (permissao !== 'ADMIN') {
        return <Navigate to="/atendimentos" />
    }

    return children
}