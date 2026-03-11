import { useEffect, useState } from "react"
import { listarAtendimentosAdminService } from "../services/atendimento-service"
import type { AtendimentoAdminProps } from "../types/dashboard"
import { parseData, hoje } from "../utils/date"

export function useDashboard() {
    const [todosAtendimentos, setTodosAtendimentos] = useState<AtendimentoAdminProps[]>([])
    const [carregando, setCarregando] = useState(true)
    const [filtroInicio, setFiltroInicio] = useState(hoje()) // ← começa com hoje
    const [filtroFim, setFiltroFim] = useState(hoje())       // ← começa com hoje

    const buscar = async () => {
        try {
            setCarregando(true)
            const dados = await listarAtendimentosAdminService()
            setTodosAtendimentos(dados.filter((a: AtendimentoAdminProps) => a.status === 1))
        } catch (error) {
            console.error(error)
        } finally {
            setCarregando(false)
        }
    }

    useEffect(() => {
        buscar()
    }, [])

    // Filtra pelo período selecionado
    const atendimentos = todosAtendimentos.filter(a => {
        const data = parseData(a.date)
        if (filtroInicio && data < filtroInicio) return false
        if (filtroFim && data > filtroFim) return false
        return true
    })

    // Cards
    const totalDia = atendimentos.reduce((acc, a) => acc + a.valor, 0)
    const totalAtendimentos = atendimentos.length
    const ticketMedio = totalAtendimentos > 0 ? totalDia / totalAtendimentos : 0

    // Ranking de serviços
    const rankingServicos = Object.entries(
        atendimentos.reduce((acc: Record<string, number>, a) => {
            acc[a.servico] = (acc[a.servico] ?? 0) + 1
            return acc
        }, {})
    )
    .map(([servico, quantidade]) => ({ servico, quantidade }))
    .sort((a, b) => b.quantidade - a.quantidade)

    // Atendimentos por barbeiro
    const porBarbeiro = Object.entries(
        atendimentos.reduce((acc: Record<string, { quantidade: number, total: number }>, a) => {
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