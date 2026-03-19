export interface PlanoDTO {
    id: number
    planoDescricao: string
    valor: number
    limiteAtendimentos: number
}

export interface ClienteDTO {
    id: number
    nomeCliente: string
    telefone: string
    vezesRetorno: number
    atendimentosMes: number
    status: number
    plano: PlanoDTO | null
}

export interface ClienteDTOGeral {
    id: number
    nomeCliente: string
    vezesRetorno: number
}
