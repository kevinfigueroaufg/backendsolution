package sv.unicomer.backendsolution.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TransaccionInDTO {

    private String txnInId;

    private String txnTipo;

    private String txnSistema;

    private String txnDetalle;

    private Double txnTotal;
}
