import { useState, useEffect } from "react";
import { Input } from "../components/ui/input";
import { Toaster } from "sonner";
import { Button } from "@/components/ui/button";
import { useNavigate } from "react-router-dom";
import { formatarCPF } from "@/utils/formatter";

export default function CadastroUserPage() {
  const [form, setForm] = useState({
    nomeAdmin: "",
    email: "",
    senha: "",
    cpf: "",
  });

  const navigate = useNavigate();

  useEffect(() => {
    try {
      const raw = localStorage.getItem("cadastro.partial");
      if (raw) {
        const parsed = JSON.parse(raw);
        setForm((prev) => ({ ...prev, ...(parsed || {}) }));
      }
    } catch (e) {
      // ignore
    }
  }, []);

  function setField(field: string, value: string) {
    setForm((prev) => ({ ...prev, [field]: value }));
  }

  function validate() {
    if (!form.nomeAdmin || !form.email || !form.senha || !form.cpf) {
      return "Preencha todos os campos";
    }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) return "Email inválido";
    if (form.senha.length < 6) return "Senha deve ter no mínimo 6 caracteres";
    return null;
  }

  function onNext() {
    const v = validate();
    if (v) {
      alert(v);
      return;
    }

    const partial = { ...form };
    try {
      localStorage.setItem("cadastro.partial", JSON.stringify(partial));
    } catch (e) {
      // ignore
    }
    navigate("/cadastro/empresa");
  }

  return (
    <section className="min-h-screen flex items-center justify-center bg-gradient-to-b from-[#213458] via-[#0b1220] to-[#08101a] px-6 notranslate" style={{ paddingTop: "max(2rem, env(safe-area-inset-top))", paddingBottom: "max(2rem, env(safe-area-inset-bottom))" }}>
      <Toaster richColors position="top-center" />

      <div className="w-full max-w-4xl grid grid-cols-1 md:grid-cols-2 gap-8 items-center">
        <div className="flex items-center justify-center">
          <img src="/assets/imagens/logo.png" alt="logo" className="w-60 md:w-56 md:-ml-8" />
        </div>

        <div className="bg-gradient-to-r from-white/5 to-white/3 backdrop-blur-md border border-white/10 rounded-2xl p-8 shadow-xl">
          <h1 className="text-2xl font-extrabold text-orange-600">Cadastro</h1>
          <h1 className="text-xl font-extrabold text-white mb-5">Informações do Usuário</h1>

          <form className="space-y-4" onSubmit={(e) => e.preventDefault()}>
            <div>
              <label className="block mb-2 text-sm font-medium text-gray-300">Nome do Administrador</label>
              <Input type="text" placeholder="Nome completo" className="text-white bg-transparent border border-white/10 rounded-md px-3 py-2" value={form.nomeAdmin} onChange={(event) => setField('nomeAdmin', event.target.value)} />
            </div>

            <div>
              <label className="block mb-2 text-sm font-medium text-gray-300">E-mail</label>
              <Input type="email" placeholder="exemplo@exemplo.com" className="text-white bg-transparent border border-white/10 rounded-md px-3 py-2" value={form.email} onChange={(event) => setField('email', event.target.value)} />
            </div>

            <div>
              <label className="block mb-2 text-sm font-medium text-gray-300">Senha</label>
              <Input type="password" placeholder="********" className="text-white bg-transparent border border-white/10 rounded-md px-3 py-2" value={form.senha} onChange={(event) => setField('senha', event.target.value)} />
            </div>

            <div>
              <label className="block mb-2 text-sm font-medium text-gray-300">CPF</label>
              <Input type="text" placeholder="000.000.000-00" className="text-white bg-transparent border border-white/10 rounded-md px-3 py-2" value={form.cpf} onChange={(event) => setField('cpf', formatarCPF(event.target.value))} />
            </div>

            <div className="flex gap-4">
              <Button type="button" variant="ghost" onClick={() => { try { localStorage.removeItem('cadastro.partial'); } catch (e) {} navigate('/login'); }} className="text-white">Cancelar</Button>
              <Button type="button" variant="default" onClick={() => onNext()} className="ml-auto text-white">Próximo</Button>
            </div>
          </form>
        </div>
      </div>
    </section>
  );
}
