package ma.cfgbank.lcn_api.service;

import lombok.RequiredArgsConstructor;
import ma.cfgbank.lcn_api.dto.UpdateUtilisateurRequest;
import ma.cfgbank.lcn_api.dto.UtilisateurResponse;
import ma.cfgbank.lcn_api.entity.Utilisateur;
import ma.cfgbank.lcn_api.repository.UtilisateurRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurResponse updateUtilisateur(Long id, UpdateUtilisateurRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        // Vérifier si le nouvel email est déjà pris par un autre utilisateur
        if (!utilisateur.getEmail().equals(request.getEmail())) {
            if (utilisateurRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "L'email est déjà utilisé");
            }
            utilisateur.setEmail(request.getEmail());
        }

        utilisateur.setNomComplet(request.getNomComplet());
        utilisateur.setRole(request.getRole());

        try {
            Utilisateur savedUser = utilisateurRepository.save(utilisateur);

            return UtilisateurResponse.builder()
                    .id(savedUser.getId())
                    .email(savedUser.getEmail())
                    .nomComplet(savedUser.getNomComplet())
                    .role(savedUser.getRole())
                    .actif(savedUser.getActif())
                    .build();
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erreur d'intégrité des données : vérifiez les valeurs envoyées (ex: rôle invalide pour la base de données).", e);
        }
    }

    public void deleteUtilisateur(Long id) {
        if (!utilisateurRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable");
        }
        try {
            utilisateurRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Impossible de supprimer cet utilisateur car il est lié à des incidents LCN. Veuillez plutôt le désactiver.", e);
        }
    }

    @Transactional
    public UtilisateurResponse toggleStatus(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
        
        utilisateur.setActif(!Boolean.TRUE.equals(utilisateur.getActif()));
        Utilisateur savedUser = utilisateurRepository.save(utilisateur);
        
        return UtilisateurResponse.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .nomComplet(savedUser.getNomComplet())
                .role(savedUser.getRole())
                .actif(savedUser.getActif())
                .build();
    }
}
