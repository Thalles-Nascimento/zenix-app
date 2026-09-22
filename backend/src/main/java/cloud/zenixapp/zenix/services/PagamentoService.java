package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.configs.TenantContext;
import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.mappers.PagamentoMapper;
import cloud.zenixapp.zenix.models.dtos.requests.PagamentoRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.SuccessResponseDTO;
import cloud.zenixapp.zenix.models.entities.FormaPagamento;
import cloud.zenixapp.zenix.models.entities.Tenants;
import cloud.zenixapp.zenix.models.interfaces.FormaPagamentoView;
import cloud.zenixapp.zenix.repositories.PagamentoRepository;
import cloud.zenixapp.zenix.repositories.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private PagamentoMapper pagamentoMapper;

    @Autowired
    private TenantRepository tenantRepository;

    @Transactional
    public SuccessResponseDTO inserirPagamento(PagamentoRequestDTO pagamentoRequestDTO){
        FormaPagamento formaPagamento = new FormaPagamento();
        formaPagamento.setFormaPagamento(pagamentoRequestDTO.descricao().toUpperCase());
        formaPagamento.setTenant(TenantContext.getTenantId());

        pagamentoRepository.save(formaPagamento);

        return new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Forma de pagamento inserida com sucesso"
        );
    }

    public List<FormaPagamentoView> buscarTodasFormaPagamento(){
        return pagamentoRepository.findAll(TenantContext.getTenantId());
    }

    @Transactional
    public SuccessResponseDTO atualizarPagamento(PagamentoRequestDTO pagamentoRequestDTO, String id){
        return pagamentoRepository.findById(id, TenantContext.getTenantId())
                .map(formaPagamentoView -> {

                    FormaPagamento formaPagamento = pagamentoMapper.fromFormaPagamentoViewtoPagamento(formaPagamentoView);

                    pagamentoMapper.atualizarFormaPagamento(formaPagamento, pagamentoRequestDTO);
                    formaPagamento.setFormaPagamento(formaPagamento.getFormaPagamento().toUpperCase());
                    pagamentoRepository.save(formaPagamento);

                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Forma de pagamento atualizada com sucesso"
                    );
                }).orElseThrow(() -> new NotFoundException("Forma de pagamento não encontrada!"));
    }

    @Transactional
    public SuccessResponseDTO deletarPagamento(String id) {
        return pagamentoRepository.findById(id, TenantContext.getTenantId())
                .map(formaPagamento -> {

                    pagamentoRepository.deleteById(formaPagamento.getId());

                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Forma de pagamento excluída com sucesso!"
                    );

                })
                .orElseThrow(() -> new NotFoundException("Forma de pagamento não encontrada!"));
    }

}
