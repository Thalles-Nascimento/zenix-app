package cloud.zenixapp.zenix.models.interfaces;


import java.time.LocalDateTime;

public interface FormaPagamentoView {
    String getId();
    String getPagamento();
    LocalDateTime getUpdatedAt();
    int getStatus();
}
