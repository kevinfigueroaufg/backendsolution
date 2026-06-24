package sv.unicomer.backendsolution.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class Transaction {

    @Id
    private String id;

    private String txnInId;

    private String txnType;

    private String txnStatus;

    private String txnSystem;

    private String txnDetail;

    private Date txnDate;

    private Double txnTotal;

}
