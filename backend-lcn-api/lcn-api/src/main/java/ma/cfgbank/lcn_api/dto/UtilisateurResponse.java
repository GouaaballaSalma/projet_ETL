package ma.cfgbank.lcn_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.cfgbank.lcn_api.model.RoleEnum;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurResponse {
    private Long id;
    private String email;
    private String nomComplet;
    private RoleEnum role;
}
