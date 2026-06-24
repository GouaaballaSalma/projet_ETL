package ma.cfgbank.lcn_api.service;

import ma.cfgbank.lcn_api.entity.ApiClient;
import ma.cfgbank.lcn_api.repository.ApiClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiClientServiceTest {

    @Mock
    private ApiClientRepository apiClientRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ApiClientService apiClientService;

    @Test
    void validateApiKey_WithValidKey_ReturnsClientName() {
        // Given
        String rawKey = "cfg_12345";
        ApiClient client = ApiClient.builder()
                .clientName("cfg")
                .active(true)
                .hashedApiKey("hashedKey")
                .build();
                
        when(apiClientRepository.findByClientNameAndActiveTrue("cfg")).thenReturn(Optional.of(client));
        when(passwordEncoder.matches(rawKey, "hashedKey")).thenReturn(true);

        // When
        String result = apiClientService.validateApiKey(rawKey);

        // Then
        assertEquals("cfg", result);
        verify(apiClientRepository).findByClientNameAndActiveTrue("cfg");
        verify(passwordEncoder).matches(rawKey, "hashedKey");
    }

    @Test
    void validateApiKey_WithInvalidFormat_ReturnsNull() {
        // When
        String result1 = apiClientService.validateApiKey(null);
        String result2 = apiClientService.validateApiKey("");
        String result3 = apiClientService.validateApiKey("invalidKeyFormat");

        // Then
        assertNull(result1);
        assertNull(result2);
        assertNull(result3);
        verifyNoInteractions(apiClientRepository, passwordEncoder);
    }

    @Test
    void validateApiKey_WithClientNotFound_ReturnsNull() {
        // Given
        String rawKey = "unknown_12345";
        when(apiClientRepository.findByClientNameAndActiveTrue("unknown")).thenReturn(Optional.empty());

        // When
        String result = apiClientService.validateApiKey(rawKey);

        // Then
        assertNull(result);
        verify(apiClientRepository).findByClientNameAndActiveTrue("unknown");
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void validateApiKey_WithWrongHash_ReturnsNull() {
        // Given
        String rawKey = "cfg_wrong";
        ApiClient client = ApiClient.builder()
                .clientName("cfg")
                .active(true)
                .hashedApiKey("hashedKey")
                .build();
                
        when(apiClientRepository.findByClientNameAndActiveTrue("cfg")).thenReturn(Optional.of(client));
        when(passwordEncoder.matches(rawKey, "hashedKey")).thenReturn(false);

        // When
        String result = apiClientService.validateApiKey(rawKey);

        // Then
        assertNull(result);
    }

    @Test
    void generateAndSaveApiKey_WithExistingClient_UpdatesAndReturnsKey() {
        // Given
        String clientName = "testClient";
        ApiClient existingClient = ApiClient.builder()
                .clientName(clientName)
                .active(true)
                .hashedApiKey("oldHash")
                .build();
                
        when(apiClientRepository.findByClientNameAndActiveTrue(clientName)).thenReturn(Optional.of(existingClient));
        when(passwordEncoder.encode(anyString())).thenReturn("newHash");
        when(apiClientRepository.save(any(ApiClient.class))).thenReturn(existingClient);

        // When
        String generatedKey = apiClientService.generateAndSaveApiKey(clientName);

        // Then
        assertNotNull(generatedKey);
        assertTrue(generatedKey.startsWith(clientName + "_"));
        assertEquals("newHash", existingClient.getHashedApiKey());
        verify(apiClientRepository).save(existingClient);
    }
    
    @Test
    void generateAndSaveApiKey_WithNewClient_CreatesAndReturnsKey() {
        // Given
        String clientName = "newClient";
        when(apiClientRepository.findByClientNameAndActiveTrue(clientName)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("newHash");
        when(apiClientRepository.save(any(ApiClient.class))).thenAnswer(i -> i.getArgument(0));

        // When
        String generatedKey = apiClientService.generateAndSaveApiKey(clientName);

        // Then
        assertNotNull(generatedKey);
        assertTrue(generatedKey.startsWith(clientName + "_"));
        verify(apiClientRepository).save(any(ApiClient.class));
    }
}
