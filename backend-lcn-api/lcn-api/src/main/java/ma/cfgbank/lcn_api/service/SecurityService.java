package ma.cfgbank.lcn_api.service;

import lombok.RequiredArgsConstructor;
import ma.cfgbank.lcn_api.entity.LcnSynth;
import ma.cfgbank.lcn_api.repository.LcnSynthRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("securityService")
@RequiredArgsConstructor
public class SecurityService {

    private final LcnSynthRepository lcnSynthRepository;

    public boolean canManageLcn(Authentication authentication, String refImpaye) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Optional<LcnSynth> optionalLcn = lcnSynthRepository.findFirstByIdRefImpaye(refImpaye);
        
        if (optionalLcn.isEmpty()) {
            return false; // Ou true selon la logique métier si on veut que le controlleur renvoie 404 plutôt que 403, mais false est plus sûr.
        }

        LcnSynth lcn = optionalLcn.get();
        String currentUserEmail = authentication.getName();

        // L'utilisateur connecté est-il le créateur de ce LCN ?
        return currentUserEmail.equals(lcn.getCreatedBy());
    }
}
