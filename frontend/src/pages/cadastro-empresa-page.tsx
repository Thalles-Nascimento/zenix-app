import { useState, useEffect } from "react";
import { Input } from "../components/ui/input";
import { Toaster } from "sonner";
import { Button } from "@/components/ui/button";
import { useCadastro } from "@/hooks/use-cadastro";
import { useNavigate } from "react-router-dom";
import type { CadastroRequest } from "@/types/cadastro";

export default function CadastroEmpresaPage() {
  const [form, setForm] = useState({
    nomeEmpresa: "",
    cnpj: "",
    nomeUnidade: "",
    enderecoUnidade: "",
  });

  const { cadastrar, loading, error } = useCadastro();
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
    if (!form.nomeEmpresa || !form.cnpj || !form.nomeUnidade || !form.enderecoUnidade) {
      return "Preencha todos os campos";
    }
    return null;
  }

  async function onSubmit() {
    const v = validate();
    if (v) {
      alert(v);
      return;
    }

    try {
      const raw = localStorage.getItem("cadastro.partial");
      const user = raw ? JSON.parse(raw) : {};
      const payload: CadastroRequest = {
        nomeAdmin: user.nomeAdmin || "",
        email: user.email || "",
        senha: user.senha || "",
        cpf: user.cpf || "",
        nomeEmpresa: form.nomeEmpresa,
        cnpj: form.cnpj,
        nomeUnidade: form.nomeUnidade,
        enderecoUnidade: form.enderecoUnidade,
      };

      await cadastrar(payload);

      try {
        localStorage.removeItem('cadastro.partial');
      } catch (e) {
        // ignore
      }
    } catch (e) {
      // errors handled in hook
    }
  }

  return (
    <section className="min-h-screen flex items-center justify-center bg-gradient-to-b from-[#213458] via-[#0b1220] to-[#08101a] px-6 notranslate" style={{ paddingTop: "max(2rem, env(safe-area-inset-top))", paddingBottom: "max(2rem, env(safe-area-inset-bottom))" }}>
      <Toaster richColors position="top-center" />

      <div className="w-full max-w-4xl grid grid-cols-1 md:grid-cols-2 gap-8 items-center">
        <div className="hidden md:flex items-center justify-center">
          <img src="/assets/imagens/logo.png" alt="logo" className="w-56 -ml-8" />
        </div>

        <div className="bg-gradient-to-r from-white/5 to-white/3 backdrop-blur-md border border-white/10 rounded-2xl p-8 shadow-xl">
          <h1 className="text-2xl font-extrabold text-white mb-2">Cadastro — Informações da Empresa</h1>
          <p className="text-sm text-gray-300 mb-6">Agora informe os dados da sua empresa</p>

          <form className="space-y-4" onSubmit={(e) => e.preventDefault()}>
            <div>
              <label className="block mb-2 text-sm font-medium text-gray-300">Nome da Empresa</label>
              <Input type="text" placeholder="Empresa" className="text-white bg-transparent border border-white/10 rounded-md px-3 py-2" value={form.nomeEmpresa} onChange={(event) => setField('nomeEmpresa', event.target.value)} />
            </div>

            <div>
              <label className="block mb-2 text-sm font-medium text-gray-300">CNPJ</label>
              <Input type="text" placeholder="00.000.000/0000-00" className="text-white bg-transparent border border-white/10 rounded-md px-3 py-2" value={form.cnpj} onChange={(event) => setField('cnpj', event.target.value)} />
            </div>

            <div>
              <label className="block mb-2 text-sm font-medium text-gray-300">Nome da Unidade</label>
              <Input type="text" placeholder="Unidade" className="text-white bg-transparent border border-white/10 rounded-md px-3 py-2" value={form.nomeUnidade} onChange={(event) => setField('nomeUnidade', event.target.value)} />
            </div>

            <div>
              <label className="block mb-2 text-sm font-medium text-gray-300">Endereço da Unidade</label>
              <Input type="text" placeholder="Endereço" className="text-white bg-transparent border border-white/10 rounded-md px-3 py-2" value={form.enderecoUnidade} onChange={(event) => setField('enderecoUnidade', event.target.value)} />
            </div>

            {typeof error === 'string' && (
              <p className="text-red-500 text-sm">{error}</p>
            )}

            <div className="flex gap-4">
              <Button type="button" variant="ghost" onClick={() => {navigate('/cadastro'); }} className="text-white">Voltar</Button>
              <Button type="button" variant="default" onClick={() => onSubmit()} className="ml-auto text-white" disabled={loading}>{loading ? 'Cadastrando...' : 'Finalizar'}</Button>
            </div>
          </form>
        </div>
      </div>
    </section>
  );
}
