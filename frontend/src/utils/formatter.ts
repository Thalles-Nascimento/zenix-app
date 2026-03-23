// Formata para exibição: 12345678900 → 123.456.789-00
export function formatarCPF(cpf: string): string {
    const limpo = cpf.replace(/\D/g, '').slice(0, 11)
    return limpo
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d{1,2})$/, '$1-$2')
}

// Remove formatação: 123.456.789-00 → 12345678900
export function limparCPF(cpf: string): string {
    return cpf.replace(/\D/g, '')
}

// Formata telefone
export const formatarTelefone = (tel: string) => {
    if (!tel) return "—"
    const n = tel.replace(/\D/g, "")
    if (n.length === 13) return `+${n.slice(0,2)} (${n.slice(2,4)}) ${n.slice(4,9)}-${n.slice(9)}`
    if (n.length === 11) return `(${n.slice(0,2)}) ${n.slice(2,7)}-${n.slice(7)}`
    if (n.length === 10) return `(${n.slice(0,2)}) ${n.slice(2,6)}-${n.slice(6)}`
    return tel
}


// Remove formatação: (11) 99999-9999 → 11999999999
export function limparTelefone(telefone: string): string {
    return telefone.replace(/\D/g, '')
}