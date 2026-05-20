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

    const paginas = Array.from({ length: totalPaginas }, (_, i) => i + 1)
        .filter(p => p === 1 || p === totalPaginas || Math.abs(p - paginaAtual) <= 1)
        .reduce<(number | string)[]>((acc, p, i, arr) => {
            if (i > 0 && (p as number) - (arr[i - 1] as number) > 1) acc.push("...")
            acc.push(p)
            return acc
        }, [])

    return (
        <div className="sticky bottom-0 left-0 right-0 z-40 rounded-b-xl bg-black/95 border-t border-gray-800 backdrop-blur-sm">
            <div className="flex items-center justify-between px-4 py-2 max-w-screen-xl mx-auto">

                {/* Contador discreto */}
                <span className="text-gray-600 text-xs">
                    {inicio}–{fim} <span className="text-gray-700">de</span> {totalItens}
                </span>

                {/* Navegação */}
                <div className="flex items-center gap-0.5">
                    <button
                        onClick={() => onPaginaChange(paginaAtual - 1)}
                        disabled={paginaAtual === 1}
                        className="w-7 h-7 flex items-center justify-center rounded-lg text-gray-500 hover:text-white disabled:opacity-25 transition-colors text-sm"
                    >
                        ←
                    </button>

                    {paginas.map((p, i) =>
                        p === "..."
                            ? <span key={`e-${i}`} className="w-6 text-center text-gray-700 text-xs">·</span>
                            : (
                                <button
                                    key={p}
                                    onClick={() => onPaginaChange(p as number)}
                                    className={`w-7 h-7 rounded-full text-xs font-medium transition-colors
                                        ${paginaAtual === p
                                            ? "bg-orange-600 text-white"
                                            : "text-gray-500 hover:text-white"
                                        }`}
                                >
                                    {p}
                                </button>
                            )
                    )}

                    <button
                        onClick={() => onPaginaChange(paginaAtual + 1)}
                        disabled={paginaAtual === totalPaginas}
                        className="w-7 h-7 flex items-center justify-center rounded-lg text-gray-500 hover:text-white disabled:opacity-25 transition-colors text-sm"
                    >
                        →
                    </button>
                </div>
            </div>
        </div>
    )
}