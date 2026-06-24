package sv.unicomer.backendsolution.dto;

import lombok.Getter;
import lombok.Setter;
import sv.unicomer.backendsolution.entity.TransactionProcessingHistory;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class TransactionDetailDTO {
    private String txnInId;

    private String txnType;

    private String txnStatus;

    private String txnSystem;

    private String txnDetail;

    private Date txnDate;

    private Double txnTotal;

    private List<TransactionProcessingHistory> txnProcessingHistory;
}
