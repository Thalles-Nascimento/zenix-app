package cloud.zenixapp.zenix.repositories;

import cloud.zenixapp.zenix.models.dtos.responses.atendimentos.AtendimentoResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.atendimentos.AtendimentoResponseWriteDTO;
import cloud.zenixapp.zenix.models.entities.Atendimento;
import cloud.zenixapp.zenix.models.entities.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AtendimentoRepository extends JpaRepository<Atendimento, String> {

//    Lista o histórico por ID do barbeiro > atendimentoService.listarHistorico()
    List<AtendimentoResponseDTO> findByUsuariosAndTenant(Usuarios usuarios, String tenant);

//    Lista os atendimentos do dia - hoje ≥ atendimentoService.listarAtendimentosHoje()
    List<AtendimentoResponseDTO> findByUsuariosAndDateAndTenant(Usuarios usuarios, String date, String tenant);

//  Ativa um atendimento
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @NativeQuery(
            value = "UPDATE atendimentos a " +
                    "SET a.atendimento_status = 1, a.deleted_at = null " +
                    "WHERE a.id = :id AND a.tenant_id = :tenantId")
    void ativarAtendimento(@Param("id") String id, @Param("tenantId") String tenantId);

//    Lista todos os atendimentos por Tenants ≥ atendimentoService.listarTodosAtendimentos()
    List<AtendimentoResponseDTO> findAllByTenant(String tenant);

//  Deleta/desativa um atendimento
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @NativeQuery(
            value = "UPDATE atendimentos a " +
                    "SET a.atendimento_status = -1, a.deleted_at = :deleteTime " +
                    "WHERE a.id = :id AND a.tenant_id = :tenantId")
    void deleteLogico(@Param("id") String id, @Param("deleteTime") LocalDateTime deleteTime, @Param("tenantId") String tenantId);

//  Lista um atendimento por ID e Tenant
    Optional<AtendimentoResponseWriteDTO> findByIdAndTenant(String id, String tenant);

    @Query(value = "SELECT a FROM Atendimento a WHERE a.id = :id AND a.tenant = :tenant")
    Optional<Atendimento> findById(@Param("id") String id, @Param("tenant") String tenant);

}
