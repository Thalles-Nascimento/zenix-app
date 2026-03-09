import { useEffect, useState } from "react"
import { Link, useLocation, useNavigate } from "react-router-dom"
import { Menu, Scissors, Users, DollarSign, LogOutIcon, ChartBarStacked, UserPlus, Building2 } from "lucide-react"
import { useAuth } from "../contexts/auth-context"
import { Sheet, SheetContent, SheetTrigger } from "./ui/sheet"
import { Button } from "./ui/button"
import { usePerfil } from "../hooks/use-perfil"
import { KeyRound, X } from "lucide-react"
import { toast } from "sonner"

interface LayoutProps {
    children: React.ReactNode
}

function SidebarContent({ onClose }: { onClose?: () => void }) {
    const { logout, permissao } = useAuth()
    const navigate = useNavigate()

    const menuItems = [
        { label: "Atendimentos", path: "/atendimentos", icon: <Scissors size={18} />, roles: ["ADMIN", "USER"] },
        { label: "Financeiro",   path: "/financeiro",   icon: <DollarSign size={18} />, roles: ["ADMIN", "USER"] },
        { label: "Usuários",     path: "/usuarios",     icon: <UserPlus size={18} />, roles: ["ADMIN"] },
        { label: "Relatório",    path: "/dashboard",    icon: <ChartBarStacked size={18} />, roles: ["ADMIN"] },
        { label: "Unidades",     path: "/unidades",     icon: <Building2 size={18} />, roles: ["ADMIN"] },
        { label: "Fila",         path: "/fila",         icon: <Users size={18} />, roles: ["ADMIN", "USER"] },
    ]

    const clickButtonLogout = () => {
        logout()
        navigate("/login")
    }

    const location = useLocation()

    return (
        <div className="flex flex-col h-full bg-black text-white">
            <div className="flex items-center justify-center gap-3 px-6">
                <img className="w-32 h-32" src="/assets/imagens/LogoWN.png" alt="logo" />
            </div>
            <nav className="flex flex-col gap-1 p-4 flex-1 bg-black">
                {menuItems
                    .filter(item => item.roles.includes(permissao ?? ""))
                    .map(item => (
                        <Link
                            key={item.path}
                            to={item.path}
                            onClick={onClose}
                            className={`flex items-center gap-3 px-4 py-3 rounded-lg text-sm font-medium transition-colors
                                ${location.pathname === item.path
                                    ? "bg-orange-600 text-white"
                                    : "text-gray-400 hover:bg-gray-800 hover:text-white"
                                }`}
                        >
                            {item.icon}
                            {item.label}
                        </Link>
                    ))}
            </nav>
            <div className="p-4">
                <Button variant="ghost" size="sm" onClick={clickButtonLogout}>
                    <LogOutIcon className="mr-2" /> Logout
                </Button>
            </div>
            <div className="px-6 py-4 border-t border-gray-800 text-xs font-bold text-gray-500">
                Zenix © {new Date().getFullYear()}
            </div>
        </div>
    )
}

export default function Layout({ children }: LayoutProps) {
    const { userName, permissao } = useAuth()
    const [mobileOpen, setMobileOpen] = useState(false)
    const [grupo, setGrupo] = useState(permissao)
    const { trocarSenha } = usePerfil()
    const [dropdownOpen, setDropdownOpen] = useState(false)
    const [modalSenhaOpen, setModalSenhaOpen] = useState(false)
    const [novaSenha, setNovaSenha] = useState("")
    const [confirmarSenha, setConfirmarSenha] = useState("")

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

    useEffect(() => {
        setGrupo(permissao === "USER" ? "Barbeiro" : permissao)
    }, [permissao])

    return (
        <div className="flex overflow-x-hidden bg-gray-950 text-white"
            style={{ minHeight: "100dvh" }}
        >
            {/* Sidebar desktop */}
            <aside className="hidden md:flex w-64 flex-col fixed inset-y-0 left-0 border-r border-gray-700 z-30">
                <SidebarContent />
            </aside>

            {/* Conteúdo principal */}
            <div className="flex flex-col flex-1 min-w-0 md:ml-64">

                {/* Navbar — padding top respeita Dynamic Island */}
                <header
                    className="sticky top-0 z-40 flex items-center justify-between px-4 bg-black border-b border-gray-700"
                    style={{
                        paddingTop: "max(0.75rem, env(safe-area-inset-top))",
                        paddingBottom: "0.75rem"
                    }}
                >
                    <Sheet open={mobileOpen} onOpenChange={setMobileOpen}>
                        <SheetTrigger className="md:hidden text-gray-400 hover:text-white">
                            <Menu size={24} />
                        </SheetTrigger>
                        <SheetContent side="left" className="p-0 w-64 bg-gray-900 border-gray-700">
                            <SidebarContent onClose={() => setMobileOpen(false)} />
                        </SheetContent>
                    </Sheet>

                    <div className="ml-auto flex items-center gap-3 relative">
                        <span className="text-sm text-gray-300 hidden sm:block">{grupo}</span>

                        <button
                            onClick={() => setDropdownOpen(!dropdownOpen)}
                            className="w-8 h-8 rounded-full bg-orange-600 flex items-center justify-center font-bold text-sm hover:bg-orange-500 transition-colors"
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
                                </div>
                            </>
                        )}
                    </div>
                </header>

                {/* Modal trocar senha */}
                {modalSenhaOpen && (
                    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 px-4">
                        <div className="bg-gray-900 border border-gray-700 rounded-xl p-6 w-full max-w-sm">
                            <div className="flex items-center justify-between mb-4">
                                <h2 className="text-white font-bold">Trocar Senha</h2>
                                <button onClick={() => setModalSenhaOpen(false)}>
                                    <X size={18} className="text-gray-400 hover:text-white" />
                                </button>
                            </div>
                            <div className="flex flex-col gap-3">
                                <div>
                                    <label className="text-gray-300 text-sm">Nova senha</label>
                                    <input
                                        type="password"
                                        placeholder="Mínimo 6 caracteres"
                                        value={novaSenha}
                                        onChange={(e) => setNovaSenha(e.target.value)}
                                        className="mt-1 w-full bg-gray-800 border border-gray-700 text-white text-sm rounded-lg px-3 py-2 outline-none"
                                    />
                                </div>
                                <div>
                                    <label className="text-gray-300 text-sm">Confirmar senha</label>
                                    <input
                                        type="password"
                                        placeholder="Repita a senha"
                                        value={confirmarSenha}
                                        onChange={(e) => setConfirmarSenha(e.target.value)}
                                        className="mt-1 w-full bg-gray-800 border border-gray-700 text-white text-sm rounded-lg px-3 py-2 outline-none"
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

                {/* Página */}
                <main className="flex-1 py-4 px-4 min-w-0">
                    {children}
                </main>

                {/* Footer — padding bottom respeita barra de navegação do iPhone */}
                <footer
                    className="px-6 border-t border-gray-700 text-center text-xs text-gray-500"
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
