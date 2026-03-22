export interface PlanoDTO {
    id: number
    planoDescricao: string
    valor: number
    servico: string[]
    limiteAtendimentos: number
}

export interface ClienteDTO {
    id: number
    nomeCliente: string
    telefone: string
    vezesRetorno: number
    atendimentosMes: number
    dataRenovacao: string
    status: number
    plano: PlanoDTO | null
}

