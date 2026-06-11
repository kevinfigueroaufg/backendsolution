package sv.unicomer.backendsolution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv.unicomer.backendsolution.entity.TransaccionProcessingHistory;

import java.util.Optional;

@Repository
public interface TransaccionProcessingHistoryRepository extends JpaRepository<TransaccionProcessingHistory, Integer> {

    Optional<TransaccionProcessingHistory> findByTxnInId(String txnInId);
}
