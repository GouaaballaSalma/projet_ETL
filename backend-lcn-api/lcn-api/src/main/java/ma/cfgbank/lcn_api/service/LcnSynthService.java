package ma.cfgbank.lcn_api.service;

import ma.cfgbank.lcn_api.dto.CreateLcnSynthRequest;
import ma.cfgbank.lcn_api.dto.LcnSynthDTO;
import ma.cfgbank.lcn_api.entity.LcnSynth;
import ma.cfgbank.lcn_api.entity.LcnSynthId;
import ma.cfgbank.lcn_api.exception.LcnBusinessException;
import ma.cfgbank.lcn_api.model.TypeClient;
import ma.cfgbank.lcn_api.model.TypeIdentifiantPM;
import ma.cfgbank.lcn_api.model.TypeIdentifiantPP;
import ma.cfgbank.lcn_api.repository.LcnSynthRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LcnSynthService {

    private final LcnSynthRepository repository;
    private final LcnSynthMapper mapper;
    
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public LcnSynthService(LcnSynthRepository repository, LcnSynthMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Page<LcnSynthDTO> rechercherIncidents(TypeClient typeClient, String identifiant, String nomComplet, TypeIdentifiantPM typeIdentifiantPM, TypeIdentifiantPP typeIdentifiantPP, Pageable pageable) {
        Page<LcnSynth> results;
        
        if (typeClient == TypeClient.PP) {
            boolean hasIdentifiant = identifiant != null && !identifiant.trim().isEmpty();
            boolean hasNomComplet = nomComplet != null && !nomComplet.trim().isEmpty();

            if (!hasIdentifiant && !hasNomComplet) {
                throw new LcnBusinessException("L'identifiant ou le nom complet (au moins un des deux) est obligatoire pour les Personnes Physiques (PP)");
            }
            if (hasIdentifiant && typeIdentifiantPP == null) {
                throw new LcnBusinessException("Le type d'identifiant PP (CIN, PASSEPORT ou SEJOUR) est obligatoire si l'identifiant est fourni");
            }
            
            if (hasIdentifiant && hasNomComplet) {
                results = repository.findByTypeClientAndNomCompletAndIdentifiantPrincipal(typeClient.name(), nomComplet, identifiant, pageable);
            } else if (hasNomComplet) {
                results = repository.findByTypeClientAndNomComplet(typeClient.name(), nomComplet, pageable);
            } else {
                results = repository.findByTypeClientAndIdentifiantPrincipal(typeClient.name(), identifiant, pageable);
            }
        } else if (typeClient == TypeClient.PM) {
            if (typeIdentifiantPM == null) {
                throw new LcnBusinessException("Le type d'identifiant PM (RC ou IF) est obligatoire pour les Personnes Morales (PM)");
            }
            if (identifiant == null || identifiant.trim().isEmpty()) {
                throw new LcnBusinessException("L'identifiant est obligatoire pour les Personnes Morales (PM)");
            }
            
            if (typeIdentifiantPM == TypeIdentifiantPM.RC) {
                results = repository.findByTypeClientAndRc(typeClient.name(), identifiant, pageable);
            } else {
                results = repository.findByTypeClientAndIdentifiantFiscal(typeClient.name(), identifiant, pageable);
            }
        } else {
            throw new LcnBusinessException("Type de client non supporté");
        }
        
        return results.map(mapper::toDTO);
    }

    @Transactional
    public LcnSynthDTO creerIncidentManuel(CreateLcnSynthRequest request) {
        if (request.getNom() != null) request.setNom(request.getNom().trim().toUpperCase());
        if (request.getPrenom() != null) request.setPrenom(request.getPrenom().trim().toUpperCase());
        if (request.getRaisonSociale() != null) request.setRaisonSociale(request.getRaisonSociale().trim().toUpperCase());
        if (request.getIdentifiantPrincipal() != null) request.setIdentifiantPrincipal(request.getIdentifiantPrincipal().trim().toUpperCase());
        if (request.getRc() != null) request.setRc(request.getRc().trim().toUpperCase());
        if (request.getIdentifiantFiscal() != null) request.setIdentifiantFiscal(request.getIdentifiantFiscal().trim().toUpperCase());

        if (request.getTypeClient() == TypeClient.PP) {
            if (request.getNom() == null || request.getPrenom() == null) {
                throw new LcnBusinessException("Nom et prénom sont obligatoires pour un client PP");
            }
        } else if (request.getTypeClient() == TypeClient.PM) {
            if (request.getRaisonSociale() == null) {
                throw new LcnBusinessException("Raison sociale obligatoire pour un client PM");
            }
        }
        
        String refImpaye = generateUniqueRefImpaye();
        
        LcnSynthId id = LcnSynthId.builder()
                .refImpaye(refImpaye)
                .lot("MAN")
                .build();
                
        LcnSynth entity = LcnSynth.builder()
                .id(id)
                .refClient(request.getRefClient())
                .typeClient(request.getTypeClient().name())
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .typeIdentifiant(request.getTypeIdentifiant())
                .identifiantPrincipal(request.getIdentifiantPrincipal())
                .dateNaissance(request.getDateNaissance())
                .raisonSociale(request.getRaisonSociale())
                .rc(request.getRc())
                .identifiantFiscal(request.getIdentifiantFiscal())
                .codeBanque(request.getCodeBanque())
                .numLcn(request.getNumLcn())
                .montant(request.getMontant())
                .devise(request.getDevise())
                .dateEmission(request.getDateEmission())
                .dateEcheance(request.getDateEcheance())
                .dateConstat(request.getDateConstat())
                .insuffisance(request.getInsuffisance())
                .rib(request.getRib())
                .codeStatut("0")
                .statut("Impayé")
                .dateStatut(LocalDate.now())
                .dateArrete(LocalDate.now())
                .build();
                
        LcnSynth saved = repository.save(entity);
        return mapper.toDTO(saved);
    }
    
    private String generateUniqueRefImpaye() {
        String generatedRef;
        boolean exists;
        int maxAttempts = 5;
        int attempts = 0;
        
        do {
            StringBuilder sb = new StringBuilder("MAN");
            for (int i = 0; i < 9; i++) {
                sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
            }
            generatedRef = sb.toString();
            exists = repository.existsByRefImpaye(generatedRef);
            attempts++;
            
            if(attempts >= maxAttempts && exists) {
                throw new LcnBusinessException("Impossible de générer une référence unique après plusieurs tentatives.");
            }
        } while (exists);
        
        return generatedRef;
    }
}