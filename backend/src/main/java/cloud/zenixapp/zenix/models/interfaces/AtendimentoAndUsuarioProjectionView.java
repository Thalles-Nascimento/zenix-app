package cloud.zenixapp.zenix.models.interfaces;


import cloud.zenixapp.zenix.configs.utils.ServicoJsonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public interface AtendimentoAndUsuarioProjectionView {
    String getId();
    String getDescricao();

    @JsonIgnore
    String getServicoRaw();

    default List<String> getServico(){
        return ServicoJsonUtils.parse(getServicoRaw());
    }
    Double getValor();
    String getFormaPagamento();
    String getData();
    String getObservacao();
    String getNomeUsuario();
    int getStatus();
}
