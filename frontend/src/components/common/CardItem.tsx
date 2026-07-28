import React from "react"

type Props = {
  title?: React.ReactNode
  subtitle?: React.ReactNode
  rightTop?: React.ReactNode
  rightBottom?: React.ReactNode
  status?: "active" | "inactive" | "finalizado" | string
  inactive?: boolean
  footerRight?: React.ReactNode
  onClick?: () => void
  children?: React.ReactNode
}

export function CardItem({ title, subtitle, rightTop, rightBottom, status, inactive, footerRight, onClick, children }: Props) {
  return (
    <div
      onClick={onClick}
      className={`bg-gray-950 rounded-lg p-4 mb-3 border border-gray-700 ${inactive ? "opacity-50" : ""} ${onClick ? "cursor-pointer" : ""}`}
    >
      <div className="flex justify-between items-start">
        <div className="flex-1 min-w-0">
          {title && <div className="text-white font-semibold truncate max-w-[220px]">{title}</div>}
          {subtitle && <div className="text-sm text-gray-300">{subtitle}</div>}
          {children}
        </div>
        <div className="flex flex-col items-end">
          {rightTop && <div className="text-xs text-gray-400">{rightTop}</div>}
          {rightBottom && <div className="text-xs text-gray-400">{rightBottom}</div>}
        </div>
      </div>

      {/* Footer area: show status on left and optional footerRight on right */}
      {(status || footerRight) && (
        <div className="mt-3 flex items-center justify-between">
          <div>
            {status && (
              <span className={status === "active" || status === "finalizado" ? 'inline-flex items-center px-2 py-1 rounded-full text-xs bg-orange-700 text-white' : 'inline-flex items-center px-2 py-1 rounded-full text-xs bg-gray-800 text-gray-300'}>
                {status === "active" ? 'Ativo' : status === 'finalizado' ? 'Finalizado' : 'Inativo'}
              </span>
            )}
          </div>
          <div>
            {footerRight}
          </div>
        </div>
      )}
    </div>
  )
}

export default CardItem
