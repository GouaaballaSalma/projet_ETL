package ma.cfgbank.lcn_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LcnSynthId implements Serializable {

    @Column(name = "REF_IMPAYE", length = 12)
    private String refImpaye;

    @Column(name = "LOT", length = 3)
    private String lot;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LcnSynthId that = (LcnSynthId) o;
        return Objects.equals(refImpaye, that.refImpaye) &&
               Objects.equals(lot, that.lot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(refImpaye, lot);
    }
}
