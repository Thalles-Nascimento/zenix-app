
interface BotaoProps{
    texto: string,
    color?: 'primary' | 'secondary' | 'sucess' | 'cancel' | 'delete'
    click: any
}

export function Botao(botaoProps: BotaoProps){
    const estilos = {
        primary:  'bg-orange-700 w-full text-white border border-gray-500 hover:bg-orange-500',
        secondary:  'bg-zinc-800 text-white',
        sucess:  'bg-orange-700 text-white border border-gray-500 hover:bg-orange-500',
        delete:  'bg-red-700 text-white border border-gray-500 hover:bg-red-500',
        cancel:  'bg-gray-700 text-white border border-gray-750 hover:bg-gray-500',
    }

    return <button type="button" className={`${estilos[botaoProps.color!]} rounded-xl px-4 py-2 outline-none notranslate`} onClick={botaoProps.click}>{botaoProps.texto}</button>
    
}