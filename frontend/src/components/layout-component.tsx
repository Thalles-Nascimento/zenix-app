import { useEffect, useState } from "react"
import { Link, useLocation, useNavigate } from "react-router-dom"
import { Menu, Scissors, Users, DollarSign, LogOutIcon, ChartBarStacked, UserPlus, Building2, Wrench, WalletCards, Settings, ChevronDown, UsersRound, Crown, X, PanelLeftClose, PanelLeftOpen } from "lucide-react"
import { useAuth } from "../contexts/auth-context"
import { Sheet, SheetContent, SheetTrigger } from "./ui/sheet"
import { usePerfil } from "../hooks/use-perfil"
import { KeyRound } from "lucide-react"
import { toast } from "sonner"

interface LayoutProps {
    children: React.ReactNode
}

function SidebarContent({ onClose, collapsed }: { onClose?: () => void, collapsed?: boolean }) {
    const { permissao } = useAuth()
    const location = useLocation()

    const configPaths = ["/servicos", "/pagamentos", "/unidades"]
    const clientesPaths = ["/clientes", "/planos"]

    const [configAberto, setConfigAberto] = useState(configPaths.includes(location.pathname))
    const [clientesAberto, setClientesAberto] = useState(clientesPaths.includes(location.pathname))

    const menuItems = [
        { label: "Atendimentos", path: "/atendimentos", icon: <Scissors size={18} />, roles: ["ADMIN", "USER"] },
        { label: "Financeiro",   path: "/financeiro",   icon: <DollarSign size={18} />, roles: ["ADMIN", "USER"] },
        { label: "Usuários",     path: "/usuarios",     icon: <UserPlus size={18} />, roles: ["ADMIN"] },
        { label: "Relatório",    path: "/dashboard",    icon: <ChartBarStacked size={18} />, roles: ["ADMIN"] },
        { label: "Fila",         path: "/fila",         icon: <Users size={18} />, roles: ["ADMIN", "USER"] },
    ]

    const configItems = [
        { label: "Serviços",           path: "/servicos",   icon: <Wrench size={16} /> },
        { label: "Forma de Pagamento", path: "/pagamentos", icon: <WalletCards size={16} /> },
        { label: "Unidades",           path: "/unidades",   icon: <Building2 size={16} /> },
    ]

    const clientesItems = [
        { label: "Clientes", path: "/clientes", icon: <UsersRound size={16} /> },
        { label: "Planos",   path: "/planos",   icon: <Crown size={16} /> },
    ]


    const isConfigAtivo = configPaths.includes(location.pathname)
    const isClientesAtivo = clientesPaths.includes(location.pathname)

    return (
        <div className={`flex flex-col h-full text-white bg-black ${collapsed ? 'items-center' : ''}`}>
            <div className="flex items-center justify-center gap-3 px-4 py-4">
                <img className={`${collapsed ? '30' : 'w-50'} object-contain`} src="/assets/imagens/logo.png" alt="logo" />
            </div>
            <nav className={`flex flex-col gap-1 p-2 flex-1 ${collapsed ? 'items-center' : 'pl-4'}`}>
                {menuItems
                    .filter(item => item.roles.includes(permissao ?? ""))
                    .map(item => (
                        <Link
                            key={item.path}
                            to={item.path}
                            onClick={onClose}
                            className={`flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium transition-colors w-full
                                ${location.pathname === item.path
                                    ? "bg-orange-500 text-white"
                                    : "text-white hover:bg-gray-900 hover:text-gray-400"
                                }`}
                        >
                            <div className="flex items-center justify-center w-6">
                                {item.icon}
                            </div>
                            {!collapsed && <span className="ml-1">{item.label}</span>}
                        </Link>
                    ))}

                {/* Dropdown Clientes — apenas ADMIN */}
                {permissao === "ADMIN" && (
                    <div className={`${collapsed ? 'w-full' : ''}`}>
                        <button
                            onClick={() => setClientesAberto(prev => !prev)}
                            className={`w-full flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium transition-colors
                                ${isClientesAtivo
                                    ? "bg-orange-500 text-white"
                                    : "text-white hover:bg-gray-900 hover:text-gray-400"
                                }`}
                        >
                            <Crown size={18} />
                            {!collapsed && <span className="flex-1 text-left">Clientes e Planos</span>}
                            {!collapsed && (
                                <ChevronDown
                                    size={16}
                                    className={`transition-transform ${clientesAberto ? "rotate-180" : ""}`}
                                />
                            )}
                        </button>

                        {clientesAberto && !collapsed && (
                            <div className="ml-4 mt-1 flex flex-col gap-1 border-l border-gray-700 pl-3">
                                {clientesItems.map(item => (
                                    <Link
                                        key={item.path}
                                        to={item.path}
                                        onClick={onClose}
                                        className={`flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors
                                            ${location.pathname === item.path
                                                ? "bg-orange-500 text-white"
                                                : "text-white hover:bg-gray-900 hover:text-gray-400"
                                            }`}
                                    >
                                        {item.icon}
                                        {item.label}
                                    </Link>
                                ))}
                            </div>
                        )}
                    </div>
                )}

                {/* Dropdown Configurações — apenas ADMIN */}
                {permissao === "ADMIN" && (
                    <div className={`${collapsed ? 'w-full' : ''}`}>
                        <button
                            onClick={() => setConfigAberto(prev => !prev)}
                            className={`w-full flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium transition-colors
                                ${isConfigAtivo
                                    ? "bg-orange-500 text-white"
                                    : "text-white hover:bg-gray-900 hover:text-gray-400"
                                }`}
                        >
                            <Settings size={18} />
                            {!collapsed && <span className="flex-1 text-left">Configurações</span>}
                            {!collapsed && (
                                <ChevronDown
                                    size={16}
                                    className={`transition-transform ${configAberto ? "rotate-180" : ""}`}
                                />
                            )}
                        </button>

                        {configAberto && !collapsed && (
                            <div className="ml-4 mt-1 flex flex-col gap-1 border-l border-gray-700 pl-3">
                                {configItems.map(item => (
                                    <Link
                                        key={item.path}
                                        to={item.path}
                                        onClick={onClose}
                                        className={`flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors
                                            ${location.pathname === item.path
                                                ? "bg-orange-500 text-white"
                                                : "text-white hover:bg-gray-900 hover:text-gray-400"
                                            }`}
                                    >
                                        {item.icon}
                                        {item.label}
                                    </Link>
                                ))}
                            </div>
                        )}
                    </div>
                )}
            </nav>
            
            {!collapsed && (
                <div className="px-6 py-4 border-t border-gray-700 text-xs font-bold text-white">
                    Zenix © {new Date().getFullYear()}
                </div>
            )}
        </div>
    )
}

export default function Layout({ children }: LayoutProps) {
    const { userName, permissao, logout } = useAuth()
    const [mobileOpen, setMobileOpen] = useState(false)
    const [grupo, setGrupo] = useState(permissao)
    const { trocarSenha } = usePerfil()
    const [dropdownOpen, setDropdownOpen] = useState(false)
    const [modalSenhaOpen, setModalSenhaOpen] = useState(false)
    const [novaSenha, setNovaSenha] = useState("")
    const [confirmarSenha, setConfirmarSenha] = useState("")
    const [sidebarCollapsed, setSidebarCollapsed] = useState(false)
    const navigate = useNavigate()

    const handleTrocarSenha = async () => {
        if (novaSenha !== confirmarSenha) {
            toast.error("As senhas não coincidem.")
            return
        }
        await trocarSenha(novaSenha)
        setModalSenhaOpen(false)
        setNovaSenha("")
        setConfirmarSenha("")
    }

    const clickButtonLogout = () => {
        logout()
        navigate("/login")
    }

    useEffect(() => {
        setGrupo(permissao === "USER" ? "Barbeiro" : permissao)
    }, [permissao])

    return (
        <div className="flex overflow-x-hidden bg-black text-white" style={{ minHeight: "100dvh" }}>
            <aside className={`${sidebarCollapsed ? 'hidden md:flex md:w-20' : 'hidden md:flex w-64'} flex-col fixed inset-y-0 left-0 border-r border-gray-700 z-30 transition-all duration-200`}>
                <SidebarContent collapsed={sidebarCollapsed} />
            </aside>

            <div className={`flex flex-col flex-1 min-w-0 ${sidebarCollapsed ? 'md:ml-20' : 'md:ml-64'}`}>
                <header
                    className="sticky top-0 z-40 flex items-center justify-between px-4 bg-black border-b border-gray-700"
                    style={{
                        paddingTop: "max(0.75rem, env(safe-area-inset-top))",
                        paddingBottom: "0.75rem"
                    }}
                >
                    <div className="flex items-center gap-2">
                        {/* Mobile menu button */}
                        <Sheet open={mobileOpen} onOpenChange={setMobileOpen}>
                            <SheetTrigger className="md:hidden text-gray-400 hover:text-white">
                                <Menu size={24} />
                            </SheetTrigger>
                            <SheetContent side="left" className="p-0 w-64 bg-gray-900 border-gray-700">
                                <SidebarContent onClose={() => setMobileOpen(false)} />
                            </SheetContent>
                        </Sheet>

                        {/* Collapse toggle (desktop) */}
                        <button
                            onClick={() => setSidebarCollapsed(prev => !prev)}
                            className="hidden md:inline-flex items-center justify-center w-10 h-10 rounded-md bg-black hover:bg-gray-800 text-gray-200"
                            aria-label={sidebarCollapsed ? "Abrir menu" : "Fechar menu"}
                        >
                            {sidebarCollapsed ? <PanelLeftOpen size={18}/> : <PanelLeftClose size={18}/>}
                        </button>
                    </div>

                    <div className="ml-auto flex items-center justify-center gap-3 relative">
                        <span className="text-sm text-white hidden sm:block">{userName}</span>
                        <button
                            onClick={() => setDropdownOpen(!dropdownOpen)}
                            className="w-8 h-8 rounded-full bg-orange-600 flex items-center justify-center font-bold hover:bg-orange-500 transition-colors notranslate"
                        >
                            {userName?.charAt(0).toUpperCase()}
                        </button>

                        {dropdownOpen && (
                            <>
                                <div className="fixed inset-0 z-40" onClick={() => setDropdownOpen(false)} />
                                <div className="absolute right-0 top-10 z-50 bg-gray-800 border border-gray-700 rounded-xl shadow-lg w-48 py-2">
                                    <div className="px-4 py-2 border-b border-gray-700">
                                        <p className="text-white text-sm font-medium">{userName}</p>
                                        <p className="text-gray-400 text-xs">{grupo}</p>
                                    </div>
                                    <button
                                        onClick={() => { setDropdownOpen(false); setModalSenhaOpen(true) }}
                                        className="w-full flex items-center gap-2 px-4 py-2 text-sm text-gray-300 hover:bg-gray-700 hover:text-white transition-colors"
                                    >
                                        <KeyRound size={14} />
                                        Trocar senha
                                    </button>
                                        <button className="w-full flex items-center gap-2 px-4 py-2 text-sm text-gray-300 hover:bg-gray-700 hover:text-white transition-colors"
                                         onClick={clickButtonLogout}
                                         >
                                            <LogOutIcon size={14}/>
                                            Logout
                                        </button>
                                </div>
                            </>
                        )}
                    </div>
                </header>

                {modalSenhaOpen && (
                    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 px-4">
                        <div className="bg-black border border-gray-700 rounded-xl p-6 w-full max-w-sm">
                            <div className="flex items-center justify-between mb-4">
                                <h2 className="text-white font-bold">Trocar Senha</h2>
                                <button onClick={() => setModalSenhaOpen(false)}>
                                    <X size={18} className="text-gray-400 hover:text-white" />
                                </button>
                            </div>
                            <div className="flex flex-col gap-3">
                                <div>
                                    <label className="text-white text-sm">Nova senha</label>
                                    <input
                                        type="password"
                                        placeholder="Mínimo 6 caracteres"
                                        value={novaSenha}
                                        onChange={(e) => setNovaSenha(e.target.value)}
                                        className="mt-1 w-full bg-gray-900 border border-gray-700 text-white text-sm rounded-lg px-3 py-2 outline-none"
                                    />
                                </div>
                                <div>
                                    <label className="text-white text-sm">Confirmar senha</label>
                                    <input
                                        type="password"
                                        placeholder="Repita a senha"
                                        value={confirmarSenha}
                                        onChange={(e) => setConfirmarSenha(e.target.value)}
                                        className="mt-1 w-full bg-gray-900 border border-gray-700 text-white text-sm rounded-lg px-3 py-2 outline-none"
                                    />
                                </div>
                                <div className="flex gap-2 mt-2">
                                    <button
                                        onClick={handleTrocarSenha}
                                        className="flex-1 bg-orange-600 hover:bg-orange-500 text-white text-sm font-medium py-2 rounded-lg transition-colors"
                                    >
                                        Salvar
                                    </button>
                                    <button
                                        onClick={() => setModalSenhaOpen(false)}
                                        className="flex-1 bg-gray-700 hover:bg-gray-600 text-white text-sm font-medium py-2 rounded-lg transition-colors"
                                    >
                                        Cancelar
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                )}

                <main className="flex-1 py-4 px-4 min-w-0 notranslate">
                    {children}
                </main>

                <footer
                    className="px-6 border-t border-gray-500 text-center text-xs text-white"
                    style={{
                        paddingTop: "1rem",
                        paddingBottom: "max(1rem, env(safe-area-inset-bottom))"
                    }}
                >
                    <h2 className="font-bold">Zenix — Todos os direitos reservados ©</h2>
                </footer>
            </div>
        </div>
    )
}
