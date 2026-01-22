package cloud.zenixapp.zenix.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AtendimentoRequestDTO {

    private String descricao;

    private String servico;

    private Double valor;

    public boolean validacao(){
        boolean descricao, servico;
        descricao = this.getDescricao().matches(".*\\d.*");
        servico = this.getServico().matches(".*\\d.*");

        return descricao || servico;
/*
descricao = true, servico = true -> retorna true
descricao = true, servico = false -> retorna true
descricao = false, servico = true -> retorna true
descricao = false, servico = false-> retorna false
*/
    }

}
