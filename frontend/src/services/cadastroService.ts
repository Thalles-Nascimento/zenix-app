import api_url from "@/enviroments/enviroments";

export type CadastroRequest = {
  nomeAdmin: string;
  email: string;
  senha: string;
  cpf: string;
  nomeEmpresa: string;
  cnpj: string;
  nomeUnidade: string;
  enderecoUnidade: string;
};

export type CadastroResponse = {
  mensagem: string;
  nomeEmpresa?: string;
  email?: string;
};

export async function cadastrarCadastro(payload: CadastroRequest) {
  const url = "/cadastro"; // base is already configured in api_url

  const res = await api_url.post(url, payload).catch((e) => {
    // Normalize axios error
    const status = e?.response?.status ?? 0;
    const message = e?.response?.data?.mensagem ?? e?.response?.data?.message ?? e.message ?? "Falha de rede";
    throw { status, message, original: e };
  });

  return res.data as CadastroResponse;
}
