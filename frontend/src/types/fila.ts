export interface FilaFormProps {
    nomeCliente: string
    servico: string[]
    formaPagamento: string
    telefoneCliente: string
    idBarbeiro: number | null
    semPreferencia: boolean
    idUnidade: number
}

export interface FilaProps {
    id: number
    nomeCliente: string
    servico: string[]
    formaPagamento: string
    horario: string
    status: 'AGUARDANDO' | 'EM_ATENDIMENTO'
    semPreferencia: boolean
}