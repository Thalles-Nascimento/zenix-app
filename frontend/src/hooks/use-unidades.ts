import { useEffect, useState } from "react"
import { trace, SpanStatusCode } from "@opentelemetry/api"
import  api_url from "../enviroments/enviroments-dev"

const tracer = trace.getTracer("zenix-frontend")

interface Unidade {
    id: number
    nomeUnidade: string
}

interface UseUnidadesReturn {
    unidades: Unidade[]
    loading: boolean
    error: string | null
}

export function useUnidades(): UseUnidadesReturn {
    const [unidades, setUnidades] = useState<Unidade[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        const span = tracer.startSpan("hook.useUnidades.fetch")

        api_url.get<Unidade[]>("/unidades")
            .then((res) => {
                const data = res.data

                span.setAttribute("unidades.count", data?.length ?? 0)
                span.setAttribute("unidades.raw_type", typeof data)
                span.setAttribute("unidades.is_array", Array.isArray(data))

                // Garante que sempre é um array — evita crash no .map()
                if (!Array.isArray(data)) {
                    const msg = `Resposta inesperada: esperado array, recebido ${typeof data}`
                    span.setStatus({ code: SpanStatusCode.ERROR, message: msg })
                    span.recordException(new Error(msg))
                    setError(msg)
                    setUnidades([])
                    return
                }

                setUnidades(data)
                span.setStatus({ code: SpanStatusCode.OK })
            })
            .catch((err) => {
                const msg = err?.response?.data?.message ?? err?.message ?? "Erro desconhecido"
                span.recordException(err)
                span.setStatus({ code: SpanStatusCode.ERROR, message: msg })
                span.setAttribute("error.status", err?.response?.status ?? 0)
                setError(msg)
                setUnidades([])
                console.error("[useUnidades] Erro ao buscar unidades:", err)
            })
            .finally(() => {
                setLoading(false)
                span.end()
            })
    }, [])

    return { unidades, loading, error }
}
