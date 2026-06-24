package ma.cfgbank.lcn_api.service;

import ma.cfgbank.lcn_api.dto.UpdateUtilisateurRequest;
import ma.cfgbank.lcn_api.dto.UtilisateurResponse;
import ma.cfgbank.lcn_api.entity.Utilisateur;
import ma.cfgbank.lcn_api.model.RoleEnum;
import ma.cfgbank.lcn_api.repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UtilisateurServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @InjectMocks
    private UtilisateurService utilisateurService;

    private Utilisateur utilisateur;
    private UpdateUtilisateurRequest updateRequest;

    @BeforeEach
    void setUp() {
        utilisateur = Utilisateur.builder()
                .id(1L)
                .email("old@cfgbank.ma")
                .nomComplet("Ancien Nom")
                .motDePasse("password")
                .role(RoleEnum.ROLE_BUSINESS)
                .actif(true)
                .build();

        updateRequest = new UpdateUtilisateurRequest();
        updateRequest.setEmail("new@cfgbank.ma");
        updateRequest.setNomComplet("Nouveau Nom");
        updateRequest.setRole(RoleEnum.ROLE_ADMIN);
    }

    @Test
    void testUpdateUtilisateur_Success() {
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));
        when(utilisateurRepository.findByEmail("new@cfgbank.ma")).thenReturn(Optional.empty());
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UtilisateurResponse response = utilisateurService.updateUtilisateur(1L, updateRequest);

        assertNotNull(response);
        assertEquals("new@cfgbank.ma", response.getEmail());
        assertEquals("Nouveau Nom", response.getNomComplet());
        assertEquals(RoleEnum.ROLE_ADMIN, response.getRole());

        verify(utilisateurRepository, times(1)).save(any(Utilisateur.class));
    }

    @Test
    void testUpdateUtilisateur_NotFound() {
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
                utilisateurService.updateUtilisateur(1L, updateRequest)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Utilisateur introuvable", exception.getReason());
    }

    @Test
    void testUpdateUtilisateur_EmailConflict() {
        Utilisateur existingUserWithEmail = Utilisateur.builder().id(2L).email("new@cfgbank.ma").build();

        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));
        when(utilisateurRepository.findByEmail("new@cfgbank.ma")).thenReturn(Optional.of(existingUserWithEmail));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
                utilisateurService.updateUtilisateur(1L, updateRequest)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("L'email est déjà utilisé", exception.getReason());
    }

    @Test
    void testUpdateUtilisateur_DataIntegrityViolation() {
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));
        when(utilisateurRepository.findByEmail("new@cfgbank.ma")).thenReturn(Optional.empty());
        when(utilisateurRepository.save(any(Utilisateur.class))).thenThrow(new DataIntegrityViolationException("DB Error"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
                utilisateurService.updateUtilisateur(1L, updateRequest)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Erreur d'intégrité des données"));
    }

    @Test
    void testDeleteUtilisateur_Success() {
        when(utilisateurRepository.existsById(1L)).thenReturn(true);
        doNothing().when(utilisateurRepository).deleteById(1L);

        assertDoesNotThrow(() -> utilisateurService.deleteUtilisateur(1L));

        verify(utilisateurRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteUtilisateur_NotFound() {
        when(utilisateurRepository.existsById(1L)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
                utilisateurService.deleteUtilisateur(1L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void testDeleteUtilisateur_DataIntegrityViolation() {
        when(utilisateurRepository.existsById(1L)).thenReturn(true);
        doThrow(new DataIntegrityViolationException("Constraint violation")).when(utilisateurRepository).deleteById(1L);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
                utilisateurService.deleteUtilisateur(1L)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Impossible de supprimer cet utilisateur"));
    }

    @Test
    void testToggleStatus_Success() {
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(invocation -> invocation.getArgument(0));

        boolean initialStatus = utilisateur.getActif();

        UtilisateurResponse response = utilisateurService.toggleStatus(1L);

        assertNotNull(response);
        assertNotEquals(initialStatus, response.getActif());

        verify(utilisateurRepository, times(1)).save(any(Utilisateur.class));
    }

    @Test
    void testToggleStatus_NotFound() {
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
                utilisateurService.toggleStatus(1L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
}
