package ma.cfgbank.lcn_api.service;

import ma.cfgbank.lcn_api.dto.ClientRiskScoreDTO;
import ma.cfgbank.lcn_api.dto.RawClientStatsDTO;
import ma.cfgbank.lcn_api.repository.LcnSynthRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private LcnSynthRepository lcnSynthRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void testGetRiskScoring_Faible() {
        // 2 incidents (20 pts) + 9000 MAD (9 pts) = 29 pts <= 30 (FAIBLE)
        RawClientStatsDTO stats = new RawClientStatsDTO(
                "ID1", null, "Jean", "Dupont", "PP", 2L, new BigDecimal("9000")
        );
        when(lcnSynthRepository.findRawClientStats()).thenReturn(List.of(stats));

        List<ClientRiskScoreDTO> result = dashboardService.getRiskScoring();

        assertEquals(1, result.size());
        assertEquals(29.0, result.get(0).getRiskScore());
        assertEquals("FAIBLE", result.get(0).getNiveauRisque());
        assertEquals("Jean Dupont", result.get(0).getNomClient());
    }

    @Test
    void testGetRiskScoring_Moyen() {
        // 5 incidents (50 pts) + 2000 MAD (2 pts) = 52 pts (MOYEN : >30 et <=100)
        RawClientStatsDTO stats = new RawClientStatsDTO(
                "ID2", "Entreprise ABC", null, null, "PM", 5L, new BigDecimal("2000")
        );
        when(lcnSynthRepository.findRawClientStats()).thenReturn(List.of(stats));

        List<ClientRiskScoreDTO> result = dashboardService.getRiskScoring();

        assertEquals(1, result.size());
        assertEquals(52.0, result.get(0).getRiskScore());
        assertEquals("MOYEN", result.get(0).getNiveauRisque());
        assertEquals("Entreprise ABC", result.get(0).getNomClient());
    }

    @Test
    void testGetRiskScoring_Eleve() {
        // 10 incidents (100 pts) + 5000 MAD (5 pts) = 105 pts > 100 (ÉLEVÉ)
        RawClientStatsDTO stats = new RawClientStatsDTO(
                "ID3", "Societe XYZ", null, null, "PM", 10L, new BigDecimal("5000")
        );
        when(lcnSynthRepository.findRawClientStats()).thenReturn(List.of(stats));

        List<ClientRiskScoreDTO> result = dashboardService.getRiskScoring();

        assertEquals(1, result.size());
        assertEquals(105.0, result.get(0).getRiskScore());
        assertEquals("ÉLEVÉ", result.get(0).getNiveauRisque());
    }

    @Test
    void testGetRiskScoring_Sorting() {
        // Test que le tri s'effectue bien par score décroissant
        RawClientStatsDTO statsFaible = new RawClientStatsDTO("ID1", null, "A", "A", "PP", 1L, new BigDecimal("1000")); // Score: 11
        RawClientStatsDTO statsEleve = new RawClientStatsDTO("ID2", null, "B", "B", "PP", 10L, new BigDecimal("5000")); // Score: 105
        RawClientStatsDTO statsMoyen = new RawClientStatsDTO("ID3", null, "C", "C", "PP", 5L, new BigDecimal("2000")); // Score: 52

        when(lcnSynthRepository.findRawClientStats()).thenReturn(Arrays.asList(statsFaible, statsEleve, statsMoyen));

        List<ClientRiskScoreDTO> result = dashboardService.getRiskScoring();

        assertEquals(3, result.size());
        assertEquals("ÉLEVÉ", result.get(0).getNiveauRisque());
        assertEquals("MOYEN", result.get(1).getNiveauRisque());
        assertEquals("FAIBLE", result.get(2).getNiveauRisque());
    }
}
