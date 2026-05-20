package cloud.zenixapp.zenix.repositories;

import cloud.zenixapp.zenix.models.entities.Tenants;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenants, String> {

    boolean existsByNome(String nome);
    boolean existsBySlug(String slug);
    boolean existsByCnpj(String cnpj);



}
