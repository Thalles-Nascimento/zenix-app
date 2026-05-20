import { useEffect, useState } from "react"

export function SuccessScreen({ nome }: { nome: string }) {
    const [segundos, setSegundos] = useState(5)

    useEffect(() => {
        const timer = setInterval(() => {
            setSegundos(prev => prev - 1)
        }, 1000)

        if (segundos === 0) {
            window.location.reload()
        }

        return () => clearInterval(timer)
    }, [segundos])

    return (
        <section className="min-h-screen bg-black flex flex-col items-center justify-center gap-6">
            <img className="w-88" src="/assets/imagens/logo.png" alt="logo" />
            <div className="bg-gray-900 rounded-xl border border-gray-700 p-8 text-center max-w-sm w-full mx-4">
                <div className="w-16 h-16 bg-orange-600 rounded-full flex items-center justify-center mx-auto mb-4">
                    <span className="text-white text-2xl">✓</span>
                </div>
                <h2 className="text-white text-xl font-bold mb-2">Você está na fila!</h2>
                <p className="text-gray-400 text-sm">Aguarde ser chamado pelo barbeiro.</p>
                <p className="text-orange-500 font-medium mt-2">{nome}</p>
                <p className="text-gray-500 text-xs mt-4">
                    Voltando em <span className="text-orange-400">{segundos}</span> segundos...
                </p>
            </div>
        </section>
    )
}