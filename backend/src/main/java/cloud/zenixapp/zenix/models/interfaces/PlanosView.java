package cloud.zenixapp.zenix.models.interfaces;

import java.util.List;

public interface PlanosView {
    String getId();
    String getDescricao();
    List<String> getServico();
    Double getValor();
    int getAtendimentos();
}
