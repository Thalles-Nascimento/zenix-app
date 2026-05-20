import { useEffect, useState } from "react"
import { buscarPagamentosService, criarPagamentoService, atualizarPagamentoService, deletarPagamentoService } from "../services/pagamento-service"
import { toast } from "sonner"
import type { PagamentoDTO } from "@/types/pagamento"

export function usePagamentos() {
    const [pagamentos, setPagamentos] = useState<PagamentoDTO[]>([])
    const [carregando, setCarregando] = useState(true)

    const buscar = async () => {
        try {
            const dados = await buscarPagamentosService()
            setPagamentos(dados.filter(p => p.status === 1))
        } catch {
            toast.error("Erro ao buscar formas de pagamento.")
        } finally {
            setCarregando(false)
        }
    }

    useEffect(() => {
        buscar()
    }, [])

    const criarPagamento = async (descricao: string) => {
        await criarPagamentoService(descricao)
        await buscar()
    }

    const atualizarPagamento = async (id: number, descricao: string) => {
        await atualizarPagamentoService(id, descricao)
        await buscar()
    }

    const deletarPagamento = async (id: number) => {
        await deletarPagamentoService(id)
        await buscar()
    }

    return { pagamentos, carregando, criarPagamento, atualizarPagamento, deletarPagamento }
}
