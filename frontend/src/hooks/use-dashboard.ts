import { useEffect, useState } from "react"
import { listarAtendimentosAdminService } from "../services/atendimento-service"
import type { AtendimentoAdminProps } from "../types/atendimento"
import { parseData, hoje } from "../utils/date"
import { toast } from "sonner"

export function useDashboard() {
    const [todosAtendimentos, setTodosAtendimentos] = useState<AtendimentoAdminProps[]>([])
    const [carregando, setCarregando] = useState(true)
    const [filtroInicio, setFiltroInicio] = useState(hoje()) // ← começa com hoje
    const [filtroFim, setFiltroFim] = useState(hoje())       // ← começa com hoje

    const buscar = async () => {
        try {
            setCarregando(true)
            const dados = await listarAtendimentosAdminService()
            setTodosAtendimentos(dados)
        } catch (error) {
            toast.error(`Erro: ${error}`)
        } finally {
            setCarregando(false)
        }
    }

    useEffect(() => {
        buscar()
    }, [])

    const atendimentos = todosAtendimentos.filter(a => {
        const data = parseData(a.data)
        if (filtroInicio && data < filtroInicio) return false
        if (filtroFim && data > filtroFim) return false
        return true
    })

    // Cards — somente ativos
    const atendimentosAtivos = atendimentos.filter(a => a.status === 1)
    const totalDia = atendimentosAtivos.reduce((acc, a) => acc + a.valor, 0)
    const totalAtendimentos = atendimentosAtivos.length
    const ticketMedio = totalAtendimentos > 0 ? totalDia / totalAtendimentos : 0

    // Rankings — somente ativos
    const rankingServicos = Object.entries(
        atendimentosAtivos.reduce((acc: Record<string, number>, a) => {
            acc[a.servico] = (acc[a.servico] ?? 0) + 1
            return acc
        }, {})
    )
    .map(([servico, quantidade]) => ({ servico, quantidade }))
    .sort((a, b) => b.quantidade - a.quantidade)

    const porBarbeiro = Object.entries(
        atendimentosAtivos.reduce((acc: Record<string, { quantidade: number, total: number }>, a) => {
            if (!acc[a.barbeiro]) acc[a.barbeiro] = { quantidade: 0, total: 0 }
            acc[a.barbeiro].quantidade += 1
            acc[a.barbeiro].total += a.valor
            return acc
        }, {})
    ).map(([barbeiro, dados]) => ({ barbeiro, ...dados }))
    .sort((a, b) => b.quantidade - a.quantidade)

    return {
        carregando,
        totalDia,
        totalAtendimentos,
        ticketMedio,
        rankingServicos,
        porBarbeiro,
        atendimentos,
        filtroInicio,
        filtroFim,
        setFiltroInicio,
        setFiltroFim,
        recarregar: buscar  
    }
}