package sv.unicomer.backendsolution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv.unicomer.backendsolution.entity.Transaccion;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Integer> {

    //Optional<Transaccion> findByTxnInId(String txnInId);
}
