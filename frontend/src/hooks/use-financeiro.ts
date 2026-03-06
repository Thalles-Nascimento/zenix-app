import { useState, useEffect } from "react"
import { listarHistorico } from "../services/atendimento-service"
import type { DadosProps } from "../types/atendimento"
import { hoje, inicioSemana, parseData } from "../utils/date"


export function useFinanceiro() {
    const [atendimentos, setAtendimentos] = useState<DadosProps[]>([])
    const [carregando, setCarregando] = useState(true)
    const [filtroInicio, setFiltroInicio] = useState("")
    const [filtroFim, setFiltroFim] = useState("")

    useEffect(() => {
        const buscar = async () => {
            try {
                const dados = await listarHistorico()
                setAtendimentos(dados.filter((a: DadosProps) => a.status === 1))
            } catch (error) {
                console.error(error)
            } finally {
                setCarregando(false)
            }
        }
        buscar()
    }, [])

    // Filtra por período selecionado
    const atendimentosFiltrados = atendimentos.filter(a => {
        const data = parseData(a.date)
        if (filtroInicio && data < filtroInicio) return false
        if (filtroFim && data > filtroFim) return false
        return true
    })

    // Cards — totais fixos independente do filtro
    const totalDia = atendimentos
        .filter(a => parseData(a.date) === hoje())
        .reduce((acc, a) => acc + a.valor, 0)

    console.log(hoje())

    const totalSemana = atendimentos
        .filter(a => {
            const data = parseData(a.date)
            return data >= inicioSemana() && data <= hoje()
        })
        .reduce((acc, a) => acc + a.valor, 0)

    const totalMes = atendimentos
        .filter(a => {
            const [, mes, ano] = a.date.split("/")
            const agora = new Date()
            return Number(mes) === agora.getMonth() + 1 && Number(ano) === agora.getFullYear()
        })
        .reduce((acc, a) => acc + a.valor, 0)

    const ticketMedio = atendimentosFiltrados.length > 0
        ? atendimentosFiltrados.reduce((acc, a) => acc + a.valor, 0) / atendimentosFiltrados.length
        : 0

    const totalPeriodo = atendimentosFiltrados.reduce((acc, a) => acc + a.valor, 0)

    // Totais por forma de pagamento
    const porFormaPagamento = Object.entries(
        atendimentosFiltrados.reduce((acc: Record<string, number>, a) => {
            acc[a.formaPagamento] = (acc[a.formaPagamento] ?? 0) + a.valor
            return acc
        }, {})
    ).map(([forma, total]) => ({ forma, total })).sort((a, b) => b.total - a.total)

    // Dados para o gráfico
    const dadosGrafico = Object.values(
        atendimentosFiltrados.reduce((acc: Record<string, { data: string, total: number, quantidade: number }>, a) => {
            if (!acc[a.date]) acc[a.date] = { data: a.date, total: 0, quantidade: 0 }
            acc[a.date].total += a.valor
            acc[a.date].quantidade += 1
            return acc
        }, {})
    ).sort((a, b) => parseData(a.data) > parseData(b.data) ? 1 : -1)

    return {
        carregando,
        totalDia,
        totalSemana,
        totalMes,
        totalPeriodo,
        ticketMedio,
        porFormaPagamento,
        atendimentosFiltrados,
        dadosGrafico,
        filtroInicio,
        filtroFim,
        setFiltroInicio,
        setFiltroFim
    }
}