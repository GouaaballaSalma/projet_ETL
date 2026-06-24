package ma.cfgbank.lcn_api;

import ma.cfgbank.lcn_api.dto.AuthenticationRequest;
import ma.cfgbank.lcn_api.dto.AuthenticationResponse;
import ma.cfgbank.lcn_api.dto.DashboardStatsDTO;
import ma.cfgbank.lcn_api.entity.LcnSynth;
import ma.cfgbank.lcn_api.entity.LcnSynthId;
import ma.cfgbank.lcn_api.entity.Utilisateur;
import ma.cfgbank.lcn_api.model.RoleEnum;
import ma.cfgbank.lcn_api.repository.LcnSynthRepository;
import ma.cfgbank.lcn_api.repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS LCN_USER",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.default_schema=LCN_USER",
        "spring.jpa.generate-ddl=true",
        "spring.flyway.enabled=false",
        "spring.liquibase.enabled=false"
})
class EndToEndIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private LcnSynthRepository lcnSynthRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // Nettoyage de la base de données H2 avant chaque test
        utilisateurRepository.deleteAll();
        lcnSynthRepository.deleteAll();

        // Etape A : Préparation des données

        // 1. Création de l'utilisateur Admin
        Utilisateur admin = Utilisateur.builder()
                .email("admin@cfgbank.ma")
                .nomComplet("Administrateur Test")
                .motDePasse(passwordEncoder.encode("admin123"))
                .role(RoleEnum.ROLE_ADMIN)
                .actif(true)
                .build();
        utilisateurRepository.save(admin);

        // 2. Insertion de données LcnSynth (Incidents)
        LcnSynth incidentPP = LcnSynth.builder()
                .id(new LcnSynthId("IMP001", "001"))
                .typeClient("PP")
                .nom("Dupont")
                .prenom("Jean")
                .identifiantPrincipal("C123456")
                .montant(new BigDecimal("15000.00"))
                .build();

        LcnSynth incidentPM = LcnSynth.builder()
                .id(new LcnSynthId("IMP002", "001"))
                .typeClient("PM")
                .raisonSociale("Entreprise ABC")
                .identifiantPrincipal("RC98765")
                .montant(new BigDecimal("25000.00"))
                .build();

        lcnSynthRepository.save(incidentPP);
        lcnSynthRepository.save(incidentPM);
    }

    @Test
    void testEndToEnd_DashboardStatsFlow() {
        // Etape B : Authentification et récupération du JWT
        AuthenticationRequest authRequest = AuthenticationRequest.builder()
                .email("admin@cfgbank.ma")
                .motDePasse("admin123")
                .build();

        ResponseEntity<AuthenticationResponse> authResponse = restTemplate.postForEntity(
                "/api/auth/login",
                authRequest,
                AuthenticationResponse.class
        );

        assertEquals(HttpStatus.OK, authResponse.getStatusCode());
        assertNotNull(authResponse.getBody());
        String jwtToken = authResponse.getBody().getToken();
        assertNotNull(jwtToken);

        // Etape C : Action métier (Appel API sécurisé avec JWT)
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<DashboardStatsDTO> statsResponse = restTemplate.exchange(
                "/api/admin/dashboard/stats",
                HttpMethod.GET,
                requestEntity,
                DashboardStatsDTO.class
        );

        // Etape D : Validation des résultats métiers E2E
        assertEquals(HttpStatus.OK, statsResponse.getStatusCode());
        assertNotNull(statsResponse.getBody());

        DashboardStatsDTO stats = statsResponse.getBody();
        
        // 2 incidents insérés (1 PP, 1 PM)
        assertEquals(2L, stats.getTotalIncidents());
        
        // Montant global = 15000 + 25000 = 40000
        assertEquals(new BigDecimal("40000.00"), stats.getMontantGlobal());
        
        // Répartition
        assertEquals(1L, stats.getRepartitionTypeClient().get("PP"));
        assertEquals(1L, stats.getRepartitionTypeClient().get("PM"));
    }
}
