package ma.cfgbank.lcn_api.service;

import ma.cfgbank.lcn_api.dto.LcnSynthDTO;
import ma.cfgbank.lcn_api.entity.LcnSynth;
import org.springframework.stereotype.Component;

@Component
public class LcnSynthMapper {

    public LcnSynthDTO toDTO(LcnSynth entity) {
        if (entity == null) {
            return null;
        }

        return LcnSynthDTO.builder()
                .refImpaye(entity.getId() != null ? entity.getId().getRefImpaye() : null)
                .lot(entity.getId() != null ? entity.getId().getLot() : null)
                .refClient(entity.getRefClient())
                .typeClient(entity.getTypeClient())
                .nom(entity.getNom())
                .prenom(entity.getPrenom())
                .typeIdentifiant(entity.getTypeIdentifiant())
                .identifiantPrincipal(entity.getIdentifiantPrincipal())
                .dateNaissance(entity.getDateNaissance())
                .raisonSociale(entity.getRaisonSociale())
                .rc(entity.getRc())
                .identifiantFiscal(entity.getIdentifiantFiscal())
                .codeBanque(entity.getCodeBanque())
                .numLcn(entity.getNumLcn())
                .montant(entity.getMontant())
                .devise(entity.getDevise())
                .dateEmission(entity.getDateEmission())
                .dateEcheance(entity.getDateEcheance())
                .dateConstat(entity.getDateConstat())
                .insuffisance(entity.getInsuffisance())
                .codeStatut(entity.getCodeStatut())
                .statut(entity.getStatut())
                .dateStatut(entity.getDateStatut())
                .rib(entity.getRib())
                .dateArrete(entity.getDateArrete())
                .dateCharge(entity.getDateCharge())
                .build();
    }
}
