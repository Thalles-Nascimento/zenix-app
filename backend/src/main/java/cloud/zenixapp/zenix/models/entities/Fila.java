package cloud.zenixapp.zenix.models.entities;

import cloud.zenixapp.zenix.models.enums.StatusFilaEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.List;


@Entity
@Table(name = "fila_atendimentos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Fila implements Serializable {

    @Serial
    private static final long serialVersionUID = -6394858690661873289L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fila_id", nullable = false)
    private Long id;

    @Column(name = "fila_client", length = 120)
    private String nomeCliente;

    @Column(name = "fila_servico")
    private List<String> servico;

    @Column(name = "fila_pagamento")
    private String formaPagamento;

    @Column(name = "fila_telefone", unique = true)
    private String telefoneCliente;

    @Column(name = "fila_horario", nullable = false)
    private LocalTime horario = LocalTime.now();

    @ManyToOne
    @JoinColumn(name = "fila_usuario_id")
    private Usuarios usuario;

    @Column(name = "fila_inicio_atendimento")
    private LocalTime inicioAtendimento;

    @Column(name = "fila_final_atendimento")
    private LocalTime fimAtendimento;

    @Column(name = "fila_status", nullable = false)
    private StatusFilaEnum status = StatusFilaEnum.AGUARDANDO;

}
