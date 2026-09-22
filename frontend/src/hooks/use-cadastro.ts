import { useState } from "react";
import type { CadastroRequest } from "@/types/cadastro";
import { cadastrarCadastro } from "@/services/cadastroService";
import { useNavigate } from "react-router-dom";
import { toast } from "sonner";

export function useCadastro() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<Record<string, string> | string | null>(null);
  const navigate = useNavigate();

  async function cadastrar(payload: CadastroRequest) {
    setLoading(true);
    setError(null);
    try {
      const res = await cadastrarCadastro(payload);
      toast.success(res.mensagem || "Cadastro realizado com sucesso!");
      localStorage.removeItem("cadastro.partial");
      // redirect to login
      navigate('/login')
      return res;
    } catch (e: any) {
      const message = e?.message ?? "Erro ao cadastrar";
      // if backend returns object with validation errors, capture them
      if (e?.status === 400 && e?.data) {
        // assume structured errors
        setError(e.data);
      } else {
        setError(message);
      }
      toast.error(message);
      throw e;
    } finally {
      setLoading(false);
    }
  }

  return { cadastrar, loading, error };
}
