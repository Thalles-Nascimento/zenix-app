package cloud.zenixapp.zenix.configs.mappers;

import cloud.zenixapp.zenix.models.dtos.requests.AtendimentoRequestDTO;
import cloud.zenixapp.zenix.models.entities.Atendimento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * <h2>
 *     Interface para mapear um DTO para Entidade Atendimento.
 * </h2>
 * <p>
 *     A interface é utilizada para realizar o mapeamento entre AtendimentoRequestDTO e Atendimento.
 * </p>
 *
 * @see Mapper
 * @version 1.0
 * @author Thalles Nascimento
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AtendimentoMapper {

    /**
     * <h2>
     *     Método para atualizar um atendimento.
     * </h2>
     * <p>
     *     Este método é utilizado para realizar o mapeamento do DTO enviado na requisição para a entidade Atendimento
     *     ignorando campos que não podem ser atualizados.
     * </p>
     * @param atendimento Entidade que receberá os dados de AtendimentoRequestDTO.
     * @param atendimentoRequestDTO DTO contendo os dados que serão mapeados para entidade Atendimento.
     * @see Atendimento
     * @see AtendimentoRequestDTO
     */
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "usuarios", ignore = true)
    void atualizarAtendimento(@MappingTarget Atendimento atendimento, AtendimentoRequestDTO atendimentoRequestDTO);

    /**
     * <h2>
     *     Método para mapear um Atendimento dado um DTO.
     * </h2>
     * <p>
     *     Este método transforma um AtendimentoRequestDTO numa entidade Atendimento. É utilizado para inserir um atendimento no sistema.
     * </p>
     * @param atendimentoRequestDTO DTO contendo os dados que serão mapeados.
     * @return {@link Atendimento}
     * @see AtendimentoRequestDTO
     * @see Atendimento
     */
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "usuarios", ignore = true)
    Atendimento inserirAtendimento(AtendimentoRequestDTO atendimentoRequestDTO);
}
