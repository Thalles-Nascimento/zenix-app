import { useEffect, useState } from "react"
import {
    atendimentoService,
    criarAtendimentoService,
    atualizarAtendimentoService,
    atualizarAtendimentoAdminService,
    deletarAtendimentoService,
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
            toast.error("Erro ao ler os atendimentos")
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
            toast.error("Campos inválidos")
        }
    }

    // Atualiza atendimento do próprio usuário (USER e ADMIN nos próprios atendimentos)
    const atualizarAtendimento = async (id: number, form: AtendimentoFormProps) => {
        try {
            await atualizarAtendimentoService(id, form)
            toast.success("Atendimento atualizado com sucesso!")
            await buscarAtendimentos()
        } catch (error) {
            toast.error("Erro ao atualizar o atendimento.")
        }
    }

    // Atualiza qualquer atendimento — exclusivo ADMIN (dashboard)
    const atualizarAtendimentoAdmin = async (id: number, form: AtendimentoFormProps) => {
        try {
            await atualizarAtendimentoAdminService(id, form)
            toast.success("Atendimento atualizado com sucesso!")
            await buscarAtendimentos()
        } catch (error) {
            toast.error("Erro ao atualizar o atendimento.")
        }
    }

    const deletarAtendimento = async (id: number) => {
        try {
            await deletarAtendimentoService(id)
            toast.success("Atendimento deletado com sucesso!")
            await buscarAtendimentos()
        } catch (error) {
            toast.error("Erro ao deletar o atendimento.")
        }
    }

    const dados = periodo === 'hoje' ? dadosHoje : dadosHistorico

    return { dados, carregando, periodo, setPeriodo, criarAtendimento, atualizarAtendimento, atualizarAtendimentoAdmin, deletarAtendimento }
}