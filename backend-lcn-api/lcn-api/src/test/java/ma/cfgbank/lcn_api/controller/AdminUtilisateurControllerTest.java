package ma.cfgbank.lcn_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.cfgbank.lcn_api.dto.CreateUtilisateurRequest;
import ma.cfgbank.lcn_api.dto.UpdateUtilisateurRequest;
import ma.cfgbank.lcn_api.dto.UtilisateurResponse;
import ma.cfgbank.lcn_api.entity.Utilisateur;
import ma.cfgbank.lcn_api.model.RoleEnum;
import ma.cfgbank.lcn_api.repository.UtilisateurRepository;
import ma.cfgbank.lcn_api.security.JwtService;
import ma.cfgbank.lcn_api.service.ApiClientService;
import ma.cfgbank.lcn_api.service.UtilisateurService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdminUtilisateurController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminUtilisateurControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UtilisateurRepository utilisateurRepository;

    @MockBean
    private UtilisateurService utilisateurService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private ApiClientService apiClientService;

    @MockBean
    private JwtService jwtService;

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testGetAllUsers_Success() throws Exception {
        Utilisateur user = Utilisateur.builder()
                .id(1L)
                .email("admin@cfgbank.ma")
                .nomComplet("Admin")
                .role(RoleEnum.ROLE_ADMIN)
                .actif(true)
                .build();

        Mockito.when(utilisateurRepository.findAll()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/admin/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("admin@cfgbank.ma"))
                .andExpect(jsonPath("$[0].role").value("ROLE_ADMIN"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testCreateUser_Success() throws Exception {
        CreateUtilisateurRequest request = new CreateUtilisateurRequest();
        request.setEmail("new@cfgbank.ma");
        request.setNomComplet("New User");
        request.setMotDePasse("password");
        request.setRole(RoleEnum.ROLE_BUSINESS);

        Utilisateur savedUser = Utilisateur.builder()
                .id(2L)
                .email("new@cfgbank.ma")
                .nomComplet("New User")
                .role(RoleEnum.ROLE_BUSINESS)
                .actif(true)
                .build();

        Mockito.when(utilisateurRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode(request.getMotDePasse())).thenReturn("encoded-password");
        Mockito.when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(savedUser);

        mockMvc.perform(post("/api/admin/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.email").value("new@cfgbank.ma"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testCreateUser_Conflict() throws Exception {
        CreateUtilisateurRequest request = new CreateUtilisateurRequest();
        request.setEmail("existing@cfgbank.ma");
        request.setNomComplet("Existing");
        request.setMotDePasse("pass");
        request.setRole(RoleEnum.ROLE_BUSINESS);

        Mockito.when(utilisateurRepository.findByEmail("existing@cfgbank.ma"))
                .thenReturn(Optional.of(new Utilisateur()));

        mockMvc.perform(post("/api/admin/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testUpdateUser_Success() throws Exception {
        UpdateUtilisateurRequest request = new UpdateUtilisateurRequest();
        request.setEmail("updated@cfgbank.ma");
        request.setNomComplet("Updated Name");
        request.setRole(RoleEnum.ROLE_BUSINESS);

        UtilisateurResponse response = new UtilisateurResponse();
        response.setId(1L);
        response.setEmail("updated@cfgbank.ma");
        response.setNomComplet("Updated Name");
        response.setRole(RoleEnum.ROLE_BUSINESS);
        response.setActif(true);

        Mockito.when(utilisateurService.updateUtilisateur(Mockito.eq(1L), any(UpdateUtilisateurRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/admin/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@cfgbank.ma"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testUpdateUser_NotFound() throws Exception {
        UpdateUtilisateurRequest request = new UpdateUtilisateurRequest();
        request.setEmail("updated@cfgbank.ma");
        request.setNomComplet("Updated Name");
        request.setRole(RoleEnum.ROLE_BUSINESS);

        Mockito.when(utilisateurService.updateUtilisateur(Mockito.eq(99L), any(UpdateUtilisateurRequest.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        mockMvc.perform(put("/api/admin/users/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testDeleteUser_Success() throws Exception {
        Mockito.doNothing().when(utilisateurService).deleteUtilisateur(1L);

        mockMvc.perform(delete("/api/admin/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testToggleUserStatus_Success() throws Exception {
        UtilisateurResponse response = new UtilisateurResponse();
        response.setId(1L);
        response.setActif(false);

        Mockito.when(utilisateurService.toggleStatus(1L)).thenReturn(response);

        mockMvc.perform(patch("/api/admin/users/1/toggle-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actif").value(false));
    }
}
