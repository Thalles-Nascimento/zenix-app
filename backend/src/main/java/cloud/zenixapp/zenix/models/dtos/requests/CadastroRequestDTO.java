package cloud.zenixapp.zenix.models.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastroRequestDTO(

        @NotBlank(message = "Nome do responsável é obrigatório")
        String nomeAdmin,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
        String senha,

        @NotBlank(message = "CPF é obrigatório")
        String cpf,

        @NotBlank(message = "Nome da empresa é obrigatório")
        String nomeEmpresa,

        @NotBlank(message = "CNPJ é obrigatório")
        String cnpj,

        @NotBlank(message = "Nome da unidade é obrigatório")
        String nomeUnidade,

        @NotBlank(message = "Endereço da unidade é obrigatório")
        String enderecoUnidade
) {}