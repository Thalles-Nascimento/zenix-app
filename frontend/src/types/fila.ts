export interface FilaFormProps {
    nomeCliente: string
    servico: string[]
    formaPagamento: string
    telefoneCliente: string
    idBarbeiro: number
}

export interface FilaProps {
    id: number
    nomeCliente: string
    servico: string[]
    formaPagamento: string
    horario: string
    status: 'AGUARDANDO' | 'EM_ATENDIMENTO'
}
