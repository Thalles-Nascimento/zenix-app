package cloud.zenixapp.zenix.models.enums;

public enum StatusFilaEnum {

//  AGUARDANDO = 0
    AGUARDANDO("Aguardando"),

//  EM_ATENDIMENTO = 1
    EM_ATENDIMENTO("Em Atendimento"),

//  FINALIZADO = 2
    FINALIZADO("Finalizado");

    private String status;

    StatusFilaEnum(String status) {
        this.status = status;
    }

    public String getStatus(){
        return this.status;
    }
}
