package ma.cfgbank.lcn_api.repository;

import ma.cfgbank.lcn_api.entity.ApiClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApiClientRepository extends JpaRepository<ApiClient, Long> {
    Optional<ApiClient> findByClientNameAndActiveTrue(String clientName);
}
