import { useState } from "react"
import { useClientes } from "../hooks/use-cliente-admin"
import { usePlanos } from "../hooks/use-planos"
import { Badge } from "../components/ui/badge"
import { Spinner } from "../components/ui/spinner"
import { Toaster, toast } from "sonner"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "../components/ui/dialog"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "../components/ui/select"
import { Label } from "../components/ui/label"
import { Input } from "../components/ui/input"
import { Botao } from "../components/common/botao"
import { InputTelefone } from "../components/common/input-telefone"
import type { ClienteDTO } from "../types/cliente"
import { usePaginacao } from "../hooks/use-pagination"
import { Paginacao } from "../components/pagination"
import { formatarTelefoneCliente } from "@/utils/formatter"
import { ModalConfirmacao } from "@/components/common/modal-confirmacao-component"
import { parseDataBrazil } from "@/utils/date"
import { Button } from "../components/ui/button"
import { UserPlus } from "lucide-react"

type ModalTipo = "editar" | "plano" | "novo" | null

export default function ClientesPage() {
    const { clientes, carregando, filtro, setFiltro, criarCliente, atualizarCliente, deletarCliente, vincularPlano, desvincularPlano, ativarCliente } = useClientes()
    const { planos } = usePlanos()

    const [clienteSelecionado, setClienteSelecionado] = useState<ClienteDTO | null>(null)
    const [modalTipo, setModalTipo] = useState<ModalTipo>(null)
    const [planoSelecionado, setPlanoSelecionado] = useState("")
    const [busca, setBusca] = useState("")
    const [confirmacaoAberta, setConfirmacaoAberta] = useState(false)

    const [formNome, setFormNome] = useState("")
    const [formTelefone, setFormTelefone] = useState("")

    const clientesFiltrados = clientes.filter(c =>
        c.nome.toLowerCase().includes(busca.toLowerCase()) ||
        c.telefone?.includes(busca)
    )

    // mobile-first: show fewer items per page on mobile; using 7 to match atendimento behavior
    const { itensPagina, paginaAtual, totalPaginas, totalItens, setPaginaAtual } = usePaginacao(clientesFiltrados, 7)

    const formatBRL = (valor?: number | null) => {
        const v = typeof valor === "number" && !isNaN(valor) ? valor : 0
        return v.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })
    }

    const abrirNovo = () => {
        setFormNome("")
        setFormTelefone("")
        setModalTipo("novo")
    }

    const abrirEditar = (cliente: ClienteDTO) => {
        setClienteSelecionado(cliente)
        setFormNome(cliente.nome)
        setFormTelefone(cliente.telefone)
        setPlanoSelecionado(cliente.planoId ? String(cliente.planoId) : "")
        setModalTipo("editar")
    }

    const fecharModal = () => {
        setModalTipo(null)
        setClienteSelecionado(null)
        setFormNome("")
        setFormTelefone("")
        setPlanoSelecionado("")
    }

    const handleCriar = async () => {
        if (!formNome.trim() || !formTelefone) {
            toast.error("Preencha nome e telefone!")
            return
        }
        try {
            await criarCliente(formNome.trim(), formTelefone.replace(/\D/g, ""))
            toast.success("Cliente criado!")
            fecharModal()
        } catch {
            toast.error("Erro ao criar cliente.")
        }
    }

    const handleAtualizar = async () => {
        if (!clienteSelecionado || !formNome.trim()) {
            toast.error("Preencha o nome!")
            return
        }
        try {
            await atualizarCliente(clienteSelecionado.id, formNome.trim(), formTelefone.replace(/\D/g, ""))
            toast.success("Cliente atualizado!")
            fecharModal()
        } catch {
            toast.error("Erro ao atualizar cliente.")
        }
    }

    const handleDeletar = async () => {
        if (!clienteSelecionado) return
        try {
            await deletarCliente(clienteSelecionado.id)
            toast.success("Cliente excluído!")
            setConfirmacaoAberta(false)
            fecharModal()
        } catch {
            toast.error("Erro ao excluir cliente.")
        }
    }

    const handleVincular = async () => {
        if (!clienteSelecionado || !planoSelecionado) return
        try {
            await vincularPlano(clienteSelecionado.id, planoSelecionado)
            toast.success("Plano vinculado!")
            // refresh selection
            setPlanoSelecionado("")
            fecharModal()
        } catch {
            toast.error("Cliente já possui um plano")
        }
    }

    const handleDesvincular = async () => {
        if (!clienteSelecionado) return
        try {
            await desvincularPlano(clienteSelecionado.id)
            toast.success("Plano removido!")
            fecharModal()
        } catch {
            toast.error("Erro ao remover plano.")
        }
    }

    const getAlerta = (cliente: ClienteDTO) => {
        if (!cliente.planoId) return null
        const percentual = cliente.atendimentoMes / cliente.planoAtendimentos
        if (cliente.atendimentoMes >= cliente.planoAtendimentos) return "limite"
        if (percentual >= 0.8) return "aviso"
        return null
    }

    if (carregando) {
        return (
            <div className="w-full bg-black flex items-center justify-center py-20">
                <Badge variant="secondary"><Spinner />Carregando...</Badge>
            </div>
        )
    }

    return (
        <div className="max-w-[1100px] mx-auto px-4 sm:px-6">
            <Toaster richColors position="top-center" />

            {/* Topbar - mobile-first */}
            <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6 notranslate">
                <div className="flex items-center gap-3">
                    <h1 className="text-white text-2xl font-bold">Clientes</h1>
                </div>

                <div className="flex flex-wrap items-center gap-3">
                    <div className="flex items-center gap-2">
                        <button onClick={() => setFiltro("ativos")} className={`px-3 py-1 rounded-full text-sm font-medium transition-colors ${filtro === "ativos" ? "bg-orange-700 text-white" : "text-gray-300 hover:text-white"}`}>
                            Ativos
                        </button>
                        <button onClick={() => setFiltro("todos")} className={`px-3 py-1 rounded-full text-sm font-medium transition-colors ${filtro === "todos" ? "bg-yellow-500 text-white" : "text-gray-300 hover:text-white"}`}>
                            Todos
                        </button>
                    </div>

                    <div className="flex items-center gap-2">
                        <Button variant="default" onClick={abrirNovo} title="Novo Cliente"><UserPlus /></Button>
                        {/* search visible on mobile (mobile-first) */}
                        <div className="w-full sm:w-72">
                            <input
                                type="text"
                                placeholder="Buscar por nome ou telefone..."
                                value={busca}
                                onChange={(e) => setBusca(e.target.value)}
                                className="bg-gray-900 border border-gray-700 text-white text-sm rounded-lg px-3 py-2 outline-none w-full"
                            />
                        </div>
                    </div>
                </div>
            </div>

            {/* Alerta de clientes no limite */}
            {clientes.some(c => getAlerta(c) === "limite") && (
                <div className="bg-red-900/30 border border-red-700 rounded-xl px-4 py-3 flex items-center gap-2 mb-4">
                    <span className="text-red-400 font-bold text-sm">⚠ Atenção:</span>
                    <span className="text-red-300 text-sm">
                        {clientes.filter(c => getAlerta(c) === "limite").length} cliente(s) atingiram o limite do plano este mês!
                    </span>
                </div>
            )}

            {/* Mobile: cards (match atendimento mobile) */}
            <div className="md:hidden">
                {itensPagina.length === 0 ? (
                    <div className="px-6 py-12 text-center text-gray-500">Nenhum cliente encontrado</div>
                ) : (
                    itensPagina.map(cliente => (
                        <div
                            key={cliente.id}
                            className={`bg-gray-900 rounded-lg p-4 mb-3 ${cliente.status === -1 ? "opacity-50" : ""} cursor-pointer`}
                            onClick={() => abrirEditar(cliente)}
                        >
                            <div className="flex justify-between items-start">
                                <div className="flex-1 min-w-0">
                                    <div className="flex items-center justify-between gap-3">
                                        <div className="flex flex-col">
                                            <span className="text-white font-semibold truncate max-w-[220px]">{cliente.nome}</span>
                                            <span className="text-sm text-gray-300">{formatarTelefoneCliente(cliente.telefone ?? "")}</span>
                                        </div>
                                        <div className="flex flex-col items-end">
                                            <span className="text-xs text-gray-400">{cliente.planoId ? cliente.planoDescricao : "Avulso"}</span>
                                            <span className="text-xs text-gray-400">{cliente.planoId ? parseDataBrazil(cliente.dataRenovacao) : "—"}</span>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div className="mt-3 flex items-center justify-between">
                                <div>
                                    {cliente.planoId ? (
                                        <div className="flex items-center gap-2">
                                            <div className="w-20 bg-gray-700 rounded-full h-1.5">
                                                <div
                                                    className={`h-1.5 rounded-full ${
                                                        getAlerta(cliente) === "limite" ? "bg-red-500" :
                                                        getAlerta(cliente) === "aviso" ? "bg-yellow-500" :
                                                        "bg-green-500"
                                                    }`}
                                                    style={{ width: `${Math.min((cliente.atendimentoMes / cliente.planoAtendimentos) * 100, 100)}%` }}
                                                />
                                            </div>
                                            <span className="text-xs font-medium text-gray-300">{cliente.atendimentoMes}/{cliente.planoAtendimentos}</span>
                                        </div>
                                    ) : (
                                        <span className="text-xs text-gray-400">Sem plano</span>
                                    )}
                                </div>
                                <div className="text-right">
                                    <span className={cliente.status === 1 ? 'inline-flex items-center px-2 py-1 rounded-full text-xs bg-orange-700 text-white' : 'inline-flex items-center px-2 py-1 rounded-full text-xs bg-gray-800 text-gray-300'}>
                                        {cliente.status === 1 ? 'Ativo' : 'Inativo'}
                                    </span>
                                </div>
                            </div>
                        </div>
                    ))
                )}
            </div>

            {/* Desktop: tabela (hidden on mobile) */}
            <div className="hidden md:block overflow-x-auto rounded-xl border border-gray-700 notranslate bg-black p-2">
                <table className="w-full text-sm text-left text-gray-300 min-w-150 table-fixed md:table">
                    <thead className="text-xs uppercase bg-gray-850 border-b border-gray-700">
                        <tr className="text-gray-300">
                            <th className="px-4 py-3">CLIENTE</th>
                            <th className="px-4 py-3">TELEFONE</th>
                            <th className="px-4 py-3">PLANO</th>
                            <th className="px-4 py-3">USO DO MÊS</th>
                            <th className="px-4 py-3">TOTAL DE VISITAS</th>
                            <th className="px-4 py-3">DATA RENOVAÇÃO</th>
                        </tr>
                    </thead>
                    <tbody>
                        {itensPagina.length === 0 ? (
                            <tr>
                                <td colSpan={6} className="px-6 py-8 text-center text-gray-500">
                                    Nenhum cliente encontrado
                                </td>
                            </tr>
                        ) : (
                            itensPagina.map(cliente => {
                                const alerta = getAlerta(cliente)
                                const inativo = cliente.status === -1
                                return (
                                    <tr
                                        key={cliente.id}
                                        className={`bg-black border-b border-gray-800 hover:bg-gray-900 transition-colors ${inativo ? "opacity-50" : ""} cursor-pointer`}
                                        onClick={() => abrirEditar(cliente)}
                                    >
                                        <td className="px-4 py-4 font-medium text-white">{cliente.nome}</td>
                                        <td className="px-4 py-4 text-gray-400">{formatarTelefoneCliente(cliente.telefone ?? "")}</td>
                                        <td className="px-4 py-4">{cliente.planoId ? (
                                            <span className="bg-orange-500/20 text-orange-400 text-xs font-semibold px-2 py-1 rounded-full border border-orange-500/30">{cliente.planoDescricao}</span>
                                        ) : (
                                            <span className="text-gray-500 text-xs">Avulso</span>
                                        )}</td>
                                        <td className="px-4 py-4">
                                            {cliente.planoId ? (
                                                <div className="flex items-center gap-2">
                                                    <div className="w-20 bg-gray-700 rounded-full h-1.5">
                                                        <div className={`h-1.5 rounded-full transition-all ${alerta === "limite" ? "bg-red-500" : alerta === "aviso" ? "bg-yellow-500" : "bg-green-500"}`} style={{ width: `${Math.min((cliente.atendimentoMes / cliente.planoAtendimentos ) * 100, 100)}%` }} />
                                                    </div>
                                                    <span className={`text-xs font-medium ${alerta === "limite" ? "text-red-400" : alerta === "aviso" ? "text-yellow-400" : "text-gray-400"}`}>{cliente.atendimentoMes}/{cliente.planoAtendimentos}{alerta === "limite" && " ⚠"}</span>
                                                </div>
                                            ) : (
                                                <span className="text-gray-500 text-xs">—</span>
                                            )}
                                        </td>
                                        <td className="px-4 py-4 text-gray-300">{cliente.retorno}</td>
                                        <td className="px-4 py-4">{cliente.planoId ? (
                                            <span className="bg-orange-500/20 text-orange-400 text-xs font-semibold px-2 py-1 rounded-full border border-orange-500/30">{parseDataBrazil(cliente.dataRenovacao)}</span>
                                        ) : (
                                            <span className="text-gray-500 text-xs">—</span>
                                        )}</td>
                                    </tr>
                                )
                            })
                        )}
                    </tbody>
                </table>

                <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 p-4">
                    <div className="text-sm text-gray-400">Mostrando <span className="text-white font-semibold">{itensPagina.length}</span> de <span className="text-white font-semibold">{totalItens}</span> clientes</div>
                    <Paginacao
                        paginaAtual={paginaAtual}
                        totalPaginas={totalPaginas}
                        totalItens={totalItens}
                        itensPorPagina={7}
                        onPaginaChange={setPaginaAtual}
                    />
                </div>
            </div>

            {/* Modal Novo Cliente */}
            <Dialog open={modalTipo === "novo"} onOpenChange={fecharModal}>
                <DialogContent className="notranslate bg-black border border-gray-700 text-white w-[calc(100vw-2rem)] max-w-md max-h-[70vh] overflow-y-auto rounded-lg shadow-sm">
                    <div className="px-4 py-3 border-b border-gray-800">
                        <DialogHeader>
                            <DialogTitle className="text-white notranslate text-lg font-semibold">Novo Cliente</DialogTitle>
                        </DialogHeader>
                    </div>
                    <div className="p-4 flex flex-col gap-4 notranslate">
                        <div>
                            <Label className="text-gray-300">Nome</Label>
                            <Input className="mt-1 bg-gray-900 border-gray-700 text-white" placeholder="Nome completo" value={formNome} onChange={(e) => setFormNome(e.target.value)} />
                        </div>
                        <div>
                            <Label className="text-gray-300">Telefone</Label>
                            <InputTelefone value={formTelefone} onChange={setFormTelefone} />
                        </div>
                        <div className="mt-6 flex flex-col sm:flex-row sm:items-center sm:justify-center gap-3">
                            <div className="w-full sm:w-auto">
                                <Botao texto="Criar Cliente" color="sucess" click={handleCriar} />
                            </div>
                            <div className="w-full sm:w-auto">
                                <Botao texto="Cancelar" color="cancel" click={fecharModal} />
                            </div>
                        </div>
                    </div>
                </DialogContent>
            </Dialog>

            {/* Modal Editar Cliente - agora inclui gerenciamento de plano */}
            <Dialog open={modalTipo === "editar"} onOpenChange={fecharModal}>
                <DialogContent className="notranslate bg-black border border-gray-700 text-white w-[calc(100vw-2rem)] max-w-md max-h-[70vh] overflow-y-auto rounded-lg shadow-sm">
                    <div className="px-4 py-3 border-b border-gray-800">
                        <DialogHeader>
                            <DialogTitle className="text-white notranslate text-lg font-semibold">Editar Cliente</DialogTitle>
                        </DialogHeader>
                    </div>
                    <div className="p-4 flex flex-col gap-4 notranslate">
                        <div>
                            <Label className="text-gray-300">Nome</Label>
                            <Input className="mt-1 bg-gray-900 border-gray-700 text-white" value={formNome} onChange={(e) => setFormNome(e.target.value)} />
                        </div>
                        <div>
                            <Label className="text-gray-300">Telefone</Label>
                            <InputTelefone value={formTelefone} onChange={setFormTelefone} />
                        </div>

                        {/* Plano management moved into edit modal */}
                        <div>
                            {clienteSelecionado?.planoId && (
                                <div className="bg-gray-900 rounded-lg p-3 border border-gray-700 notranslate mb-4">
                                    <div className="flex justify-between mb-2">
                                        <p className="text-gray-400 text-xs uppercase tracking-widest">Uso do Mês</p>
                                        <p className={`text-xs font-bold ${getAlerta(clienteSelecionado) === "limite" ? "text-red-400" : getAlerta(clienteSelecionado) === "aviso" ? "text-yellow-400" : "text-green-400"}`}>
                                            {clienteSelecionado.atendimentoMes}/{clienteSelecionado.planoAtendimentos}
                                            {getAlerta(clienteSelecionado) === "limite" && " — LIMITE ⚠"}
                                        </p>
                                    </div>
                                    <div className="w-full bg-gray-700 rounded-full h-2 mb-2">
                                        <div className={`h-2 rounded-full ${getAlerta(clienteSelecionado) === "limite" ? "bg-red-500" : getAlerta(clienteSelecionado) === "aviso" ? "bg-yellow-500" : "bg-green-500"}`} style={{ width: `${Math.min((clienteSelecionado.atendimentoMes / clienteSelecionado.planoAtendimentos) * 100, 100)}%` }} />
                                    </div>
                                    <div className="flex justify-between">
                                        <p className="text-gray-400 text-xs">Plano atual: <span className="text-orange-400 font-semibold">{clienteSelecionado.planoDescricao}</span> — {formatBRL(clienteSelecionado.planoValor)}/mês</p>
                                        <p className="text-gray-400 text-xs">Renovação: <span className="text-orange-400 font-semibold">{clienteSelecionado.dataRenovacao}</span></p>
                                    </div>
                                </div>
                            )}

                            <Label className="text-gray-300">{clienteSelecionado?.planoId ? "Trocar Plano" : "Vincular Plano"}</Label>
                        <div className="mt-2 flex gap-2 items-end">
                            <div className="flex-1">
                                <Select value={planoSelecionado} onValueChange={setPlanoSelecionado}>
                                    <SelectTrigger className="mt-1 bg-gray-900 border-gray-700 text-white w-full notranslate">
                                        <SelectValue placeholder="Adicione um plano" />
                                    </SelectTrigger>
                                    <SelectContent className="bg-black border-gray-700 text-white notranslate">
                                        {planos.map(p => (
                                            <SelectItem key={p.planoId} value={String(p.planoId)} className="notranslate">{p.planoDescricao} — {formatBRL(p.planoValor)} ({p.planoAtendimentos} atend./Mês)</SelectItem>
                                        ))}
                                    </SelectContent>
                                </Select>
                            </div>
                            {clienteSelecionado?.planoId ? (
                                <button
                                    onClick={handleDesvincular}
                                    className="bg-red-600/30 hover:bg-red-600/50 text-red-400 text-xs font-bold px-2 py-1 my-1 rounded-md transition-colors border border-red-600/30"
                                    title="Remover plano"
                                >
                                    −
                                </button>
                            ) : (
                                <button
                                    onClick={handleVincular}
                                    disabled={!planoSelecionado || !clienteSelecionado}
                                    className="bg-orange-600/30 hover:bg-orange-600/50 text-orange-400 text-xs font-bold px-2 py-1 my-1 rounded-md transition-colors border border-orange-600/30"
                                    title="Vincular plano"
                                >
                                    +
                                </button>
                            )}
                        </div>
                        </div>

                        <div className="flex items-center justify-center gap-4 mt-4">
                            {/* If deleted, show reativar */}
                            {clienteSelecionado?.status === -1 ? (
                                <div className="flex gap-2">
                                    <Botao texto="Reativar" color="sucess" click={async () => { if (!clienteSelecionado) return; await ativarCliente(clienteSelecionado.id); toast.success("Cliente reativado!"); fecharModal(); }} />
                                    <Botao texto="Fechar" color="cancel" click={fecharModal} />
                                </div>
                            ) : (
                                <>
                                    <div className="flex flex-col sm:flex-row sm:items-center sm:gap-4">
                                        <Botao texto="Salvar" color="sucess" click={handleAtualizar} />
                                        <Botao texto="Excluir" color="delete" click={() => setConfirmacaoAberta(true)} />
                                    </div>
                                    
                                </>
                            )}
                        </div>
                    </div>
                </DialogContent>
            </Dialog>

            <ModalConfirmacao
                open={confirmacaoAberta}
                titulo="Excluir Cliente"
                mensagem={`Deseja excluir o cliente "${clienteSelecionado?.nome}"? Esta ação não pode ser desfeita.`}
                onConfirmar={handleDeletar}
                onCancelar={() => setConfirmacaoAberta(false)}
            />
        </div>
    )
}
