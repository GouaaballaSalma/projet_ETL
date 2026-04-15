package ma.cfgbank.lcn_api.repository;

import ma.cfgbank.lcn_api.entity.LcnSynth;
import ma.cfgbank.lcn_api.entity.LcnSynthId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LcnSynthRepository extends JpaRepository<LcnSynth, LcnSynthId> {
    
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM LcnSynth l WHERE l.id.refImpaye = :refImpaye")
    boolean existsByRefImpaye(@Param("refImpaye") String refImpaye);
    
    List<LcnSynth> findByTypeClientAndIdentifiantPrincipal(String typeClient, String identifiantPrincipal);
    
    List<LcnSynth> findByTypeClientAndRc(String typeClient, String rc);
    
    List<LcnSynth> findByTypeClientAndIdentifiantFiscal(String typeClient, String identifiantFiscal);
}