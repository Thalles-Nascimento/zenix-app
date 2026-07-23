package cloud.zenixapp.zenix.models.interfaces;

import cloud.zenixapp.zenix.configs.utils.ServicoJsonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.List;

public interface PlanosView {
    String getId();
    String getDescricao();
    @JsonIgnore
    String getServicoRaw();

    default List<String> getServico(){
        return ServicoJsonUtils.parse(getServicoRaw());
    }
    LocalDateTime getUpdatedAt();
    Double getValor();
    int getAtendimentos();
}
