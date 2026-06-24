package sv.unicomer.backendsolution.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class TransactionType {

    @Id
    @SequenceGenerator(name = "txntype_id_seq", sequenceName = "txntype_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "txntype_id_seq")
    private Integer id;

    private String txnTypeName;

    public TransactionType(String txnTypeName) {
        this.txnTypeName = txnTypeName;
    }
}
