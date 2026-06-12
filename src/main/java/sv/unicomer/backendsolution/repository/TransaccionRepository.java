package sv.unicomer.backendsolution.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sv.unicomer.backendsolution.entity.Transaccion;

import java.util.Date;
import java.util.Optional;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Integer> {

    Optional<Transaccion> findByTxnInId(String txnInId);

    @Query("""
        SELECT t
        FROM Transaccion t
        WHERE (:estado IS NULL OR t.txnEstado LIKE CONCAT('%', :estado, '%'))
        AND (:sistemaOrigen IS NULL OR t.txnSistema LIKE CONCAT('%', :sistemaOrigen, '%'))
        AND (:tipo IS NULL OR t.txnTipo LIKE CONCAT('%', :tipo, '%'))
        AND (:fechaInicio IS NULL OR t.txnDate >= :fechaInicio)
        AND (:fechaFin IS NULL OR t.txnDate <= :fechaFin)
    """)
    Page<Transaccion> buscarTransacciones(
            @Param("estado") String estado,
            @Param("tipo") String tipo,
            @Param("sistemaOrigen") String sistemaOrigen,
            @Param("fechaInicio") Date fechaInicio,
            @Param("fechaFin") Date fechaFin,
            Pageable pageable);
}
