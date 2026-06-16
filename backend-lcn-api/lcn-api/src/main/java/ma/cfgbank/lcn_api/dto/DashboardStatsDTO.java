package ma.cfgbank.lcn_api.dto;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class DashboardStatsDTO {
    private Long totalIncidents;
    private BigDecimal montantGlobal;
    private Map<String, Long> repartitionTypeClient;
}
