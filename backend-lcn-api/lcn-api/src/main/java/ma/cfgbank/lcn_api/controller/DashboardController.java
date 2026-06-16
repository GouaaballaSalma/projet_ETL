package ma.cfgbank.lcn_api.controller;

import lombok.RequiredArgsConstructor;
import ma.cfgbank.lcn_api.dto.ClientRiskScoreDTO;
import ma.cfgbank.lcn_api.dto.DashboardStatsDTO;
import ma.cfgbank.lcn_api.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getStats() {
        return ResponseEntity.ok(dashboardService.getGlobalStats());
    }

    @GetMapping("/risk-scoring")
    public ResponseEntity<List<ClientRiskScoreDTO>> getRiskScoring() {
        return ResponseEntity.ok(dashboardService.getRiskScoring());
    }
}
