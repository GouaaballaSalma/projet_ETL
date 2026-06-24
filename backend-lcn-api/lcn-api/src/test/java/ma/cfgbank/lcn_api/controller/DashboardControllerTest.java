package ma.cfgbank.lcn_api.controller;

import ma.cfgbank.lcn_api.dto.ClientRiskScoreDTO;
import ma.cfgbank.lcn_api.dto.DashboardStatsDTO;
import ma.cfgbank.lcn_api.security.JwtService;
import ma.cfgbank.lcn_api.service.ApiClientService;
import ma.cfgbank.lcn_api.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @MockBean
    private ApiClientService apiClientService;

    @MockBean
    private JwtService jwtService;

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testGetStats_Success() throws Exception {
        DashboardStatsDTO stats = DashboardStatsDTO.builder()
                .totalIncidents(10L)
                .montantGlobal(new BigDecimal("150000.00"))
                .repartitionTypeClient(Map.of("PP", 5L, "PM", 5L))
                .build();

        Mockito.when(dashboardService.getGlobalStats()).thenReturn(stats);

        mockMvc.perform(get("/api/admin/dashboard/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIncidents").value(10))
                .andExpect(jsonPath("$.repartitionTypeClient.PP").value(5));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testGetRiskScoring_Success() throws Exception {
        ClientRiskScoreDTO riskScore = ClientRiskScoreDTO.builder()
                .nomClient("Jean Dupont")
                .typeClient("PP")
                .totalIncidents(2L)
                .montantTotal(new BigDecimal("9000"))
                .riskScore(29.0)
                .niveauRisque("FAIBLE")
                .build();

        Mockito.when(dashboardService.getRiskScoring()).thenReturn(List.of(riskScore));

        mockMvc.perform(get("/api/admin/dashboard/risk-scoring")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nomClient").value("Jean Dupont"))
                .andExpect(jsonPath("$[0].niveauRisque").value("FAIBLE"));
    }
}
