package sv.unicomer.backendsolution.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sv.unicomer.backendsolution.entity.Transaction;

import java.util.Date;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    Optional<Transaction> findByTxnInId(String txnInId);

    @Query("""
        SELECT t
        FROM Transaction t
        WHERE (:status IS NULL OR t.txnStatus LIKE CONCAT('%', :status, '%'))
        AND (:originSystem IS NULL OR t.txnSystem LIKE CONCAT('%', :originSystem, '%'))
        AND (:type IS NULL OR t.txnType LIKE CONCAT('%', :type, '%'))
        AND (:startDate IS NULL OR t.txnDate >= :startDate)
        AND (:endDate IS NULL OR t.txnDate <= :endDate)
    """)
    Page<Transaction> searchTransactions(
            @Param("status") String status,
            @Param("type") String type,
            @Param("originSystem") String originSystem,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            Pageable pageable);
}
