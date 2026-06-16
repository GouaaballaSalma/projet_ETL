package ma.cfgbank.lcn_api.dto;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ClientRiskScoreDTO {
    private String nomClient;
    private String typeClient;
    private Long totalIncidents;
    private BigDecimal montantTotal;
    private Double riskScore;
    private String niveauRisque;
}
