package ma.cfgbank.lcn_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ma.cfgbank.lcn_api.model.RoleEnum;

@Data
public class CreateUtilisateurRequest {
    
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    @NotBlank(message = "Le nom complet est obligatoire")
    private String nomComplet;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String motDePasse;

    @NotNull(message = "Le rôle est obligatoire")
    private RoleEnum role;
}
