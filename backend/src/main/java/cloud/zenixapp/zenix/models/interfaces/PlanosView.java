package cloud.zenixapp.zenix.models.interfaces;

import java.time.LocalDateTime;
import java.util.List;

public interface PlanosView {
    String getId();
    String getDescricao();
    List<String> getServico();
    LocalDateTime getUpdatedAt();
    Double getValor();
    int getAtendimentos();
}
