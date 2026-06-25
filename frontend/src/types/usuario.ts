
export interface UserProps{
    id: number
    nome: string,
    email: string,
    cpf: string,
    unidade: UnidadeProps
    grupo: string,
    status: number
}

export interface UsuarioFormProps {
    nome: string
    email: string
    cpf: string
    unidade: number
    senha: string
    grupo: string
}

export interface UnidadeProps {
    id: number
    nome: string,
    endereco: string,
    status: number
}

export interface UnidadeFormProps {
    nome: string
    endereco: string
}

export interface UsuarioSimplificadoProps {
    id: number
    nome: string
    email: string
    grupo: string
    status: number
}

export interface UnidadeDetalheProps {
    id: number
    nome: string
    endereco: string
    status: number
    usuarios: UsuarioSimplificadoProps[]
}
