import { useState } from "react";
import { Input } from "../components/ui/input";
import { useLogin } from '../hooks/use-login'
import { Toaster } from "sonner";
import { Button } from "@/components/ui/button";

export default function CadastroPage() {

    const [email, setEmail] = useState<string>("");
    const [senha, setSenha] = useState<string>("");
    const { logar, erro } = useLogin()

    return (
        <section
            className="bg-black flex flex-col items-center justify-center px-6 notranslate"
            style={{
                minHeight: "100dvh",
                paddingTop: "max(2rem, env(safe-area-inset-top))",
                paddingBottom: "max(2rem, env(safe-area-inset-bottom))",
            }}
        >
            <Toaster richColors position="top-center" />
            <div className=" items-center mb-9 text-4xl font-semibold notranslate">
                <img className="w-88" src="/assets/imagens/logo.png" alt="logo" />
            </div>
            <div className="w-full bg-gray-900 rounded-xl shadow border md:mt-0 sm:max-w-md xl:p-0 border-gray-500 notranslate">
                <div className="p-6 space-y-4 md:space-y-6 sm:p-8 notranslate">
                    <h1 className="text-xl font-bold leading-tight tracking-tight text-white md:text-2xl">
                        Faça login na sua conta.
                    </h1>
                    <form className="space-y-4 md:space-y-6" onSubmit={(event) => event.preventDefault()}>
                        <div>
                            <label className="block mb-2 text-sm font-medium text-white">Seu e-mail</label>
                            <Input type="email" placeholder="E-mail" className="text-white bg-gray-900" onChange={(event) => setEmail(event.target.value)} />
                        </div>
                        <div>
                            <label className="block mb-2 text-sm font-medium text-white">Senha</label>
                            <Input type="password" placeholder="********" className="text-white bg-gray-900" onChange={(event) => setSenha(event.target.value)} />
                        </div>

                        {erro && (
                            <p className="text-red-500 text-sm">{erro}</p>
                        )}

                        <Button
                            variant="default"
                            className="w-full text-white font-bold rounded-xl notranslate"
                            onClick={() => logar(email, senha)}
                        >
                            Login
                        </Button>
                    </form>
                </div>
            </div>
        </section>
    )
}
