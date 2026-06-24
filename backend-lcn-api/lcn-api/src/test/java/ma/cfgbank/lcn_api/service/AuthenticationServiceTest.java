package ma.cfgbank.lcn_api.service;

import ma.cfgbank.lcn_api.dto.AuthenticationRequest;
import ma.cfgbank.lcn_api.dto.AuthenticationResponse;
import ma.cfgbank.lcn_api.entity.Utilisateur;
import ma.cfgbank.lcn_api.repository.UtilisateurRepository;
import ma.cfgbank.lcn_api.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private AuthenticationRequest request;
    private Utilisateur utilisateur;

    @BeforeEach
    void setUp() {
        request = AuthenticationRequest.builder()
                .email("test@cfgbank.ma")
                .motDePasse("password123")
                .build();

        utilisateur = Utilisateur.builder()
                .id(1L)
                .email("test@cfgbank.ma")
                .nomComplet("Test User")
                .actif(true)
                .build();
    }

    @Test
    void testLogin_Success() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMotDePasse()));
        when(utilisateurRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(utilisateur));
        when(jwtService.generateToken(utilisateur)).thenReturn("mocked-jwt-token");

        AuthenticationResponse response = authenticationService.login(request);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).generateToken(utilisateur);
    }

    @Test
    void testLogin_UserNotFound() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMotDePasse()));
        when(utilisateurRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authenticationService.login(request));
    }

    @Test
    void testLogin_BadCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authenticationService.login(request));

        verify(utilisateurRepository, never()).findByEmail(anyString());
        verify(jwtService, never()).generateToken(any(Utilisateur.class));
    }

    @Test
    void testLogin_UserDisabled() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new DisabledException("User is disabled"));

        assertThrows(DisabledException.class, () -> authenticationService.login(request));

        verify(utilisateurRepository, never()).findByEmail(anyString());
        verify(jwtService, never()).generateToken(any(Utilisateur.class));
    }
}
