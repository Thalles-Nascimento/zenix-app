package cloud.zenixapp.zenix.dtos;

public record RegisterRequestDTO(String nome, String email, String cpf, String senha, String grupo) {
}
