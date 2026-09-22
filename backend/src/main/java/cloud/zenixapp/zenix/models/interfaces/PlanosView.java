package cloud.zenixapp.zenix.models.interfaces;

import cloud.zenixapp.zenix.configs.utils.ServicoJsonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.List;

public interface PlanosView {
    String getPlanoId();
    String getPlanoDescricao();
    @JsonIgnore
    String getServicoRaw();

    default List<String> getServico(){
        return ServicoJsonUtils.parse(getServicoRaw());
    }
    LocalDateTime getUpdatedAt();
    Double getPlanoValor();
    int getPlanoAtendimentos();
}
