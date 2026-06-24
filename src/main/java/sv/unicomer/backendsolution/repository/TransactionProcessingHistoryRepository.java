package sv.unicomer.backendsolution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv.unicomer.backendsolution.entity.TransactionProcessingHistory;

import java.util.List;
import java.util.Optional;
@Repository
public interface TransactionProcessingHistoryRepository extends JpaRepository<TransactionProcessingHistory, Integer> {

    Optional<TransactionProcessingHistory> findByTxnInId(String txnInId);

    List<TransactionProcessingHistory> findListByTxnInId(String txnInId);
}
