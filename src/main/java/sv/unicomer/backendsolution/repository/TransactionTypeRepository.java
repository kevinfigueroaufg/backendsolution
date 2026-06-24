package sv.unicomer.backendsolution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv.unicomer.backendsolution.entity.TransactionType;

import java.util.Optional;

@Repository
public interface TransactionTypeRepository extends JpaRepository<TransactionType, Integer> {

    Optional<TransactionType> findByTxnTypeName(String txnTypeName);
}
