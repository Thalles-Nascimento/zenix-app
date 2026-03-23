package cloud.zenixapp.zenix.models.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioLoginDTO(
        @Email(message = "E-mail inválido")
        @NotNull(message = "Campo não pode ser nulo")
        String email,

        @NotNull(message = "Campo não pode ser nulo")
        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
        String senha
) {

}
