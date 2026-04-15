package ma.cfgbank.lcn_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "LCN_SYNTH", schema = "LCN_USER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LcnSynth {

    @EmbeddedId
    private LcnSynthId id;

    @Column(name = "REF_CLIENT", length = 12)
    private String refClient;

    @Column(name = "TYPE_CLIENT", length = 2)
    private String typeClient;

    @Column(name = "NOM", length = 50)
    private String nom;

    @Column(name = "PRENOM", length = 50)
    private String prenom;

    @Column(name = "TYPE_IDENTIFIANT", length = 1)
    private String typeIdentifiant;

    @Column(name = "IDENTIFIANT_PRINCIPAL", length = 20)
    private String identifiantPrincipal;

    @Column(name = "DATE_NAISSANCE")
    private LocalDate dateNaissance;

    @Column(name = "RAISON_SOCIALE", length = 120)
    private String raisonSociale;

    @Column(name = "RC", length = 20)
    private String rc;

    @Column(name = "IDENTIFIANT_FISCAL", length = 20)
    private String identifiantFiscal;

    @Column(name = "CODE_BANQUE", length = 3)
    private String codeBanque;

    @Column(name = "NUM_LCN", length = 10)
    private String numLcn;

    @Column(name = "MONTANT", precision = 15, scale = 2)
    private BigDecimal montant;

    @Column(name = "DEVISE", length = 3)
    private String devise;

    @Column(name = "DATE_EMISSION")
    private LocalDate dateEmission;

    @Column(name = "DATE_ECHEANCE")
    private LocalDate dateEcheance;

    @Column(name = "DATE_CONSTAT")
    private LocalDate dateConstat;

    @Column(name = "INSUFFISANCE", precision = 15, scale = 2)
    private BigDecimal insuffisance;

    @Column(name = "CODE_STATUT", length = 1)
    private String codeStatut;

    @Column(name = "STATUT", length = 20)
    private String statut;

    @Column(name = "DATE_STATUT")
    private LocalDate dateStatut;

    @Column(name = "RIB", length = 24)
    private String rib;

    @Column(name = "DATE_ARRETE")
    private LocalDate dateArrete;

    @Column(name = "DATE_CHARGE", insertable = false, updatable = false)
    private LocalDate dateCharge;
}