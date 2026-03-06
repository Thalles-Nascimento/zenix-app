import { useEffect, useState } from "react";
import type { UnidadeProps } from "../types/usuario";
import {  buscarUnidades } from "../services/unidade-service";
import { toast } from "sonner";


export function useUnidades() {
    const [unidades, setUnidades] = useState<UnidadeProps[]>([])
    const [carregando, setCarregando] = useState(true)

    useEffect(() => {
        const buscar = async () => {
            try {
                const dados = await buscarUnidades()
                setUnidades(dados.filter((u: UnidadeProps) => u.status === 1))
            } catch (error) {
                toast.error("Erro ao buscar unidades.")
            } finally {
                setCarregando(false)
            }
        }
        buscar()
    }, [])

    return { unidades, carregando }
}