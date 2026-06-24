package sv.unicomer.backendsolution.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class TransactionProcessingHistory {
    @Id
    @SequenceGenerator(name = "txnProcess_id_seq", sequenceName = "txnProcess_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "txnProcess_id_seq")
    private Integer id;

    private String txnInId;

    private String txnProcessStatus;

    private String txnProcessDetail;

    private Date txnProcessDate;
}
