// Converte "dd/MM/yyyy" para "yyyy-MM-dd"
export function parseData(dateStr: string): string {
    const [dia, mes, ano] = dateStr.split("/")
    return `${ano}-${mes}-${dia}`
}

// Retorna hoje no formato yyyy-MM-dd
export function hoje(): string {
    const agora = new Date()
    const ano = agora.getFullYear()
    const mes = String(agora.getMonth() + 1).padStart(2, '0')
    const dia = String(agora.getDate()).padStart(2, '0')
    return `${ano}-${mes}-${dia}`
}

// Retorna o início da semana (segunda-feira) no formato yyyy-MM-dd
export function inicioSemana(): string {
    const agora = new Date()
    const dia = agora.getDay()
    const diff = dia === 0 ? -6 : 1 - dia
    const segunda = new Date(agora)
    segunda.setDate(agora.getDate() + diff)
    return segunda.toISOString().split('T')[0]
}