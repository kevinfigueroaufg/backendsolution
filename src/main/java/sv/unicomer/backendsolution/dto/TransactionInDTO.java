package sv.unicomer.backendsolution.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TransactionInDTO {

    private String txnInId;

    private String txnType;

    private String txnSystem;

    private String txnDetail;

    private Double txnTotal;
}
