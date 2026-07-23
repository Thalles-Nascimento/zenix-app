package cloud.zenixapp.zenix.models.interfaces;


import cloud.zenixapp.zenix.configs.utils.ServicoJsonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalTime;
import java.util.List;

public interface FilaProjectionView {
    String getId();
    String getNomeCliente();
    @JsonIgnore
    String getServicoRaw();

    default List<String> getServico(){
        return ServicoJsonUtils.parse(getServicoRaw());
    }
    String getFormaPagamento();
    LocalTime getHorario();
    int getStatus();
    boolean getSemPreferencia();

}
