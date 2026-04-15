package ma.cfgbank.lcn_api.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.cfgbank.lcn_api.model.TypeClient;
import ma.cfgbank.lcn_api.model.TypeIdentifiantPM;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLcnSynthRequest {

    @NotBlank(message = "La référence client est obligatoire")
    @Size(max = 12, message = "La référence client ne doit pas dépasser 12 caractères")
    private String refClient;

    @NotNull(message = "Le type de client est obligatoire")
    private TypeClient typeClient;

    @Size(max = 50, message = "Le nom ne peut excéder 50 caractères")
    private String nom;

    @Size(max = 50, message = "Le prénom ne peut excéder 50 caractères")
    private String prenom;

    @Size(max = 1, message = "Le type d'identifiant (PP) ne peut excéder 1 caractère")
    private String typeIdentifiant;

    @Size(max = 20, message = "L'identifiant principal ne peut excéder 20 caractères")
    private String identifiantPrincipal;

    private LocalDate dateNaissance;

    @Size(max = 120, message = "La raison sociale ne peut excéder 120 caractères")
    private String raisonSociale;

    private TypeIdentifiantPM typeIdentifiantPM; 

    @Size(max = 20, message = "Le RC ne peut excéder 20 caractères")
    private String rc;

    @Size(max = 20, message = "L'IF ne peut excéder 20 caractères")
    private String identifiantFiscal;

    @NotBlank(message = "Le code banque est obligatoire")
    @Size(max = 3, message = "Le code banque ne peut excéder 3 caractères")
    private String codeBanque;

    @NotBlank(message = "Le numéro LCN est obligatoire")
    @Size(max = 10, message = "Le numéro LCN ne peut excéder 10 caractères")
    private String numLcn;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private BigDecimal montant;

    @NotBlank(message = "La devise est obligatoire")
    @Size(max = 3, message = "La devise ne peut excéder 3 caractères")
    private String devise;

    @NotNull(message = "La date d'émission est obligatoire")
    private LocalDate dateEmission;

    @NotNull(message = "La date d'échéance est obligatoire")
    private LocalDate dateEcheance;

    @NotNull(message = "La date de constat est obligatoire")
    private LocalDate dateConstat;

    @NotNull(message = "Le montant de l'insuffisance est obligatoire")
    @Positive(message = "L'insuffisance doit être positive")
    private BigDecimal insuffisance;

    @NotBlank(message = "Le RIB est obligatoire")
    @Size(max = 24, message = "Le RIB ne peut excéder 24 caractères")
    private String rib;
}
