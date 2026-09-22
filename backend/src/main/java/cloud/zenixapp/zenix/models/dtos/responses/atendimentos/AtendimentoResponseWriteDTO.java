package cloud.zenixapp.zenix.models.dtos.responses.atendimentos;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <h2>
 *      Record AtendimentoResponseWriteDTO
 * </h2>
 * <p>
 *     DTO de resposta do Domínio Atendimento usado para atualizar um atendimento.
 * </p>
 * @param id ID do atendimento
 * @param descricao Nome do cliente do atendimento
 * @param servico Lista de serviços do atendimento
 * @param valor Valor total do atendimento
 * @param formaPagamento Forma de pagamento do atendimento
 * @param date Data do atendimento
 * @param updatedAt Data e hora da última atualização do atendimento
 * @param deletedAt Data e hora que o atendimento foi deletado
 * @param observacao Observação do atendimento
 * @param status
 */
public record AtendimentoResponseWriteDTO(
        String id,

        String descricao,

        List<String> servico,

        Double valor,

        String formaPagamento,

        String date,

        LocalDateTime updatedAt,

        LocalDateTime deletedAt,

        String observacao,

        int status
){

}
