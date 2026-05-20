interface InputProps {
    tipo: string,
    placeHolder: string,
    onChange: (e: React.ChangeEvent<HTMLInputElement>) => void
    value: string
    propriedades?: 'login' | 'insert'
}

export function Input(inputProps: InputProps){
    const estilos = {
        login:  'bg-gray-800 w-full text-orange-400 border border-gray-600 focus:border-orange-700',
        insert:  'bg-zinc-800 text-white'
    }

    return <input value={inputProps.value} onChange={inputProps.onChange} type={inputProps.tipo} placeholder={inputProps.placeHolder} className={`${estilos[inputProps.propriedades!]} rounded-lg px-4 py-2 outline-none my-2`}/>
}