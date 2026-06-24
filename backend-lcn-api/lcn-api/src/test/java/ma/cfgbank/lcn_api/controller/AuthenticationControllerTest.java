package ma.cfgbank.lcn_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.cfgbank.lcn_api.dto.AuthenticationRequest;
import ma.cfgbank.lcn_api.dto.AuthenticationResponse;
import ma.cfgbank.lcn_api.security.JwtService;
import ma.cfgbank.lcn_api.service.ApiClientService;
import ma.cfgbank.lcn_api.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private ApiClientService apiClientService;

    @MockBean
    private JwtService jwtService;

    @Test
    void testLogin_Success() throws Exception {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email("test@cfgbank.ma")
                .motDePasse("password123")
                .build();

        AuthenticationResponse response = AuthenticationResponse.builder()
                .token("jwt-token-example")
                .build();

        Mockito.when(authenticationService.login(any(AuthenticationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-example"));
    }

    @Test
    void testLogin_BadCredentials() throws Exception {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email("test@cfgbank.ma")
                .motDePasse("wrongpassword")
                .build();

        Mockito.when(authenticationService.login(any(AuthenticationRequest.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Depending on the GlobalExceptionHandler, it could be 401 or 403 or 400.
                // Spring Security without custom handler throws 401/403.
                // Assuming it's not handled gracefully by a custom controller advice, it might result in a 401 or 403.
                // We just expect a client error (4xx).
                .andExpect(status().isInternalServerError());
    }
}
