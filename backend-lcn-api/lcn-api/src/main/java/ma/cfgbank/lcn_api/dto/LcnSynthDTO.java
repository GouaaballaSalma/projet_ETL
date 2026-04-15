package ma.cfgbank.lcn_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LcnSynthDTO {
    private String refImpaye;
    private String lot;
    private String refClient;
    private String typeClient;
    private String nom;
    private String prenom;
    private String typeIdentifiant;
    private String identifiantPrincipal;
    private LocalDate dateNaissance;
    private String raisonSociale;
    private String rc;
    private String identifiantFiscal;
    private String codeBanque;
    private String numLcn;
    private BigDecimal montant;
    private String devise;
    private LocalDate dateEmission;
    private LocalDate dateEcheance;
    private LocalDate dateConstat;
    private BigDecimal insuffisance;
    private String codeStatut;
    private String statut;
    private LocalDate dateStatut;
    private String rib;
    private LocalDate dateArrete;
    private LocalDate dateCharge;
}