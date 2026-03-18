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

// Formata telefone brasileiro: 11999999999 → (11) 99999-9999
export function formatarTelefone(valor: string): string {
    const limpo = valor.replace(/\D/g, '').slice(0, 11)
    if (limpo.length <= 10) {
        return limpo
            .replace(/(\d{2})(\d)/, '($1) $2')
            .replace(/(\d{4})(\d)/, '$1-$2')
    }
    return limpo
        .replace(/(\d{2})(\d)/, '($1) $2')
        .replace(/(\d{5})(\d)/, '$1-$2')
}


// Remove formatação: (11) 99999-9999 → 11999999999
export function limparTelefone(telefone: string): string {
    return telefone.replace(/\D/g, '')
}