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
import { formatarTelefone } from "@/utils/formatter"
import { ModalConfirmacao } from "@/components/common/modal-confirmacao-component"

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
        c.nomeCliente.toLowerCase().includes(busca.toLowerCase()) ||
        c.telefone?.includes(busca)
    )

    const { itensPagina, paginaAtual, totalPaginas, totalItens, setPaginaAtual } = usePaginacao(clientesFiltrados, 5)

    const formatBRL = (valor: number) =>
        valor.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })

    const abrirNovo = () => {
        setFormNome("")
        setFormTelefone("")
        setModalTipo("novo")
    }

    const abrirEditar = (cliente: ClienteDTO) => {
        setClienteSelecionado(cliente)
        setFormNome(cliente.nomeCliente)
        setFormTelefone(cliente.telefone ?? "")
        setModalTipo("editar")
    }

    const abrirPlano = (cliente: ClienteDTO) => {
        setClienteSelecionado(cliente)
        setPlanoSelecionado(cliente.plano ? String(cliente.plano.id) : "")
        setModalTipo("plano")
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
            await vincularPlano(clienteSelecionado.id, Number(planoSelecionado))
            toast.success("Plano vinculado!")
            fecharModal()
        } catch {
            toast.error("Erro ao vincular plano.")
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
        if (!cliente.plano) return null
        const percentual = cliente.atendimentosMes / cliente.plano.limiteAtendimentos
        if (cliente.atendimentosMes >= cliente.plano.limiteAtendimentos) return "limite"
        if (percentual >= 0.8) return "aviso"
        return null
    }

    if (carregando) {
        return (
            <div className="w-full flex items-center justify-center py-20">
                <Badge variant="secondary"><Spinner />Carregando...</Badge>
            </div>
        )
    }

    return (
        <div className="flex flex-col gap-6 notranslate">
            <Toaster richColors position="top-center" />

            {/* Topbar */}
            <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
                <h1 className="text-white text-xl font-bold">Clientes</h1>
                <div className="flex flex-wrap items-center gap-2">
                    <input
                        type="text"
                        placeholder="Buscar por nome ou telefone..."
                        value={busca}
                        onChange={(e) => setBusca(e.target.value)}
                        className="bg-gray-900 border border-gray-700 text-white text-sm rounded-lg px-3 py-2 outline-none w-full sm:w-56"
                    />
                    <button
                        onClick={() => setFiltro("ativos")}
                        className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors
                            ${filtro === "ativos" ? "bg-orange-700 text-white" : "bg-gray-800 text-gray-400 hover:text-white"}`}
                    >
                        Ativos
                    </button>
                    <button
                        onClick={() => setFiltro("todos")}
                        className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors
                            ${filtro === "todos" ? "bg-gray-600 text-white" : "bg-gray-900 text-gray-400 hover:text-white"}`}
                    >
                        Todos
                    </button>
                    <button
                        onClick={abrirNovo}
                        className="bg-orange-600 hover:bg-orange-500 text-white text-sm font-bold px-4 py-2 rounded-lg transition-colors whitespace-nowrap"
                    >
                        + Novo Cliente
                    </button>
                </div>
            </div>

            {/* Alerta de clientes no limite */}
            {clientes.some(c => getAlerta(c) === "limite") && (
                <div className="bg-red-900/30 border border-red-700 rounded-xl px-4 py-3 flex items-center gap-2">
                    <span className="text-red-400 font-bold text-sm">⚠ Atenção:</span>
                    <span className="text-red-300 text-sm">
                        {clientes.filter(c => getAlerta(c) === "limite").length} cliente(s) atingiram o limite do plano este mês!
                    </span>
                </div>
            )}

            {/* Tabela */}
            <div className="bg-black rounded-xl border border-gray-700 notranslate">
                <div className="overflow-x-auto rounded-xl notranslate">
                    <table className="w-full text-sm text-left text-gray-300 notranslate">
                        <thead className="text-xs text-white uppercase bg-gray-800 border-b border-gray-700 notranslate">
                            <tr>
                                <th className="px-4 py-3 notranslate">CLIENTE</th>
                                <th className="px-4 py-3 notranslate">TELEFONE</th>
                                <th className="px-4 py-3 notranslate">PLANO</th>
                                <th className="px-4 py-3 notranslate">USO DO MÊS</th>
                                <th className="px-4 py-3 notranslate">TOTAL DE VISITAS</th>
                                <th className="px-4 py-3 notranslate">STATUS</th>
                                <th className="px-4 py-3 notranslate">AÇÕES</th>
                            </tr>
                        </thead>
                        <tbody>
                            {itensPagina.length === 0 ? (
                                <tr>
                                    <td colSpan={7} className="px-6 py-8 text-center text-gray-500 notranslate">
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
                                            className={`border-b border-gray-700 notranslate ${inativo ? "opacity-50 bg-black" : "bg-black"}`}
                                        >
                                            <td className="px-4 py-4 notranslate font-medium text-white">
                                                {cliente.nomeCliente}
                                            </td>
                                            <td className="px-4 py-4 notranslate text-gray-400">
                                                {formatarTelefone(cliente.telefone ?? "")}
                                            </td>
                                            <td className="px-4 py-4 notranslate">
                                                {cliente.plano ? (
                                                    <span className="bg-orange-500/20 text-orange-400 text-xs font-semibold px-2 py-1 rounded-full border border-orange-500/30">
                                                        {cliente.plano.planoDescricao}
                                                    </span>
                                                ) : (
                                                    <span className="text-gray-500 text-xs">Avulso</span>
                                                )}
                                            </td>
                                            <td className="px-4 py-4 notranslate">
                                                {cliente.plano ? (
                                                    <div className="flex items-center gap-2">
                                                        <div className="w-20 bg-gray-700 rounded-full h-1.5">
                                                            <div
                                                                className={`h-1.5 rounded-full transition-all ${
                                                                    alerta === "limite" ? "bg-red-500" :
                                                                    alerta === "aviso" ? "bg-yellow-500" :
                                                                    "bg-green-500"
                                                                }`}
                                                                style={{ width: `${Math.min((cliente.atendimentosMes / cliente.plano.limiteAtendimentos) * 100, 100)}%` }}
                                                            />
                                                        </div>
                                                        <span className={`text-xs font-medium ${
                                                            alerta === "limite" ? "text-red-400" :
                                                            alerta === "aviso" ? "text-yellow-400" :
                                                            "text-gray-400"
                                                        }`}>
                                                            {cliente.atendimentosMes}/{cliente.plano.limiteAtendimentos}
                                                            {alerta === "limite" && " ⚠"}
                                                        </span>
                                                    </div>
                                                ) : (
                                                    <span className="text-gray-500 text-xs">—</span>
                                                )}
                                            </td>
                                            <td className="px-4 py-4 notranslate text-gray-300">
                                                {cliente.vezesRetorno}
                                            </td>
                                            <td className="px-4 py-4 notranslate">
                                                <span className={inativo ? "text-gray-500 text-xs" : "text-orange-500 text-xs font-semibold"}>
                                                    {inativo ? "EXCLUÍDO" : "ATIVO"}
                                                </span>
                                            </td>
                                            <td className="px-4 py-4 notranslate">
                                                {!inativo ? (
                                                    <div className="flex gap-2">
                                                        <button onClick={() => abrirEditar(cliente)}
                                                            className="bg-gray-700 hover:bg-gray-600 text-white text-xs font-bold px-3 py-1.5 rounded-lg transition-colors">
                                                            Editar
                                                        </button>
                                                        <button onClick={() => abrirPlano(cliente)}
                                                            className="bg-orange-600/30 hover:bg-orange-600/50 text-orange-400 text-xs font-bold px-3 py-1.5 rounded-lg transition-colors border border-orange-600/30">
                                                            Plano
                                                        </button>
                                                    </div>
                                                ) : (
                                                    <button
                                                        onClick={() => ativarCliente(cliente.id).then(() => toast.success("Cliente reativado!"))}
                                                        className="bg-green-900/40 hover:bg-green-900/60 text-green-400 text-xs font-bold px-3 py-1.5 rounded-lg transition-colors border border-green-700/40"
                                                    >
                                                        Reativar
                                                    </button>
                                                )}
                                            </td>
                                        </tr>
                                    )
                                })
                            )}
                        </tbody>
                    </table>
                </div>
                <Paginacao
                    paginaAtual={paginaAtual}
                    totalPaginas={totalPaginas}
                    totalItens={totalItens}
                    itensPorPagina={5}
                    onPaginaChange={setPaginaAtual}
                />
            </div>

            {/* Modal Novo Cliente */}
            <Dialog open={modalTipo === "novo"} onOpenChange={fecharModal}>
                <DialogContent className="bg-black border-gray-700 text-white w-[calc(100vw-2rem)] max-w-md notranslate">
                    <DialogHeader>
                        <DialogTitle className="text-white notranslate">Novo Cliente</DialogTitle>
                    </DialogHeader>
                    <div className="flex flex-col gap-4 mt-2 notranslate">
                        <div>
                            <Label className="text-gray-300">Nome</Label>
                            <Input
                                className="mt-1 bg-gray-900 border-gray-700 text-white"
                                placeholder="Nome completo"
                                value={formNome}
                                onChange={(e) => setFormNome(e.target.value)}
                            />
                        </div>
                        <div>
                            <Label className="text-gray-300">Telefone</Label>
                            <InputTelefone value={formTelefone} onChange={setFormTelefone} />
                        </div>
                        <div className="flex flex-col gap-2 mt-2">
                            <Botao texto="Criar Cliente" color="sucess" click={handleCriar} />
                            <Botao texto="Cancelar" color="cancel" click={fecharModal} />
                        </div>
                    </div>
                </DialogContent>
            </Dialog>

            {/* Modal Editar Cliente */}
            <Dialog open={modalTipo === "editar"} onOpenChange={fecharModal}>
                <DialogContent className="bg-black border-gray-700 text-white w-[calc(100vw-2rem)] max-w-md notranslate">
                    <DialogHeader>
                        <DialogTitle className="text-white notranslate">Editar Cliente</DialogTitle>
                    </DialogHeader>
                    <div className="flex flex-col gap-4 mt-2 notranslate">
                        <div>
                            <Label className="text-gray-300">Nome</Label>
                            <Input
                                className="mt-1 bg-gray-900 border-gray-700 text-white"
                                value={formNome}
                                onChange={(e) => setFormNome(e.target.value)}
                            />
                        </div>
                        <div>
                            <Label className="text-gray-300">Telefone</Label>
                            <InputTelefone value={formTelefone} onChange={setFormTelefone} />
                        </div>
                        <div className="flex flex-col gap-2 mt-2">
                            <Botao texto="Salvar Alterações" color="sucess" click={handleAtualizar} />
                            <Botao texto="Excluir Cliente" color="delete" click={() => setConfirmacaoAberta(true)} />
                            <Botao texto="Cancelar" color="cancel" click={fecharModal} />
                        </div>
                    </div>
                </DialogContent>
            </Dialog>

            {/* Modal Gerenciar Plano */}
            <Dialog open={modalTipo === "plano"} onOpenChange={fecharModal}>
                <DialogContent className="bg-black border-gray-700 text-white w-[calc(100vw-2rem)] max-w-md notranslate">
                    <DialogHeader>
                        <DialogTitle className="text-white notranslate">
                            Plano — <span className="text-orange-500">{clienteSelecionado?.nomeCliente}</span>
                        </DialogTitle>
                    </DialogHeader>

                    {clienteSelecionado?.plano && (
                        <div className="bg-gray-900 rounded-lg p-3 border border-gray-700 notranslate">
                            <div className="flex justify-between mb-2">
                                <p className="text-gray-400 text-xs uppercase tracking-widest">Uso do Mês</p>
                                <p className={`text-xs font-bold ${
                                    getAlerta(clienteSelecionado) === "limite" ? "text-red-400" :
                                    getAlerta(clienteSelecionado) === "aviso" ? "text-yellow-400" :
                                    "text-green-400"
                                }`}>
                                    {clienteSelecionado.atendimentosMes}/{clienteSelecionado.plano.limiteAtendimentos}
                                    {getAlerta(clienteSelecionado) === "limite" && " — LIMITE ⚠"}
                                </p>
                            </div>
                            <div className="w-full bg-gray-700 rounded-full h-2">
                                <div
                                    className={`h-2 rounded-full ${
                                        getAlerta(clienteSelecionado) === "limite" ? "bg-red-500" :
                                        getAlerta(clienteSelecionado) === "aviso" ? "bg-yellow-500" :
                                        "bg-green-500"
                                    }`}
                                    style={{ width: `${Math.min((clienteSelecionado.atendimentosMes / clienteSelecionado.plano.limiteAtendimentos) * 100, 100)}%` }}
                                />
                            </div>
                            <p className="text-gray-400 text-xs mt-2">
                                Plano atual: <span className="text-orange-400 font-semibold">{clienteSelecionado.plano.planoDescricao}</span> — {formatBRL(clienteSelecionado.plano.valor)}/mês
                            </p>
                        </div>
                    )}

                    <div className="notranslate">
                        <Label className="text-gray-300">
                            {clienteSelecionado?.plano ? "Trocar Plano" : "Vincular Plano"}
                        </Label>
                        <Select value={planoSelecionado} onValueChange={setPlanoSelecionado}>
                            <SelectTrigger className="mt-1 bg-gray-900 border-gray-700 text-white w-full notranslate">
                                <SelectValue placeholder="Selecione um plano" />
                            </SelectTrigger>
                            <SelectContent className="bg-black border-gray-700 text-white notranslate">
                                {planos.map(p => (
                                    <SelectItem key={p.id} value={String(p.id)} className="notranslate">
                                        {p.planoDescricao} — {formatBRL(p.valor)} ({p.limiteAtendimentos} atend./mês)
                                    </SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                    </div>

                    <div className="flex flex-col gap-2 mt-2">
                        <Botao texto="Salvar Plano" color="sucess" click={handleVincular} />
                        {clienteSelecionado?.plano && (
                            <Botao texto="Remover Plano" color="delete" click={handleDesvincular} />
                        )}
                        <Botao texto="Cancelar" color="cancel" click={fecharModal} />
                    </div>
                </DialogContent>
            </Dialog>

            <ModalConfirmacao
                open={confirmacaoAberta}
                titulo="Excluir Cliente"
                mensagem={`Deseja excluir o cliente "${clienteSelecionado?.nomeCliente}"? Esta ação não pode ser desfeita.`}
                onConfirmar={handleDeletar}
                onCancelar={() => setConfirmacaoAberta(false)}
            />
        </div>
    )
}