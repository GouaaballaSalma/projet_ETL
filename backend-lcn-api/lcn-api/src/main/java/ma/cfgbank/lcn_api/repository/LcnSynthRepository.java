package ma.cfgbank.lcn_api.repository;

import ma.cfgbank.lcn_api.entity.LcnSynth;
import ma.cfgbank.lcn_api.entity.LcnSynthId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface LcnSynthRepository extends JpaRepository<LcnSynth, LcnSynthId> {
    
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM LcnSynth l WHERE l.id.refImpaye = :refImpaye")
    boolean existsByRefImpaye(@Param("refImpaye") String refImpaye);
    
    java.util.Optional<LcnSynth> findFirstByIdRefImpaye(String refImpaye);
    
    Page<LcnSynth> findByTypeClientAndIdentifiantPrincipal(String typeClient, String identifiantPrincipal, Pageable pageable);
    
    @Query("SELECT l FROM LcnSynth l WHERE l.typeClient = :typeClient AND (" +
           "LOWER(l.nom) LIKE LOWER(CONCAT('%', :nomComplet, '%')) OR " +
           "LOWER(l.prenom) LIKE LOWER(CONCAT('%', :nomComplet, '%')) OR " +
           "LOWER(CONCAT(l.nom, ' ', l.prenom)) LIKE LOWER(CONCAT('%', :nomComplet, '%')) OR " +
           "LOWER(CONCAT(l.prenom, ' ', l.nom)) LIKE LOWER(CONCAT('%', :nomComplet, '%')))")
    Page<LcnSynth> findByTypeClientAndNomComplet(@Param("typeClient") String typeClient, @Param("nomComplet") String nomComplet, Pageable pageable);
    
    @Query("SELECT l FROM LcnSynth l WHERE l.typeClient = :typeClient AND l.identifiantPrincipal = :identifiantPrincipal AND (" +
           "LOWER(l.nom) LIKE LOWER(CONCAT('%', :nomComplet, '%')) OR " +
           "LOWER(l.prenom) LIKE LOWER(CONCAT('%', :nomComplet, '%')) OR " +
           "LOWER(CONCAT(l.nom, ' ', l.prenom)) LIKE LOWER(CONCAT('%', :nomComplet, '%')) OR " +
           "LOWER(CONCAT(l.prenom, ' ', l.nom)) LIKE LOWER(CONCAT('%', :nomComplet, '%')))")
    Page<LcnSynth> findByTypeClientAndNomCompletAndIdentifiantPrincipal(@Param("typeClient") String typeClient, @Param("nomComplet") String nomComplet, @Param("identifiantPrincipal") String identifiantPrincipal, Pageable pageable);
    
    Page<LcnSynth> findByTypeClientAndRc(String typeClient, String rc, Pageable pageable);
    
    Page<LcnSynth> findByTypeClientAndIdentifiantFiscal(String typeClient, String identifiantFiscal, Pageable pageable);

    @Query("SELECT SUM(l.montant) FROM LcnSynth l")
    java.math.BigDecimal sumMontantTotal();

    @Query("SELECT l.typeClient, COUNT(l) FROM LcnSynth l GROUP BY l.typeClient")
    List<Object[]> countByTypeClient();

    @Query("SELECT new ma.cfgbank.lcn_api.dto.RawClientStatsDTO(l.identifiantPrincipal, l.raisonSociale, l.nom, l.prenom, l.typeClient, COUNT(l), SUM(l.montant)) FROM LcnSynth l GROUP BY l.identifiantPrincipal, l.raisonSociale, l.nom, l.prenom, l.typeClient")
    List<ma.cfgbank.lcn_api.dto.RawClientStatsDTO> findRawClientStats();
}