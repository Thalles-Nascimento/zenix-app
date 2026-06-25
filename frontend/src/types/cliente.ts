export interface PlanoDTO {
    id: number
    descricao: string
    valor: number
    servico: string[]
    atendimentos: number
}

export interface ClienteDTO {
    id: number
    nome: string
    telefone: string
    retorno: number
    atendimentoMes: number
    dataRenovacao: string
    status: number
    planoId: PlanoDTO | null
}

