package ma.cfgbank.lcn_api.service;

import ma.cfgbank.lcn_api.entity.LcnSynth;
import ma.cfgbank.lcn_api.entity.LcnSynthId;
import ma.cfgbank.lcn_api.repository.LcnSynthRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {

    @Mock
    private LcnSynthRepository lcnSynthRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private SecurityService securityService;

    @Test
    void canManageLcn_WithNullAuthentication_ReturnsFalse() {
        // When
        boolean result = securityService.canManageLcn(null, "REF_123");

        // Then
        assertFalse(result);
        verifyNoInteractions(lcnSynthRepository);
    }

    @Test
    void canManageLcn_WithUnauthenticatedUser_ReturnsFalse() {
        // Given
        when(authentication.isAuthenticated()).thenReturn(false);

        // When
        boolean result = securityService.canManageLcn(authentication, "REF_123");

        // Then
        assertFalse(result);
        verifyNoInteractions(lcnSynthRepository);
    }

    @Test
    void canManageLcn_WithLcnNotFound_ReturnsFalse() {
        // Given
        when(authentication.isAuthenticated()).thenReturn(true);
        when(lcnSynthRepository.findFirstByIdRefImpaye("REF_123")).thenReturn(Optional.empty());

        // When
        boolean result = securityService.canManageLcn(authentication, "REF_123");

        // Then
        assertFalse(result);
        verify(lcnSynthRepository).findFirstByIdRefImpaye("REF_123");
    }

    @Test
    void canManageLcn_WithMatchingCreator_ReturnsTrue() {
        // Given
        String currentUser = "user@test.com";
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(currentUser);
        
        LcnSynth lcn = LcnSynth.builder()
                .id(LcnSynthId.builder().refImpaye("REF_123").build())
                .createdBy(currentUser)
                .build();
                
        when(lcnSynthRepository.findFirstByIdRefImpaye("REF_123")).thenReturn(Optional.of(lcn));

        // When
        boolean result = securityService.canManageLcn(authentication, "REF_123");

        // Then
        assertTrue(result);
    }

    @Test
    void canManageLcn_WithDifferentCreator_ReturnsFalse() {
        // Given
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("otheruser@test.com");
        
        LcnSynth lcn = LcnSynth.builder()
                .id(LcnSynthId.builder().refImpaye("REF_123").build())
                .createdBy("user@test.com")
                .build();
                
        when(lcnSynthRepository.findFirstByIdRefImpaye("REF_123")).thenReturn(Optional.of(lcn));

        // When
        boolean result = securityService.canManageLcn(authentication, "REF_123");

        // Then
        assertFalse(result);
    }
}
