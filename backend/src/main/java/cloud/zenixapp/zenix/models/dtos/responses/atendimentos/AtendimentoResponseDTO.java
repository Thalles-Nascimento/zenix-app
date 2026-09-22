package cloud.zenixapp.zenix.models.dtos.responses.atendimentos;

import java.util.List;

/**
 * <h2>
 *      Record AtendimentoResponseDTO
 * </h2>
 * <p>
 *     DTO de resposta padrão do Domínio Atendimento.
 * </p>
 * @param id ID do atendimento
 * @param descricao Nome do cliente do atendimento
 * @param servico Lista de serviços do atendimento
 * @param valor Valor total do atendimento
 * @param formaPagamento Forma de pagamento do atendimento
 * @param date Data do atendimento
 * @param observacao Observação do atendimento
 * @param status
 */
public record AtendimentoResponseDTO(
        String id,

        String descricao,

        List<String> servico,

        Double valor,

        String formaPagamento,

        String date,

        String observacao,

        int status
){

}
