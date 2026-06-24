package ma.cfgbank.lcn_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.cfgbank.lcn_api.dto.CreateLcnSynthRequest;
import ma.cfgbank.lcn_api.dto.LcnSynthDTO;
import ma.cfgbank.lcn_api.model.TypeClient;
import ma.cfgbank.lcn_api.security.JwtService;
import ma.cfgbank.lcn_api.service.LcnSynthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LcnSynthController.class)
@AutoConfigureMockMvc
class LcnSynthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LcnSynthService lcnSynthService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private ma.cfgbank.lcn_api.service.SecurityService securityService;

    @MockBean
    private ma.cfgbank.lcn_api.service.ApiClientService apiClientService;

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void rechercherIncidents_Returns200() throws Exception {
        // Given
        LcnSynthDTO dto = new LcnSynthDTO();
        dto.setRefImpaye("REF_123");
        Page<LcnSynthDTO> page = new PageImpl<>(Collections.singletonList(dto));

        when(lcnSynthService.rechercherIncidents(eq(TypeClient.PP), isNull(), isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/lcn/recherche")
                .param("typeClient", "PP")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].refImpaye").value("REF_123"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_BUSINESS")
    void creerIncidentManuel_Returns201() throws Exception {
        // Given
        CreateLcnSynthRequest request = new CreateLcnSynthRequest();
        request.setTypeClient(TypeClient.PM);
        request.setRaisonSociale("CFG BANK");
        request.setIdentifiantFiscal("IF_1234");
        request.setNumLcn("LCN_001");
        request.setDateConstat(java.time.LocalDate.now());
        request.setRefClient("REF_C_123");
        request.setInsuffisance(java.math.BigDecimal.TEN);
        request.setCodeBanque("011");
        request.setMontant(java.math.BigDecimal.TEN);
        request.setDevise("MAD");
        request.setDateEcheance(java.time.LocalDate.now());
        request.setRib("011111111111111111111111");
        request.setDateEmission(java.time.LocalDate.now());

        LcnSynthDTO dto = new LcnSynthDTO();
        dto.setRefImpaye("REF_123");

        when(lcnSynthService.creerIncidentManuel(any(CreateLcnSynthRequest.class))).thenReturn(dto);

        // When & Then
        mockMvc.perform(post("/api/lcn")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.refImpaye").value("REF_123"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void modifierIncidentManuel_Returns200() throws Exception {
        // Given
        CreateLcnSynthRequest request = new CreateLcnSynthRequest();
        request.setTypeClient(TypeClient.PM);
        request.setRaisonSociale("CFG BANK");
        request.setIdentifiantFiscal("IF_1234");
        request.setNumLcn("LCN_001");
        request.setDateConstat(java.time.LocalDate.now());
        request.setRefClient("REF_C_123");
        request.setInsuffisance(java.math.BigDecimal.TEN);
        request.setCodeBanque("011");
        request.setMontant(java.math.BigDecimal.TEN);
        request.setDevise("MAD");
        request.setDateEcheance(java.time.LocalDate.now());
        request.setRib("011111111111111111111111");
        request.setDateEmission(java.time.LocalDate.now());
        LcnSynthDTO dto = new LcnSynthDTO();
        dto.setRefImpaye("REF_123");

        when(lcnSynthService.modifierIncidentManuel(eq("REF_123"), any(CreateLcnSynthRequest.class))).thenReturn(dto);

        // When & Then
        mockMvc.perform(put("/api/lcn/{id}", "REF_123")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.refImpaye").value("REF_123"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void supprimerIncidentManuel_Returns204() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/lcn/{id}", "REF_123")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(lcnSynthService).supprimerIncidentManuel("REF_123");
    }
}
