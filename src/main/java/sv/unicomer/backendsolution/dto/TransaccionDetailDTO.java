package sv.unicomer.backendsolution.dto;

import lombok.Getter;
import lombok.Setter;
import sv.unicomer.backendsolution.entity.TransaccionProcessingHistory;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class TransaccionDetailDTO {
    private String txnInId;

    private String txnTipo;

    private String txnEstado;

    private String txnSistema;

    private String txnDetalle;

    private Date txnDate;

    private Double txnTotal;

    private List<TransaccionProcessingHistory> txnProcessingHistory;
}
