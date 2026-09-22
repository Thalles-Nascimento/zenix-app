package cloud.zenixapp.zenix.repositories;

import cloud.zenixapp.zenix.models.entities.Tenants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TenantRepository extends JpaRepository<Tenants, String> {

    boolean existsByNome(String nome);
    boolean existsBySlug(String slug);
    boolean existsByCnpj(String cnpj);

    @Query("SELECT id FROM Tenants WHERE cnpj = :cnpj")
    String findIdByCnpj(@Param("cnpj") String cnpj);



}
