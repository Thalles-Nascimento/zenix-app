package cloud.zenixapp.zenix.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UsuarioLoginDTO(@Email String email, @NotNull String senha) {
}
