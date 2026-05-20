package ma.cfgbank.lcn_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.cfgbank.lcn_api.model.RoleEnum;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUtilisateurRequest {

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Le format de l'email est invalide")
    private String email;

    @NotBlank(message = "Le nom complet est obligatoire")
    private String nomComplet;

    @NotNull(message = "Le rôle est obligatoire")
    private RoleEnum role;
}
