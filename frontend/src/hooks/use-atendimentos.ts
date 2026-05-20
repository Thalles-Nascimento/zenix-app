import { useEffect, useState } from "react"
import {
    atendimentoService,
    criarAtendimentoService,
    atualizarAtendimentoAdminService,
    deletarAtendimentoService,
    ativarAtendimentoService,
    listarHistorico
} from "../services/atendimento-service"
import type { DadosProps, AtendimentoFormProps } from "../types/atendimento"
import { toast } from "sonner"

type Periodo = 'hoje' | 'historico'

export function useAtendimentos() {
    const [dadosHoje, setDadosHoje] = useState<DadosProps[]>([])
    const [dadosHistorico, setDadosHistorico] = useState<DadosProps[]>([])
    const [carregando, setCarregando] = useState(true)
    const [periodo, setPeriodo] = useState<Periodo>('hoje')

    const buscarAtendimentos = async () => {
        try {
            setCarregando(true)
            const [hojeData, historicoData] = await Promise.all([
                atendimentoService(),
                listarHistorico()
            ])
            setDadosHoje(hojeData)
            setDadosHistorico(historicoData)
        } catch (error) {
            toast.error(`Erro: ${error}`)
        } finally {
            setCarregando(false)
        }
    }

    useEffect(() => {
        buscarAtendimentos()
    }, [])

    const criarAtendimento = async (form: AtendimentoFormProps) => {
        try {
            await criarAtendimentoService(form)
            toast.success("Atendimento criado com sucesso!")
            await buscarAtendimentos()
        } catch (error) {
            toast.error(`Erro: ${error}`)
        }
    }
    
    // Atualiza qualquer atendimento — exclusivo ADMIN
    const atualizarAtendimentoAdmin = async (id: number, form: AtendimentoFormProps) => {
        try {
            await atualizarAtendimentoAdminService(id, form)
            toast.success("Atendimento atualizado com sucesso!")
            await buscarAtendimentos()
        } catch (error) {
            toast.error(`Erro: ${error}`)
        }
    }

    const deletarAtendimento = async (id: number) => {
        try {
            await deletarAtendimentoService(id)
            toast.success("Atendimento deletado com sucesso!")
            await buscarAtendimentos()
        } catch (error) {
            toast.error(`Erro: ${error}`)
        }
    }

    const ativarAtendimento = async (id: number) => {
        try {
            await ativarAtendimentoService(id)
            toast.success("Atendimento reativado com sucesso!")
            await buscarAtendimentos()
        } catch (error) {
            toast.error(`Erro: ${error}`)
        }
    }

    const dados = periodo === 'hoje' ? dadosHoje : dadosHistorico

    return { dados, carregando, periodo, setPeriodo, criarAtendimento, atualizarAtendimentoAdmin, deletarAtendimento, ativarAtendimento }
}