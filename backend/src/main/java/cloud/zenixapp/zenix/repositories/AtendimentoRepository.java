package cloud.zenixapp.zenix.repositories;

import cloud.zenixapp.zenix.models.dtos.responses.atendimentos.AtendimentoResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.atendimentos.AtendimentoResponseWriteDTO;
import cloud.zenixapp.zenix.models.entities.Atendimento;
import cloud.zenixapp.zenix.models.entities.Tenants;
import cloud.zenixapp.zenix.models.entities.Usuarios;
import cloud.zenixapp.zenix.services.AtendimentoService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * <h2>
 *      Repositório da Entidade Atendimento.
 * </h2>
 * <p>
 *     O repositório é responsável por realizar as consultas no banco de dados do sistema.
 * </p>
 * @see JpaRepository
 * @see Tenants
 *
 * @version 1.0
 * @author Thalles Nascimento
 */
public interface AtendimentoRepository extends JpaRepository<Atendimento, String> {

    /**
     * <h2>
     *     Método para consultar o histórico de atendimentos de um usuário
     * </h2>
     * <p>
     *     Este método busca no banco de dados os atendimentos de um usuário.
     * </p>
     * @param usuarios Entidade {@link Usuarios} usada para claúsula Where da consulta.
     * @param tenant TenantID do inquilino que realizou a requisição - Type: {@code String}.
     * @return List<{@link AtendimentoResponseDTO}> - Pode retornar uma lista vazia.
     * @see Usuarios
     * @see AtendimentoResponseDTO
     */
    List<AtendimentoResponseDTO> findByUsuariosAndTenant(Usuarios usuarios, String tenant);

    /**
     * <h2>
     *     Método para consultar os atendimentos do dia de um usuário.
     * </h2>
     * <p>
     *     Este método busca no banco de dados os atendimentos do dia de um usuário.
     * </p>
     * @param usuarios Entidade {@link Usuarios} usada para claúsula Where da consulta.
     * @param date {@code String} Data do dia.
     * @param tenant TenantID do inquilino que realizou a requisição - Type: {@code String}.
     * @return List<{@link AtendimentoResponseDTO}> - Pode retornar uma lista vazia.
     * @see Usuarios
     * @see AtendimentoResponseDTO
     */
    List<AtendimentoResponseDTO> findByUsuariosAndDateAndTenant(Usuarios usuarios, String date, String tenant);


    /**
     * <h2>
     *     Método para ativar um atendimento.
     * </h2>
     * <p>
     *     Este método ativa um atendimento no banco de dados, realizando um Update na coluna Status e DeletedAt.
     * </p>
     * @param id 'ID' do atendimento que será ativado
     * @param tenantId TenantID do inquilino que realizou a requisição - Type: {@code String}.
     *
     * @see Modifying
     * @see NativeQuery
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @NativeQuery(
            value = "UPDATE atendimentos a " +
                    "SET a.atendimento_status = 1, a.deleted_at = null " +
                    "WHERE a.id = :id AND a.tenant_id = :tenantId")
    void ativarAtendimento(@Param("id") String id, @Param("tenantId") String tenantId);

    /**
     * <h2>
     *     Método para listar todos os atendimentos.
     * </h2>
     * <p>
     *     Este método lista todos os atendimentos da barbearia.
     * </p>
     * @param tenant TenantID do inquilino que realizou a requisição - Type: {@code String}.
     * @return List<{@link AtendimentoResponseDTO}> - Pode retornar uma lista vazia.
     * @see AtendimentoResponseDTO
     */
    List<AtendimentoResponseDTO> findAllByTenant(String tenant);

    /**
     * <h2>
     *     Método para deletar um atendimento.
     * </h2>
     * <p>
     *     Este método é utilizado para realizar um delete lógico do atendimento.
     * </p>
     * @param id ID do atendimento que será deletado.
     * @param deleteTime Data e hora que o atendimento foi deletado.
     * @param tenantId TenantID do inquilino que realizou a requisição - Type: {@code String}.
     * @see Modifying
     * @see NativeQuery
     * @see LocalDateTime
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @NativeQuery(
            value = "UPDATE atendimentos a " +
                    "SET a.atendimento_status = -1, a.deleted_at = :deleteTime " +
                    "WHERE a.id = :id AND a.tenant_id = :tenantId")
    void deleteLogico(@Param("id") String id, @Param("deleteTime") LocalDateTime deleteTime, @Param("tenantId") String tenantId);

    /**
     * <h2>
     *     Método para listar um atendimento por ID.
     * </h2>
     * <p>
     *     Este método lista um atendimento pelo ID.
     * </p>
     * @param id ID do atendimento que será retornado
     * @param tenant TenantID do inquilino que realizou a requisição - Type: {@code String}.
     * @return Optional<{@link AtendimentoResponseWriteDTO}>.
     * @see Optional
     * @see AtendimentoResponseWriteDTO
     */
    Optional<AtendimentoResponseWriteDTO> findByIdAndTenant(String id, String tenant);

    /**
     * <h2>
     *     Método para listar um atendimento por ID.
     * </h2>
     * <h6>Obs: É utilizado para atualizar um atendimento.</h6>
     * <p>
     *     Este método lista um atendimento por ID.
     * </p>
     * @param id ID do atendimento que será retornado.
     * @param tenant TenantID do inquilino que realizou a requisição - Type: {@code String}.
     * @return Optional<{@link Atendimento}>.
     *
     * @see Atendimento
     * @see AtendimentoService
     * @see Optional
     * @see Query
     */
    @Query(value = "SELECT a FROM Atendimento a WHERE a.id = :id AND a.tenant = :tenant")
    Optional<Atendimento> findById(@Param("id") String id, @Param("tenant") String tenant);

}
