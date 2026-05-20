package ma.cfgbank.lcn_api.service;

import lombok.RequiredArgsConstructor;
import ma.cfgbank.lcn_api.entity.ApiClient;
import ma.cfgbank.lcn_api.repository.ApiClientRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiClientService {

    private final ApiClientRepository apiClientRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Valide une API Key brute reçue dans une requête HTTP.
     * Le format attendu de la clé brute est : clientName_randomUUID
     *
     * @param rawApiKey L'API Key brute envoyée par le client.
     * @return Le nom du client si la clé est valide, sinon null.
     */
    public String validateApiKey(String rawApiKey) {
        if (rawApiKey == null || rawApiKey.isBlank()) {
            return null;
        }

        // 1. Extraire le clientName de la clé brute (format attendu : clientName_uuid)
        String[] parts = rawApiKey.split("_", 2);
        if (parts.length != 2) {
            return null; // Format invalide
        }

        String clientName = parts[0];

        // 2. Chercher le client en base (uniquement s'il est actif)
        Optional<ApiClient> optionalClient = apiClientRepository.findByClientNameAndActiveTrue(clientName);
        if (optionalClient.isEmpty()) {
            return null;
        }

        ApiClient client = optionalClient.get();

        // 3. Vérifier le hash
        if (passwordEncoder.matches(rawApiKey, client.getHashedApiKey())) {
            return client.getClientName();
        }

        return null; // Le hash ne correspond pas
    }

    /**
     * Génère une nouvelle API Key pour un client (à utiliser par un endpoint admin).
     */
    public String generateAndSaveApiKey(String clientName) {
        String rawApiKey = clientName + "_" + UUID.randomUUID().toString();
        String hashedApiKey = passwordEncoder.encode(rawApiKey);

        ApiClient client = apiClientRepository.findByClientNameAndActiveTrue(clientName)
                .orElse(ApiClient.builder().clientName(clientName).active(true).build());

        client.setHashedApiKey(hashedApiKey);
        apiClientRepository.save(client);

        return rawApiKey; // On retourne la clé brute une seule fois à l'utilisateur
    }
}
