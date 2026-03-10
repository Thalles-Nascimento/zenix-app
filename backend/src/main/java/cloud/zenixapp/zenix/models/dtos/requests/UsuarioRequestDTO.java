package cloud.zenixapp.zenix.models.dtos.requests;

import cloud.zenixapp.zenix.models.entities.Unidades;
import cloud.zenixapp.zenix.models.enums.UsuariosRoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UsuarioRequestDTO(
        @NotNull(message = "O campo não pode ser nulo!")
        @Pattern(regexp = "^[A-Za-zÀ-ÿ ]+$", message = "O valor está fora do padrão")
        String nome,

        @Email(message = "E-mail inválido")
        @NotNull(message = "O campo não pode ser nulo")
        String email,

        @NotNull(message = "O campo não pode ser nulo")
        String cpf,

        Long unidade,

        @NotNull(message = "O campo não pode ser nulo")
        String senha,

        @NotNull(message = "O campo não pode ser nulo")
        UsuariosRoleEnum grupo

) {

}
