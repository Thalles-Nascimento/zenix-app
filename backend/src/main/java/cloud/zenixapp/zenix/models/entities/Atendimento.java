package cloud.zenixapp.zenix.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <h2>
 *     Entidade Atendimento.
 * </h2>
 * <p>
 *     Classe responsável pelo Domínio Atendimento.
 * </p>
 *
 * @see Entity
 * @see BaseEntity
 *
 * @version 1.0
 * @author Thalles Nascimento
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "atendimentos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Atendimento extends BaseEntity {

    /**
     * <h2>
     *     Descrição
     * </h2>
     * <p>
     *     Usado para descrever o nome do cliente que foi atendido.
     * </p>
     *
     * <h6>
     *     {@code private} {@link String} descrição
     * </h6>
     */
    @Column(name = "atendimento_descricao", length = 120)
    private String descricao;

    /**
     * <h2>
     *     Serviço
     * </h2>
     * <p>
     *     Lista dos serviços realizados pelo cliente.
     * </p>
     *
     * <h6>
     *     {@code private} List<{@link String}> servico
     * </h6>
     */
    @Column(name = "atendimento_servico", length = 100)
    private List<String> servico;

    /**
     * <h2>
     *     Valor
     * </h2>
     * <p>
     *     Valor do atendimento.
     * </p>
     *
     * <h6>
     *     {@code private} {@link Double} valor
     * </h6>
     */
    @Column(name = "atendimento_valor", length = 25)
    private Double valor;

    /**
     * <h2>
     *     Data
     * </h2>
     * <h6>
     *     Tipo = {@link String}
     * </h6>
     * <p>
     *     Data que o atendimento foi realizado.
     * </p>
     *
     * <h6>
     *     {@code private} {@link String} date
     * </h6>
     */
    @Column(name = "atendimento_data", nullable = false)
    private String date;

    /**
     * <h2>
     *     Forma de Pagamento
     * </h2>
     * <p>
     *     Forma de pagamento escolhida pelo cliente.
     * </p>
     *
     * <h6>
     *     {@code private} {@link String} formaPagamento
     * </h6>
     */
    @Column(name = "atendimento_pagamento", nullable = false)
    private String formaPagamento;

    /**
     * <h2>
     *     Observação
     * </h2>
     * <h6>
     *     Opcional
     * </h6>
     * <p>
     *     Caso tenha alguma observação à se fazer para esse atendimento.
     * </p>
     *
     * <h6>
     *     {@code private} {@link String} observacao
     * </h6>
     */
    @Column(name = "atendimento_observacao", length = 500)
    private String observacao;

    /**
     * <h2>
     *     Usuários
     * </h2>
     * <p>
     *     Relacionamento com a entidade {@link Usuarios}.
     * </p>
     *
     * <h6>
     *     {@code private} {@link Usuarios} usuarios
     * </h6>
     * @see Usuarios
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuarios usuarios;

    /**
     * <h2>
     *     Status
     * </h2>
     * <p>
     *     Status do atendimento.
     * </p>
     *
     * <h6>
     *     {@code private} {@link Integer} status
     * </h6>
     *
     * {@code Default = 1}
     */
    @Column(name = "atendimento_status", nullable = false)
    private Integer status = 1;

}
