package cloud.zenixapp.zenix.models.interfaces;

import cloud.zenixapp.zenix.configs.utils.ServicoJsonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ClientesProjectionView {
    String getId();
    String getNome();
    String getTelefone();
    LocalDate getDataRenovacao();
    LocalDateTime getUpdatedAt();
    int getAtendimentoMes();
    int getRetorno();
    int getStatus();

    // --- Plano (LEFT JOIN) - todos nullable-friendly: vem null quando o cliente nao tem plano ---
    String getPlanoId();
    String getPlanoDescricao();
    Double getPlanoValor();
    String getPlanoServicoRaw();
    int getPlanoAtendimentos();

    @JsonIgnore
    default List<String> getPlanoServico() {
        return ServicoJsonUtils.parse(getPlanoServicoRaw());
    }
}