import { useState } from "react";
import { Input } from "../components/ui/input";
import { useLogin } from '../hooks/use-login'
import { Toaster } from "sonner";
import { Button } from "@/components/ui/button";
import { useNavigate } from "react-router-dom";

export default function LoginPage() {

    const [email, setEmail] = useState<string>("");
    const [senha, setSenha] = useState<string>("");
    const navigate = useNavigate();
    const { logar, erro } = useLogin()

    return (
        <section
            className="min-h-screen flex items-center justify-center bg-gradient-to-b from-[#213458] via-[#0b1220] to-[#08101a] px-6 notranslate"
            style={{ paddingTop: "max(2rem, env(safe-area-inset-top))", paddingBottom: "max(2rem, env(safe-area-inset-bottom))" }}
        >
            <Toaster richColors position="top-center" />

            <div className="w-full max-w-4xl grid grid-cols-1 md:grid-cols-2 gap-8 items-center">
                <div className="hidden md:flex items-center justify-center">
                    <img src="/assets/imagens/logo.png" alt="logo" className="w-56 -ml-8" />
                </div>

                <div className="bg-gradient-to-r from-white/5 to-white/3 backdrop-blur-md border border-white/10 rounded-2xl p-8 shadow-xl">
                    <h1 className="text-2xl font-extrabold text-orange-600 mb-2">Seja Bem-vindo</h1>
                    <p className="text-white mb-6">Entre na sua conta</p>

                    <form className="space-y-4" onSubmit={(event) => event.preventDefault()}>
                        <div>
                            <label className="block mb-2 text-sm font-medium text-gray-200">E-mail</label>
                            <Input type="email" placeholder="exemplo@exemplo.com" className="text-white bg-transparent border border-white/10 rounded-md px-3 py-2" onChange={(event) => setEmail(event.target.value)} />
                        </div>
                        <div>
                            <label className="block mb-2 text-sm font-medium text-gray-200">Senha</label>
                            <Input type="password" placeholder="********" className="text-white bg-transparent border border-white/10 rounded-md px-3 py-2" onChange={(event) => setSenha(event.target.value)} />
                        </div>

                        {erro && (
                            <p className="text-red-400 text-sm">{erro}</p>
                        )}

                        <Button
                            type="button"
                            variant="default"
                            className="w-full text-white font-bold rounded-xl mt-2"
                            onClick={() => logar(email, senha)}
                        >
                            Login
                        </Button>

                        <Button type="button" variant="secondary" onClick={() => {navigate('/cadastro'); }} className="text-black w-full rounded-xl">Cria Conta</Button>
                    </form>
                </div>
            </div>
        </section>
    )
}
