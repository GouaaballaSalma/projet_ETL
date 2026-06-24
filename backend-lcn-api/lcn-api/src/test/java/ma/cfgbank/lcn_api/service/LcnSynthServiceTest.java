package ma.cfgbank.lcn_api.service;

import ma.cfgbank.lcn_api.dto.CreateLcnSynthRequest;
import ma.cfgbank.lcn_api.dto.LcnSynthDTO;
import ma.cfgbank.lcn_api.entity.LcnSynth;
import ma.cfgbank.lcn_api.entity.LcnSynthId;
import ma.cfgbank.lcn_api.exception.LcnBusinessException;
import ma.cfgbank.lcn_api.model.TypeClient;
import ma.cfgbank.lcn_api.model.TypeIdentifiantPM;
import ma.cfgbank.lcn_api.model.TypeIdentifiantPP;
import ma.cfgbank.lcn_api.repository.LcnSynthRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LcnSynthServiceTest {

    @Mock
    private LcnSynthRepository repository;

    @Mock
    private LcnSynthMapper mapper;

    @InjectMocks
    private LcnSynthService lcnSynthService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void rechercherIncidents_PP_MissingIdentifierAndName_ThrowsException() {
        // When & Then
        LcnBusinessException exception = assertThrows(LcnBusinessException.class, () -> {
            lcnSynthService.rechercherIncidents(TypeClient.PP, "", "", null, null, pageable);
        });
        assertEquals("L'identifiant ou le nom complet (au moins un des deux) est obligatoire pour les Personnes Physiques (PP)", exception.getMessage());
    }

    @Test
    void rechercherIncidents_PP_WithIdentifierButMissingType_ThrowsException() {
        // When & Then
        LcnBusinessException exception = assertThrows(LcnBusinessException.class, () -> {
            lcnSynthService.rechercherIncidents(TypeClient.PP, "ID123", "", null, null, pageable);
        });
        assertEquals("Le type d'identifiant PP (CIN, PASSEPORT ou SEJOUR) est obligatoire si l'identifiant est fourni", exception.getMessage());
    }

    @Test
    void rechercherIncidents_PP_WithBothIdentifierAndName_ReturnsPage() {
        // Given
        LcnSynth entity = new LcnSynth();
        LcnSynthDTO dto = new LcnSynthDTO();
        Page<LcnSynth> page = new PageImpl<>(Collections.singletonList(entity));
        
        when(repository.findByTypeClientAndNomCompletAndIdentifiantPrincipal("PP", "Doe John", "ID123", pageable))
                .thenReturn(page);
        when(mapper.toDTO(entity)).thenReturn(dto);

        // When
        Page<LcnSynthDTO> result = lcnSynthService.rechercherIncidents(TypeClient.PP, "ID123", "Doe John", null, TypeIdentifiantPP.CIN, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(repository).findByTypeClientAndNomCompletAndIdentifiantPrincipal("PP", "Doe John", "ID123", pageable);
    }

    @Test
    void rechercherIncidents_PP_WithNameOnly_ReturnsPage() {
        // Given
        LcnSynth entity = new LcnSynth();
        LcnSynthDTO dto = new LcnSynthDTO();
        Page<LcnSynth> page = new PageImpl<>(Collections.singletonList(entity));
        
        when(repository.findByTypeClientAndNomComplet("PP", "Doe John", pageable))
                .thenReturn(page);
        when(mapper.toDTO(entity)).thenReturn(dto);

        // When
        Page<LcnSynthDTO> result = lcnSynthService.rechercherIncidents(TypeClient.PP, null, "Doe John", null, null, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(repository).findByTypeClientAndNomComplet("PP", "Doe John", pageable);
    }

    @Test
    void rechercherIncidents_PM_MissingType_ThrowsException() {
        // When & Then
        LcnBusinessException exception = assertThrows(LcnBusinessException.class, () -> {
            lcnSynthService.rechercherIncidents(TypeClient.PM, "ID123", "", null, null, pageable);
        });
        assertEquals("Le type d'identifiant PM (RC ou IF) est obligatoire pour les Personnes Morales (PM)", exception.getMessage());
    }

    @Test
    void rechercherIncidents_PM_MissingIdentifier_ThrowsException() {
        // When & Then
        LcnBusinessException exception = assertThrows(LcnBusinessException.class, () -> {
            lcnSynthService.rechercherIncidents(TypeClient.PM, "", "", TypeIdentifiantPM.RC, null, pageable);
        });
        assertEquals("L'identifiant est obligatoire pour les Personnes Morales (PM)", exception.getMessage());
    }

    @Test
    void rechercherIncidents_PM_WithRC_ReturnsPage() {
        // Given
        LcnSynth entity = new LcnSynth();
        LcnSynthDTO dto = new LcnSynthDTO();
        Page<LcnSynth> page = new PageImpl<>(Collections.singletonList(entity));
        
        when(repository.findByTypeClientAndRc("PM", "RC123", pageable)).thenReturn(page);
        when(mapper.toDTO(entity)).thenReturn(dto);

        // When
        Page<LcnSynthDTO> result = lcnSynthService.rechercherIncidents(TypeClient.PM, "RC123", null, TypeIdentifiantPM.RC, null, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void rechercherIncidents_PM_WithIF_ReturnsPage() {
        // Given
        LcnSynth entity = new LcnSynth();
        LcnSynthDTO dto = new LcnSynthDTO();
        Page<LcnSynth> page = new PageImpl<>(Collections.singletonList(entity));
        
        when(repository.findByTypeClientAndIdentifiantFiscal("PM", "IF123", pageable)).thenReturn(page);
        when(mapper.toDTO(entity)).thenReturn(dto);

        // When
        Page<LcnSynthDTO> result = lcnSynthService.rechercherIncidents(TypeClient.PM, "IF123", null, TypeIdentifiantPM.IF, null, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void creerIncidentManuel_PP_MissingNames_ThrowsException() {
        // Given
        CreateLcnSynthRequest request = new CreateLcnSynthRequest();
        request.setTypeClient(TypeClient.PP);
        // Missing nom and prenom

        // When & Then
        LcnBusinessException exception = assertThrows(LcnBusinessException.class, () -> {
            lcnSynthService.creerIncidentManuel(request);
        });
        assertEquals("Nom et prénom sont obligatoires pour un client PP", exception.getMessage());
    }

    @Test
    void creerIncidentManuel_PM_MissingRaisonSociale_ThrowsException() {
        // Given
        CreateLcnSynthRequest request = new CreateLcnSynthRequest();
        request.setTypeClient(TypeClient.PM);
        // Missing raisonSociale

        // When & Then
        LcnBusinessException exception = assertThrows(LcnBusinessException.class, () -> {
            lcnSynthService.creerIncidentManuel(request);
        });
        assertEquals("Raison sociale obligatoire pour un client PM", exception.getMessage());
    }

    @Test
    void creerIncidentManuel_Success() {
        // Given
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin@cfgbank.ma");

        CreateLcnSynthRequest request = new CreateLcnSynthRequest();
        request.setTypeClient(TypeClient.PP);
        request.setNom(" Doe ");
        request.setPrenom(" John ");

        when(repository.existsByRefImpaye(anyString())).thenReturn(false);
        when(repository.save(any(LcnSynth.class))).thenAnswer(i -> i.getArgument(0));
        when(mapper.toDTO(any(LcnSynth.class))).thenReturn(new LcnSynthDTO());

        // When
        LcnSynthDTO result = lcnSynthService.creerIncidentManuel(request);

        // Then
        assertNotNull(result);
        verify(repository).save(any(LcnSynth.class));
    }

    @Test
    void modifierIncidentManuel_NotFound_ThrowsException() {
        // Given
        when(repository.findFirstByIdRefImpaye("REF_123")).thenReturn(Optional.empty());

        // When & Then
        LcnBusinessException exception = assertThrows(LcnBusinessException.class, () -> {
            lcnSynthService.modifierIncidentManuel("REF_123", new CreateLcnSynthRequest());
        });
        assertEquals("Incident LCN introuvable", exception.getMessage());
    }

    @Test
    void modifierIncidentManuel_Success() {
        // Given
        LcnSynth entity = new LcnSynth();
        when(repository.findFirstByIdRefImpaye("REF_123")).thenReturn(Optional.of(entity));
        
        CreateLcnSynthRequest request = new CreateLcnSynthRequest();
        request.setMontant(new BigDecimal("1000.00"));
        request.setStatut("Nouveau statut");

        when(mapper.toDTO(entity)).thenReturn(new LcnSynthDTO());

        // When
        LcnSynthDTO result = lcnSynthService.modifierIncidentManuel("REF_123", request);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("1000.00"), entity.getMontant());
        assertEquals("Nouveau statut", entity.getStatut());
    }

    @Test
    void supprimerIncidentManuel_NotFound_ThrowsException() {
        // Given
        when(repository.findFirstByIdRefImpaye("REF_123")).thenReturn(Optional.empty());

        // When & Then
        LcnBusinessException exception = assertThrows(LcnBusinessException.class, () -> {
            lcnSynthService.supprimerIncidentManuel("REF_123");
        });
        assertEquals("Incident LCN introuvable", exception.getMessage());
    }

    @Test
    void supprimerIncidentManuel_Success() {
        // Given
        LcnSynth entity = new LcnSynth();
        when(repository.findFirstByIdRefImpaye("REF_123")).thenReturn(Optional.of(entity));

        // When
        lcnSynthService.supprimerIncidentManuel("REF_123");

        // Then
        verify(repository).delete(entity);
    }
}
