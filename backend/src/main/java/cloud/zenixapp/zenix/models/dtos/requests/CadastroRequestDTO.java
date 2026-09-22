package cloud.zenixapp.zenix.models.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * <h2>
 *     Record CadastroRequestDTO
 * </h2>
 * <p>
 *     DTO para cadastro de uma Barbearia nova no sistema.
 * </p>
 * @param nomeAdmin Nome do usuário admin {@link NotBlank}
 * @param email E-mail do usuário {@link NotBlank} {@link Email}
 * @param senha Senha do usuário {@link NotBlank} {@link Size}
 * @param cpf CPF do usuário {@link NotBlank} {@link Size}
 * @param nomeEmpresa Nome da empresa/Barbearia {@link NotBlank}
 * @param cnpj CNPJ da empresa/Barbearia {@link NotBlank} {@link Size}
 * @param nomeUnidade Nome da unidade principal da barbearia {@link NotBlank}
 * @param enderecoUnidade Endereço da unidade {@link NotBlank}
 */
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
        @Size(min = 11, max = 11, message = "CPF inválido!")
        String cpf,

        @NotBlank(message = "Nome da empresa é obrigatório")
        String nomeEmpresa,

        @NotBlank(message = "CNPJ é obrigatório")
        @Size(min = 14, max = 14, message = "CNPJ inválido!")
        String cnpj,

        @NotBlank(message = "Nome da unidade é obrigatório")
        String nomeUnidade,

        @NotBlank(message = "Endereço da unidade é obrigatório")
        String enderecoUnidade
) {}