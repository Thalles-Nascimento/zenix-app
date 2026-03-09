import { WebTracerProvider } from '@opentelemetry/sdk-trace-web'
import { BatchSpanProcessor } from '@opentelemetry/sdk-trace-base'
import { OTLPTraceExporter } from '@opentelemetry/exporter-trace-otlp-http'
import { ZoneContextManager } from '@opentelemetry/context-zone'
import { registerInstrumentations } from '@opentelemetry/instrumentation'
import { FetchInstrumentation } from '@opentelemetry/instrumentation-fetch'
import { XMLHttpRequestInstrumentation } from '@opentelemetry/instrumentation-xml-http-request'
import { DocumentLoadInstrumentation } from '@opentelemetry/instrumentation-document-load'
import { UserInteractionInstrumentation } from '@opentelemetry/instrumentation-user-interaction'
import { ATTR_SERVICE_NAME, ATTR_SERVICE_VERSION } from '@opentelemetry/semantic-conventions'
import { resourceFromAttributes } from '@opentelemetry/resources'


const resource = resourceFromAttributes({
    [ATTR_SERVICE_NAME]: 'zenix-frontend',
    [ATTR_SERVICE_VERSION]: '1.0.0',
    'deployment.environment': import.meta.env.MODE,
})

const exporter = new OTLPTraceExporter({
    url: `${import.meta.env.VITE_OTEL_URL ?? ''}/v1/traces`,
})

const provider = new WebTracerProvider({
    resource,
    spanProcessors: [new BatchSpanProcessor(exporter)],
})

provider.register({ contextManager: new ZoneContextManager() })

registerInstrumentations({
    instrumentations: [
        new FetchInstrumentation({ propagateTraceHeaderCorsUrls: [/.*/], clearTimingResources: true }),
        new XMLHttpRequestInstrumentation({ propagateTraceHeaderCorsUrls: [/.*/] }),
        new DocumentLoadInstrumentation(),
        new UserInteractionInstrumentation(),
    ],
})

export const tracer = provider.getTracer('zenix-frontend')