import { useEffect, useState } from "react"
import { barbeirosPorUnidadeService } from "../services/usuario-service"
import type { UserProps } from "../types/usuario"

export function useBarbeiros(unidadeId: number) {
    const [barbeiros, setBarbeiros] = useState<UserProps[]>([])

    useEffect(() => {
        const buscar = async () => {
            try {
                const data = await barbeirosPorUnidadeService(unidadeId)
                setBarbeiros(data)
            } catch (error) {
                console.error(error)
            }
        }
        buscar()
    }, [unidadeId])

    return { barbeiros }
}