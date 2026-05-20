package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.mappers.PagamentoMapper;
import cloud.zenixapp.zenix.models.dtos.requests.PagamentoRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.PagamentoResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.SuccessResponseDTO;
import cloud.zenixapp.zenix.models.entities.FormaPagamento;
import cloud.zenixapp.zenix.repositories.PagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private PagamentoMapper pagamentoMapper;

    @Transactional
    public SuccessResponseDTO inserirPagamento(PagamentoRequestDTO pagamentoRequestDTO){
        FormaPagamento formaPagamento = new FormaPagamento();
        formaPagamento.setFormaPagamento(pagamentoRequestDTO.descricao().toUpperCase());

        pagamentoRepository.save(formaPagamento);

        return new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Forma de pagamento inserida com sucesso"
        );
    }

    public List<PagamentoResponseDTO> buscarTodasFormaPagamento(){
        return pagamentoMapper.toListFormaPagamento(pagamentoRepository.findAll());
    }

    @Transactional
    public SuccessResponseDTO atualizarPagamento(PagamentoRequestDTO pagamentoRequestDTO, String id){
        return pagamentoRepository.findById(id)
                .map(formaPagamento -> {

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
        return pagamentoRepository.findById(id)
                .map(formaPagamento -> {
                    pagamentoRepository.deleteById(id);

                    return new SuccessResponseDTO(
                            HttpStatus.OK.value(),
                            "Forma de pagamento excluída com sucesso!"
                    );

                })
                .orElseThrow(() -> new NotFoundException("Forma de pagamento não encontrada!"));
    }

}
