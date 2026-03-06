import { useState } from "react"

export function usePaginacao<T>(itens: T[], itensPorPagina: number = 10) {
    const [paginaAtual, setPaginaAtual] = useState(1)

    const totalPaginas = Math.ceil(itens.length / itensPorPagina)

    // Fatia o array para exibir só a página atual
    const itensPagina = itens.slice(
        (paginaAtual - 1) * itensPorPagina,
        paginaAtual * itensPorPagina
    )

    // Volta para página 1 quando os dados mudam (ex: ao aplicar filtro)
    const resetPagina = () => setPaginaAtual(1)

    return {
        itensPagina,
        paginaAtual,
        totalPaginas,
        setPaginaAtual,
        resetPagina,
        totalItens: itens.length
    }
}