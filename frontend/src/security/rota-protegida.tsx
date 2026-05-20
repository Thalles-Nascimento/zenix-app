import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '../contexts/auth-context'
import { Loader2 } from 'lucide-react'

export default function RotaProtegida() {
    const { userName, carregando } = useAuth()

    if (carregando) {
        return (
            <div className="flex min-h-screen items-center justify-center bg-black">
                <Loader2 className="h-8 w-8 animate-spin text-orange-500" />
            </div>
        )
    }

    if (userName === null) {
        return <Navigate to='/login' />
    }

    return <Outlet />
}