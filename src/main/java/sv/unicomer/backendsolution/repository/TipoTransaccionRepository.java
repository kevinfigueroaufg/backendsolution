package sv.unicomer.backendsolution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv.unicomer.backendsolution.entity.TipoTransaccion;

import java.util.Optional;

@Repository
public interface TipoTransaccionRepository extends JpaRepository<TipoTransaccion, Integer> {

    Optional<TipoTransaccion> findByTxnTipoNombre(String txnTipoNombre);
}
