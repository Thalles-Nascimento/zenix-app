import { useEffect, useState } from "react"
import { usuariosService } from "../services/usuario-service"
import type { UserProps } from "../types/usuario"

export function useBarbeiros(unidadeId: number) {
    const [barbeiros, setBarbeiros] = useState<UserProps[]>([])

    useEffect(() => {
        const buscar = async () => {
            try {
                const usuarios = await usuariosService()
                setBarbeiros(usuarios.filter((u: UserProps) =>
                    u.status === 1 &&
                    u.unidade.id === unidadeId
                ))
            } catch (error) {
                console.error(error)
            }
        }
        buscar()
    }, [unidadeId])

    return { barbeiros }
}