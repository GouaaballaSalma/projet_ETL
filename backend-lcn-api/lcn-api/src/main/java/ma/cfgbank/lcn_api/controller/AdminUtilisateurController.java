package ma.cfgbank.lcn_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.cfgbank.lcn_api.dto.CreateUtilisateurRequest;
import ma.cfgbank.lcn_api.dto.UpdateUtilisateurRequest;
import ma.cfgbank.lcn_api.dto.UtilisateurResponse;
import ma.cfgbank.lcn_api.entity.Utilisateur;
import ma.cfgbank.lcn_api.repository.UtilisateurRepository;
import ma.cfgbank.lcn_api.service.UtilisateurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminUtilisateurController {

    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurService utilisateurService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<List<UtilisateurResponse>> getAllUsers() {
        List<UtilisateurResponse> users = utilisateurRepository.findAll().stream()
                .map(user -> UtilisateurResponse.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .nomComplet(user.getNomComplet())
                        .role(user.getRole())
                        .actif(user.getActif())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<UtilisateurResponse> createUser(@Valid @RequestBody CreateUtilisateurRequest request) {
        if (utilisateurRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Utilisateur newUser = Utilisateur.builder()
                .email(request.getEmail())
                .nomComplet(request.getNomComplet())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .role(request.getRole())
                .build();

        try {
            Utilisateur savedUser = utilisateurRepository.save(newUser);

            UtilisateurResponse response = UtilisateurResponse.builder()
                    .id(savedUser.getId())
                    .email(savedUser.getEmail())
                    .nomComplet(savedUser.getNomComplet())
                    .role(savedUser.getRole())
                    .actif(savedUser.getActif())
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erreur d'intégrité des données : vérifiez les valeurs envoyées (ex: rôle invalide pour la base de données).", e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UtilisateurResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUtilisateurRequest request) {
        UtilisateurResponse updatedUser = utilisateurService.updateUtilisateur(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        utilisateurService.deleteUtilisateur(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<UtilisateurResponse> toggleUserStatus(@PathVariable Long id) {
        UtilisateurResponse updatedUser = utilisateurService.toggleStatus(id);
        return ResponseEntity.ok(updatedUser);
    }
}
