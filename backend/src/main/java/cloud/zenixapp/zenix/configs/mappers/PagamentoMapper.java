package cloud.zenixapp.zenix.configs.mappers;

import cloud.zenixapp.zenix.models.dtos.requests.PagamentoRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.PagamentoResponseDTO;
import cloud.zenixapp.zenix.models.entities.FormaPagamento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;


@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PagamentoMapper {

    @Mapping(target = "formaPagamento", source = "descricao")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    FormaPagamento toPagamento(PagamentoRequestDTO pagamentoRequestDTO);

    @Mapping(target = "formaPagamento", source = "descricao")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void atualizarFormaPagamento(@MappingTarget FormaPagamento pagamento, PagamentoRequestDTO pagamentoRequestDTO);

    @Mapping(target = "formaPagamento", source = "formaPagamento")
    PagamentoResponseDTO toPagamentoResponseDTO(FormaPagamento formaPagamento);

    List<PagamentoResponseDTO> toListFormaPagamento(List<FormaPagamento> pagamentoList);

}
