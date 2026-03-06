package cloud.zenixapp.zenix.models.dtos.responses;

public record AtendimentoResponseDTO(
        Long id,

        String descricao,

        String servico,

        Double valor,

        String formaPagamento,

        String date,

        int status
){

}
