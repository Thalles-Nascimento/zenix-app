export interface AtendimentoProps {
    id: string
    descricao: string
    servico: string[]
    valor: number
    formaPagamento: string
    data: string
    status: number
    observacao?: string
}

export interface AtendimentoFormProps {
    descricao: string
    servico: string[]
    valor: number
    formaPagamento: string
    observacao?: string
}

export interface AtendimentoAdminProps {
    id: number
    descricao: string
    servico: string[]
    valor: number
    formaPagamento: string
    data: string
    status: number
    barbeiro: string
    observacao?: string
}

export interface TotalCliente{
    quantidade: number
    total: number
}
