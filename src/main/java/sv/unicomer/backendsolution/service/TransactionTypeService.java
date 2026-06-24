package sv.unicomer.backendsolution.service;

import org.springframework.stereotype.Service;
import sv.unicomer.backendsolution.entity.TransactionType;
import sv.unicomer.backendsolution.repository.TransactionTypeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionTypeService {
    private final TransactionTypeRepository transactionTypeRepository;

    public TransactionTypeService(TransactionTypeRepository transactionTypeRepository) {
        this.transactionTypeRepository = transactionTypeRepository;
        loadTypeTxn();
    }

    public void loadTypeTxn() {
        ArrayList<TransactionType> avaliableTypeTransaction= new ArrayList<TransactionType>();
        avaliableTypeTransaction.add(new TransactionType("PAGO COLECTURIA"));
        avaliableTypeTransaction.add(new TransactionType("PAGO SERVICIO BASICO"));
        avaliableTypeTransaction.add(new TransactionType("PAGO TELEFONIA"));
        avaliableTypeTransaction.add(new TransactionType("CONSULTA DE SALDO"));
        avaliableTypeTransaction.add(new TransactionType("RECARGA TELEFONICA"));
        for (TransactionType txnType : avaliableTypeTransaction) {
            transactionTypeRepository.save(txnType);
        }
    }

    public List<TransactionType> getAllTransactionTypes() {
        return transactionTypeRepository.findAll();
    }

    public Optional<TransactionType> getTransactionTypeByName(String name) {
        return transactionTypeRepository.findByTxnTypeName(name);
    }
}
