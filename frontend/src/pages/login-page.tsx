import { useState } from "react";
import { Button } from '../components/ui/button'
import { Input } from "../components/ui/input";
import { useLogin } from '../hooks/use-login'
import { Toaster } from "sonner";

export default function LoginPage(){

    const [email, setEmail] = useState<string>("");
    const [senha, setSenha] = useState<string>("");
    const { logar, erro } = useLogin()


    return (
        <section className="min-h-screen bg-black">
            <Toaster richColors position="top-center" />
            <div className="flex flex-col items-center justify-center px-6 py-8 mx-auto md:h-screen lg:py-0">
                <div className="flex items-center mb-1 text-4xl font-semibold">
                    <img className="w-48 h-48 mr-2" src="/assets/imagens/LogoWN.png" alt="logo"/>
                </div>
                <div className="w-full bg-zinc-900 rounded-xl shadow border md:mt-0 sm:max-w-md xl:p-0 border-gray-500">
                    <div className="p-6 space-y-4 md:space-y-6 sm:p-8">
                        <h1 className="text-xl font-bold leading-tight tracking-tight text-white md:text-2xl">
                            Faça login na sua conta.
                        </h1>
                        <form className="space-y-4 md:space-y-6" onSubmit={(event) => event.preventDefault()}>
                            <div>
                                <label className="block mb-2 text-sm font-medium text-gray-300">Seu e-mail</label>
                                <Input type="email" placeholder="E-mail" className="text-white" onChange={(event) => setEmail(event.target.value)}/>
                            </div>
                            <div>
                                <label className="block mb-2 text-sm font-medium text-gray-300">Senha</label>
                                <Input type="password" placeholder="********" className="text-white" onChange={(event) => setSenha(event.target.value)}/>
                            </div>

                            {erro && (
                                <p className="text-red-500 text-sm">{erro}</p>
                            )}

                            <Button
                                variant="default"
                                className="w-full"
                                onClick={() => logar(email, senha)}
                            >
                                Login
                            </Button>
                        </form>
                    </div>
                </div>
            </div>
            </section>
    )
}