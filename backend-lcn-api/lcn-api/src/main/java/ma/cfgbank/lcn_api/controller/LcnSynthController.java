package ma.cfgbank.lcn_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ma.cfgbank.lcn_api.dto.CreateLcnSynthRequest;
import ma.cfgbank.lcn_api.dto.LcnSynthDTO;
import ma.cfgbank.lcn_api.model.TypeClient;
import ma.cfgbank.lcn_api.model.TypeIdentifiantPM;
import ma.cfgbank.lcn_api.model.TypeIdentifiantPP;
import ma.cfgbank.lcn_api.service.LcnSynthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@RestController
@RequestMapping("/api/lcn")
@Validated
@Tag(name = "Gestion LCN", description = "Endpoints pour la consultation et la création manuelle d'incidents LCN")
public class LcnSynthController {

    private final LcnSynthService service;

    public LcnSynthController(LcnSynthService service) {
        this.service = service;
    }

    @Operation(summary = "Rechercher des incidents LCN", description = "Recherche les incidents selon le type de client (PP ou PM) et son identifiant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recherche effectuée avec succès"),
            @ApiResponse(responseCode = "400", description = "Paramètres de recherche invalides ou manquants"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping("/recherche")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_BUSINESS', 'ROLE_READ_ONLY', 'ROLE_API_CLIENT')")
    public ResponseEntity<Page<LcnSynthDTO>> rechercherIncidents(
            @RequestParam TypeClient typeClient,
            @RequestParam(required = false) String identifiant,
            @RequestParam(required = false) String nomComplet,
            @RequestParam(required = false) TypeIdentifiantPM typeIdentifiantPM,
            @RequestParam(required = false) TypeIdentifiantPP typeIdentifiantPP,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
            
        if (size > 50) size = 50;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateEmission"));
            
        Page<LcnSynthDTO> results = service.rechercherIncidents(typeClient, identifiant, nomComplet, typeIdentifiantPM, typeIdentifiantPP, pageable);
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "Créer un incident LCN manuellement", description = "Ajoute un nouvel incident de paiement LCN dans le système.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Incident créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données d'entrée invalides (erreur de validation)"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_BUSINESS')")
    public ResponseEntity<LcnSynthDTO> creerIncidentManuel(@Valid @RequestBody CreateLcnSynthRequest request) {
        LcnSynthDTO created = service.creerIncidentManuel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Modifier un incident LCN manuellement", description = "Modifie un incident existant.")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or (hasAuthority('ROLE_BUSINESS') and @securityService.canManageLcn(authentication, #refImpaye))")
    public ResponseEntity<LcnSynthDTO> modifierIncidentManuel(
            @PathVariable("id") String refImpaye, 
            @Valid @RequestBody CreateLcnSynthRequest request) {
        LcnSynthDTO updated = service.modifierIncidentManuel(refImpaye, request);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Supprimer un incident LCN manuellement", description = "Supprime un incident existant.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or (hasAuthority('ROLE_BUSINESS') and @securityService.canManageLcn(authentication, #refImpaye))")
    public ResponseEntity<Void> supprimerIncidentManuel(@PathVariable("id") String refImpaye) {
        service.supprimerIncidentManuel(refImpaye);
        return ResponseEntity.noContent().build();
    }
}