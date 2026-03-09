import React from "react"
import { trace, SpanStatusCode } from "@opentelemetry/api"

interface Props {
    children: React.ReactNode
    fallback?: React.ReactNode
    componentName?: string
}

interface State {
    hasError: boolean
    error: Error | null
}

export class ErrorBoundary extends React.Component<Props, State> {
    constructor(props: Props) {
        super(props)
        this.state = { hasError: false, error: null }
    }

    static getDerivedStateFromError(error: Error): State {
        return { hasError: true, error }
    }

    componentDidCatch(error: Error, info: React.ErrorInfo) {
        const tracer = trace.getTracer("zenix-frontend")
        const span = tracer.startSpan(`error.boundary.${this.props.componentName ?? "unknown"}`)

        span.setStatus({ code: SpanStatusCode.ERROR, message: error.message })
        span.recordException(error)
        span.setAttribute("component", this.props.componentName ?? "unknown")
        span.setAttribute("react.component_stack", info.componentStack ?? "")
        span.setAttribute("error.type", error.name)
        span.setAttribute("error.message", error.message)
        span.end()

        console.error(`[ErrorBoundary] Erro em ${this.props.componentName}:`, error, info)
    }

    render() {
        if (this.state.hasError) {
            return this.props.fallback ?? (
                <div className="p-4 text-red-500 text-sm">
                    Erro ao renderizar componente. Tente novamente.
                </div>
            )
        }
        return this.props.children
    }
}
