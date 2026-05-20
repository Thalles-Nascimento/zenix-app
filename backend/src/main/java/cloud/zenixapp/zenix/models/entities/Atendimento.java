package cloud.zenixapp.zenix.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "atendimentos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Atendimento extends BaseEntity {

    @Column(name = "atendimento_descricao", length = 120)
    private String descricao;

//  TODO Transformar servico numa tabela do banco e refatorar - Campos servico e valor
    @Column(name = "atendimento_servico", length = 100)
    private List<String> servico;

    @Column(name = "atendimento_valor", length = 25)
    private Double valor;

    @Column(name = "atendimento_data", nullable = false)
    private String date;

    @Column(name = "atendimento_pagamento", nullable = false)
    private String formaPagamento;

    @Column(name = "atendimento_observacao", length = 500)
    private String observacao;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuarios usuarios;

    @Column(name = "atendimento_status", nullable = false)
    private Integer status = 1;

}
