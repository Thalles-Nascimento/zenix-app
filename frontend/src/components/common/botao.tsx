interface BotaoProps{
    texto: string,
    color?: 'primary' | 'secondary' | 'sucess' | 'cancel' | 'delete'
    click: any,
    compact?: boolean
}

export function Botao(botaoProps: BotaoProps){
    const estilos: Record<string,string> = {
        primary:  'bg-orange-700 w-full text-white border border-gray-500 hover:bg-orange-500',
        secondary:  'bg-zinc-800 text-white hover:bg-gray-500',
        sucess:  'bg-orange-700 text-white border border-gray-500 hover:bg-orange-500',
        delete:  'bg-red-700 text-white border border-gray-500 hover:bg-red-500',
        cancel:  'bg-gray-700 text-white border border-gray-750 hover:bg-gray-500',
    }

    const colorKey = botaoProps.color ?? 'primary'
    let base = estilos[colorKey] ?? estilos['primary']
    const sizeClass = botaoProps.compact ? 'rounded-xl px-3 py-1.5 text-md' : 'rounded-xl px-4 py-2'

    if (botaoProps.compact) {
        base = base.replace('w-full', 'w-auto')
    }

    return <button type="button" className={`${base} ${sizeClass} outline-none notranslate`} onClick={botaoProps.click}>{botaoProps.texto}</button>

}
