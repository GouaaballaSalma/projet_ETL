package ma.cfgbank.lcn_api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import ma.cfgbank.lcn_api.service.ApiClientService;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final ApiClientService apiClientService;
    private static final String API_KEY_HEADER = "X-API-KEY";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String rawApiKey = request.getHeader(API_KEY_HEADER);

        // Si le header n'est pas présent, on passe au filtre suivant (ex: JwtAuthenticationFilter)
        if (rawApiKey == null || rawApiKey.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        // Si l'utilisateur n'est pas déjà authentifié dans le contexte
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            String clientName = apiClientService.validateApiKey(rawApiKey);

            if (clientName != null) {
                // Création du token d'authentification pour un API Client
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        clientName,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_API_CLIENT"))
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Injection dans le contexte de sécurité
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
