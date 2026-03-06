package cloud.zenixapp.zenix.models.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UsuarioLoginDTO(
        @Email(message = "E-mail inválido")
        @NotNull(message = "Campo não pode ser nulo")
        String email,

        @NotNull(message = "Campo não pode ser nulo")
        String senha
) {

}
