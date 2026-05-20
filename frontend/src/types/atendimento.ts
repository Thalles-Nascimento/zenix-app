export interface DadosProps {
    id: number
    descricao: string
    servico: string[]
    valor: number
    formaPagamento: string
    date: string
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

export interface TotalCliente{
    quantidade: number
    total: number
}
