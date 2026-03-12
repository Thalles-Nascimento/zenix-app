interface Props {
    paginaAtual: number
    totalPaginas: number
    totalItens: number
    itensPorPagina: number
    onPaginaChange: (pagina: number) => void
}

export function Paginacao({ paginaAtual, totalPaginas, totalItens, itensPorPagina, onPaginaChange }: Props) {
    if (totalPaginas <= 1) return null

    const inicio = (paginaAtual - 1) * itensPorPagina + 1
    const fim = Math.min(paginaAtual * itensPorPagina, totalItens)

    return (
        <div className="flex items-center justify-between px-2 py-3">
            <p className="text-gray-400 text-sm">
                Mostrando <span className="text-white">{inicio}–{fim}</span> de <span className="text-white">{totalItens}</span>
            </p>
            <div className="flex items-center gap-1">
                <button
                    onClick={() => onPaginaChange(paginaAtual - 1)}
                    disabled={paginaAtual === 1}
                    className="px-3 py-1.5 rounded-lg text-sm bg-gray-900 text-white hover:bg-gray-700 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
                >
                    ← Anterior
                </button>

                {Array.from({ length: totalPaginas }, (_, i) => i + 1)
                    .filter(p => p === 1 || p === totalPaginas || Math.abs(p - paginaAtual) <= 1)
                    .reduce<(number | string)[]>((acc, p, i, arr) => {
                        if (i > 0 && (p as number) - (arr[i - 1] as number) > 1) acc.push('...')
                        acc.push(p)
                        return acc
                    }, [])
                    .map((p, i) => p === '...'
                        ? <span key={`ellipsis-${i}`} className="px-2 text-gray-500">...</span>
                        : (
                            <button
                                key={p}
                                onClick={() => onPaginaChange(p as number)}
                                className={`w-8 h-8 rounded-lg text-sm font-medium transition-colors
                                    ${paginaAtual === p
                                        ? 'bg-orange-600 text-white'
                                        : 'bg-gray-800 text-gray-300 hover:bg-gray-700'
                                    }`}
                            >
                                {p}
                            </button>
                        )
                    )
                }

                <button
                    onClick={() => onPaginaChange(paginaAtual + 1)}
                    disabled={paginaAtual === totalPaginas}
                    className="px-3 py-1.5 rounded-lg text-sm bg-gray-900 text-white hover:bg-gray-700 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
                >
                    Próxima →
                </button>
            </div>
        </div>
    )
}