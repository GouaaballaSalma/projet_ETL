package ma.cfgbank.lcn_api.security;

import ma.cfgbank.lcn_api.repository.UtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@AutoConfigureMockMvc
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
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // Mock du repository pour éviter que le test de succès ne cherche à lire une vraie base de données
    @MockBean
    private UtilisateurRepository utilisateurRepository;

    @Test
    void testAccess_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
        // Cas 1 : Aucune authentification, ni token, ni MockUser.
        // Spring Security doit renvoyer un 401 Unauthorized ou 403 Forbidden
        // (Généralement 401 si un AuthenticationEntryPoint est bien configuré pour les requêtes REST)
        mockMvc.perform(get("/api/admin/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_BUSINESS")
    void testAccess_InsufficientRole_ShouldReturnForbidden() throws Exception {
        // Cas 2 : Utilisateur authentifié mais avec le mauvais rôle (ROLE_BUSINESS).
        // Le endpoint exige ROLE_ADMIN via @PreAuthorize.
        mockMvc.perform(get("/api/admin/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testAccess_AuthorizedRole_ShouldReturnOk() throws Exception {
        // Cas 3 : Utilisateur authentifié avec le rôle ROLE_ADMIN.
        // Le endpoint doit être accessible (Code 200 OK).
        when(utilisateurRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/admin/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
