package cloud.zenixapp.zenix.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record AuthenticationDTO(@Email String email, @NotNull String senha) {
}
