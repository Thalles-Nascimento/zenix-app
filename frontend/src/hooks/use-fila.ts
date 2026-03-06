import { useEffect, useState } from "react"
import { toast } from "sonner"
import { buscarFilaService, chamarProximoService, finalizarFilaService } from "../services/fila-service"
import type { FilaProps } from "../types/fila"

export function useFila() {
    const [fila, setFila] = useState<FilaProps[]>([])
    const [carregando, setCarregando] = useState(true)
    const [clienteSelecionado, setClienteSelecionado] = useState<FilaProps | null>(null)

    const buscarFila = async () => {
        try {
            const dados = await buscarFilaService()
            setFila(dados)
        } catch (error) {
            toast.error("Erro ao buscar fila.")
        } finally {
            setCarregando(false)
        }
    }

    useEffect(() => {
        buscarFila()

        // Atualiza a fila a cada 5 segundos automaticamente
        const intervalo = setInterval(buscarFila, 5000)
        return () => clearInterval(intervalo)
    }, [])

    const chamarProximo = async (id: number) => {
        
        try {
            await chamarProximoService(id)
            toast.success("Cliente chamado!")
            await buscarFila()
        } catch (error) {
            toast.error("Erro ao chamar cliente.")
        }
    }

    const finalizarAtendimento = async (id: number) => {
        try {
            await finalizarFilaService(id)
            toast.success("Atendimento finalizado!")
            setClienteSelecionado(null)
            await buscarFila()
        } catch (error) {
            toast.error("Erro ao finalizar atendimento.")
        }
    }

    return { fila, carregando, clienteSelecionado, setClienteSelecionado, chamarProximo, finalizarAtendimento }
}