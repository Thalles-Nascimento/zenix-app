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
