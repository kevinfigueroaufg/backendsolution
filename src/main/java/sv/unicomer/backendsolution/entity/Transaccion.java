package sv.unicomer.backendsolution.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class Transaccion {

    @Id
    @SequenceGenerator(name = "txn_id_seq", sequenceName = "txn_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "txn_id_seq")
    private Integer id;

    private String txnInId;

    private String txnTipo;

    private String txnEstado;

    private String txnSistema;

    private String txnDetalle;

    private Date txnDate;

    private Double txnTotal;

}
