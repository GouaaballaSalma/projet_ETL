package ma.cfgbank.lcn_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "LCN_API_CLIENTS")
public class ApiClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String clientName;

    @Column(nullable = false, length = 255)
    private String hashedApiKey;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}
