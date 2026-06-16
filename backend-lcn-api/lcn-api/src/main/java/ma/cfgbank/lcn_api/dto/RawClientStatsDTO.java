package ma.cfgbank.lcn_api.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class RawClientStatsDTO {
    private String identifiantPrincipal;
    private String raisonSociale;
    private String nom;
    private String prenom;
    private String typeClient;
    private Long totalIncidents;
    private BigDecimal montantTotal;
}
