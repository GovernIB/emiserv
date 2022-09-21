package es.caib.emiserv.client.comu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultesOkError {

    @Builder.Default private Long ok = 0L;
    @Builder.Default private Long error = 0L;

    public void addOk(Long num) {
        this.ok += num;
    }

    public void addError(Long num) {
        this.error += num;
    }

}
