export interface PlanoDTO {
    planoId: string
    planoDescricao: string
    planoValor: number
    planoServico: string[]
    planoAtendimentos: number
}

export interface ClienteDTO {
    id: string
    nome: string
    telefone: string
    retorno: number
    atendimentoMes: number
    dataRenovacao: string
    status: number
    planoId: string
    planoDescricao: string
    planoValor: number
    planoServico: string[]
    planoAtendimentos: number
}

