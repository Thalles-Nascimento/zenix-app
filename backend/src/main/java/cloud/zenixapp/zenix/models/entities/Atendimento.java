package cloud.zenixapp.zenix.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "atendimentos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Atendimento implements Serializable {

    @Serial
    private static final long serialVersionUID = -632158690661873289L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "atendimento_id", nullable = false)
    private Long id;

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

    //  TODO Criar mais colunas - created_at, delete_at e update_at
    @Column(name = "atendimento_created_at")
    private LocalDateTime created_at = LocalDateTime.now();

    @Column(name = "atendimento_update_at")
    private LocalDateTime update_at;

    @Column(name = "atendimento_delete_at")
    private LocalDateTime delete_at;

}
