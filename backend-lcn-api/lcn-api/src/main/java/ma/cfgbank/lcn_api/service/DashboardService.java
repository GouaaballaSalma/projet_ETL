package ma.cfgbank.lcn_api.service;

import lombok.RequiredArgsConstructor;
import ma.cfgbank.lcn_api.dto.ClientRiskScoreDTO;
import ma.cfgbank.lcn_api.dto.DashboardStatsDTO;
import ma.cfgbank.lcn_api.dto.RawClientStatsDTO;
import ma.cfgbank.lcn_api.repository.LcnSynthRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final LcnSynthRepository lcnSynthRepository;

    public DashboardStatsDTO getGlobalStats() {
        Long totalIncidents = lcnSynthRepository.count();
        BigDecimal montantGlobal = lcnSynthRepository.sumMontantTotal();
        if (montantGlobal == null) montantGlobal = BigDecimal.ZERO;

        List<Object[]> repartition = lcnSynthRepository.countByTypeClient();
        Map<String, Long> repMap = new HashMap<>();
        for (Object[] row : repartition) {
            String type = (String) row[0];
            Long count = (Long) row[1];
            repMap.put(type != null ? type : "INCONNU", count);
        }

        return DashboardStatsDTO.builder()
                .totalIncidents(totalIncidents)
                .montantGlobal(montantGlobal)
                .repartitionTypeClient(repMap)
                .build();
    }

    public List<ClientRiskScoreDTO> getRiskScoring() {
        List<RawClientStatsDTO> rawStats = lcnSynthRepository.findRawClientStats();

        return rawStats.stream().map(raw -> {
            String nomClient = "PP".equals(raw.getTypeClient()) ? 
                ((raw.getNom() != null ? raw.getNom() : "") + " " + (raw.getPrenom() != null ? raw.getPrenom() : "")).trim() : 
                raw.getRaisonSociale();
            
            if (nomClient == null || nomClient.trim().isEmpty()) {
                nomClient = raw.getIdentifiantPrincipal();
            }

            // Algorithme de Scoring
            // Poids_1: 10 points par incident
            // Poids_2: 1 point pour chaque 1000 MAD
            double score = (raw.getTotalIncidents() * 10.0);
            if (raw.getMontantTotal() != null) {
                score += (raw.getMontantTotal().doubleValue() / 1000.0);
            }

            String niveauRisque;
            if (score > 100) {
                niveauRisque = "ÉLEVÉ";
            } else if (score > 30) {
                niveauRisque = "MOYEN";
            } else {
                niveauRisque = "FAIBLE";
            }

            return ClientRiskScoreDTO.builder()
                    .nomClient(nomClient)
                    .typeClient(raw.getTypeClient())
                    .totalIncidents(raw.getTotalIncidents())
                    .montantTotal(raw.getMontantTotal() != null ? raw.getMontantTotal() : BigDecimal.ZERO)
                    .riskScore(Math.round(score * 100.0) / 100.0)
                    .niveauRisque(niveauRisque)
                    .build();
        })
        .sorted((a, b) -> b.getRiskScore().compareTo(a.getRiskScore()))
        .collect(Collectors.toList());
    }
}
