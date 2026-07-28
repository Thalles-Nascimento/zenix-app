import React from "react"
import { Paginacao } from "../pagination"

export type PaginationProps = {
  paginaAtual: number
  totalPaginas: number
  totalItens: number
  itensPorPagina: number
  onPaginaChange: (pagina: number) => void
}

type Props = {
  table?: React.ReactNode
  cards?: React.ReactNode
  pagination?: PaginationProps
  infoLeft?: React.ReactNode
}

export function TableLayout({ table, cards, pagination, infoLeft }: Props) {
  return (
    <div className="overflow-x-auto rounded-xl border border-gray-700 notranslate bg-black p-2">

      {/* Desktop table (hidden on mobile) */}
      <div className="hidden md:block">{table}</div>

      {/* Mobile cards (hidden on desktop) */}
      <div className="md:hidden">{cards}</div>

      {/* Pagination / info — render only one instance here */}
      {pagination && (
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 p-4">
          <div className="text-sm text-gray-400">{infoLeft ?? (
            <>
              Mostrando <span className="text-white font-semibold">{pagination.totalItens > 0 ? Math.min(pagination.itensPorPagina, pagination.totalItens) : 0}</span> de <span className="text-white font-semibold">{pagination.totalItens}</span>
            </>
          )}</div>

          <Paginacao
            paginaAtual={pagination.paginaAtual}
            totalPaginas={pagination.totalPaginas}
            totalItens={pagination.totalItens}
            itensPorPagina={pagination.itensPorPagina}
            onPaginaChange={pagination.onPaginaChange}
          />
        </div>
      )}

    </div>
  )
}

export default TableLayout
