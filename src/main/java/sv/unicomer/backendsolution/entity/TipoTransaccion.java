package sv.unicomer.backendsolution.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class TipoTransaccion {

    @Id
    @SequenceGenerator(name = "txntipo_id_seq", sequenceName = "txntipo_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "txntipo_id_seq")
    private Integer id;

    private String txnTipoNombre;

    public TipoTransaccion(String txnTipoNombre) {
        this.txnTipoNombre = txnTipoNombre;
    }
}
