package sv.unicomer.backendsolution.service;

import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sv.unicomer.backendsolution.dto.TransactionDetailDTO;
import sv.unicomer.backendsolution.dto.TransactionFilterDTO;
import sv.unicomer.backendsolution.dto.TransactionInDTO;
import sv.unicomer.backendsolution.entity.Transaction;
import sv.unicomer.backendsolution.entity.TransactionProcessingHistory;
import sv.unicomer.backendsolution.entity.TransactionType;
import sv.unicomer.backendsolution.exceptions.CustomException;
import sv.unicomer.backendsolution.repository.TransactionProcessingHistoryRepository;
import sv.unicomer.backendsolution.repository.TransactionRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static sv.unicomer.backendsolution.util.MessageConstants.*;

@Service
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionProcessingHistoryRepository transactionProcessingHistoryRepository;
    private final TransactionTypeService transactionTypeService;

    public TransactionService(TransactionRepository transactionRepository, TransactionProcessingHistoryRepository transactionProcessingHistoryRepository, TransactionTypeService transactionTypeService) {
        this.transactionRepository = transactionRepository;
        this.transactionProcessingHistoryRepository = transactionProcessingHistoryRepository;
        this.transactionTypeService = transactionTypeService;
    }

    @Transactional
    public Transaction saveTxn(TransactionInDTO transactionDTO) {
        try {
            Optional<TransactionType> type =
                    transactionTypeService.getTransactionTypeByName(transactionDTO.getTxnType());
            Optional<Transaction> txnInId =
                    getTransactionByTxnInId(transactionDTO.getTxnInId());
            if (txnInId.isEmpty()) {
                if (type.isPresent()) {
                    if (transactionDTO.getTxnDetail() != null) {
                        if (!transactionDTO.getTxnDetail().equals("")) {
                            Transaction txn = new Transaction();
                            txn.setId(UUID.randomUUID().toString().replace("-", ""));
                            txn.setTxnInId(transactionDTO.getTxnInId());
                            txn.setTxnType(transactionDTO.getTxnType());
                            txn.setTxnStatus(PROCESS_RECEIVED);
                            txn.setTxnSystem(transactionDTO.getTxnSystem());
                            txn.setTxnDetail(transactionDTO.getTxnDetail());
                            txn.setTxnDate(new Date());
                            txn.setTxnTotal(transactionDTO.getTxnTotal());
                            log.info("Objeto guardado, procesando guardado txn");
                            transactionRepository.save(txn);
                            log.info("Comenzando procesamiento de transaccion recibida...");
                            return processTxn(txn.getTxnInId());
                        }else {
                            log.error("Descripcion de transaccion no puede ser vacia");
                            throw new ResponseStatusException(
                                    HttpStatus.BAD_REQUEST,"Descripcion de transaccion no puede ser vacia");
                        }
                    }else {
                        log.error("Descripcion de transaccion no puede ser vacia");
                        throw new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,"Descripcion de transaccion no puede ser vacia");
                    }
                }else {
                    log.error("Tipo de transaccion no reconocida");
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Tipo de transacción no reconocida");
                }
            }else {
                log.error("ID transaccion externa duplicado");
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,"ID transaccion externa duplicado");
            }
        }catch (PersistenceException ex) {
            // Manejar errores de la base de datos, como problemas de conexión o constraints
            log.error("Error al guardar transaccion en la base de datos. ", ex);
            throw new CustomException("Error al guardar transaccion en la base de datos. ", ex);
        } catch (Exception ex) {
            // Manejar cualquier otra excepción
            log.error("Error inesperado al guardar la transaccion. ", ex);
            throw new CustomException("Error inesperado al guardar la transaccion. ", ex);
        }
    }

    public Optional<Transaction> getTransactionByTxnInId(String id) {
        return transactionRepository.findByTxnInId(id);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public List<TransactionProcessingHistory> getAllTransactionsProcessed() {
        return transactionProcessingHistoryRepository.findAll();
    }

    @Transactional
    public Transaction processTxn(String txnInId) {
        try {
            Optional<Transaction> txnIn =
                    getTransactionByTxnInId(txnInId);
            if (txnIn.isPresent()) {
                Transaction transactionBD = txnIn.get();
                if (transactionBD.getTxnStatus().equals(PROCESS_PROCESSED)) {
                    log.info("Estado {} no valido para procesar transaccion {}, no se proceso transaccion",transactionBD.getTxnStatus(), transactionBD.getTxnInId());
                    return null;
                }
                log.info("Transaccion encontrada, cambiando a estado {}",PROCESS_PROCESSING);
                transactionBD.setTxnStatus(PROCESS_PROCESSING);
                transactionRepository.saveAndFlush(transactionBD);
                log.info("Estado actualizado");
                try{
                    TransactionProcessingHistory txnProcess=new TransactionProcessingHistory();
                    log.info("Registrando historico procesamiento");
                    txnProcess.setTxnInId(txnInId);
                    txnProcess.setTxnProcessStatus(PROCESS_PROCESSED);
                    txnProcess.setTxnProcessDetail("Procesado");
                    txnProcess.setTxnProcessDate(new Date());
                    transactionProcessingHistoryRepository.save(txnProcess);
                    log.info("Registrando historico guardado");
                    log.info("Cambiando a estado {} txn {}",PROCESS_PROCESSED, txnInId);
                    Optional<Transaction> txnInxUpdate =
                            getTransactionByTxnInId(txnInId);
                    Transaction transaccionBDxUpdate = txnInxUpdate.get();
                    transaccionBDxUpdate.setTxnStatus(PROCESS_PROCESSED);
                    log.info("Estado actualizado, transaccion {}, estado {}",txnInId,PROCESS_PROCESSED);
                    return transactionRepository.saveAndFlush(transaccionBDxUpdate);
                }catch (PersistenceException ex) {
                    // Manejar errores de la base de datos, como problemas de conexión o constraints
                    log.error("Error al guardar transaccion en la base de datos. ", ex);
                    TransactionProcessingHistory txnProcessFail=new TransactionProcessingHistory();
                    log.info("Registrando historico procesamiento fallido");
                    txnProcessFail.setTxnInId(txnInId);
                    txnProcessFail.setTxnProcessStatus(PROCESS_RETRY_PENDING);
                    txnProcessFail.setTxnProcessDetail("PersistenceException - Error BD");
                    txnProcessFail.setTxnProcessDate(new Date());
                    transactionProcessingHistoryRepository.save(txnProcessFail);
                    log.info("Registrando historico guardado");
                    log.info("Cambiando a estado {} txn {}",PROCESS_FAILED, txnInId);
                    Optional<Transaction> txnInxFailed =
                            getTransactionByTxnInId(txnInId);
                    Transaction transactionBDxFailed = txnInxFailed.get();
                    transactionBDxFailed.setTxnStatus(PROCESS_FAILED);
                    log.info("Estado actualizado, transaccion {}, estado {}",txnInId,PROCESS_FAILED);
                    transactionRepository.saveAndFlush(transactionBDxFailed);
                    return null;
                } catch (Exception ex) {
                    // Manejar cualquier otra excepción
                    log.error("Error inesperado al guardar la transaccion. ", ex);
                    TransactionProcessingHistory txnProcessFail=new TransactionProcessingHistory();
                    log.info("Registrando historico procesamiento fallido");
                    txnProcessFail.setTxnInId(txnInId);
                    txnProcessFail.setTxnProcessStatus(PROCESS_RETRY_PENDING);
                    txnProcessFail.setTxnProcessDetail("PersistenceException - Error BD");
                    txnProcessFail.setTxnProcessDate(new Date());
                    transactionProcessingHistoryRepository.save(txnProcessFail);
                    log.info("Registrando historico guardado");
                    log.info("Cambiando a estado {} txn {}",PROCESS_FAILED, txnInId);
                    Optional<Transaction> txnInxFailed =
                            getTransactionByTxnInId(txnInId);
                    Transaction transaccionBDxFailed = txnInxFailed.get();
                    transaccionBDxFailed.setTxnStatus(PROCESS_FAILED);
                    log.info("Estado actualizado, transaccion {}, estado {}",txnInId,PROCESS_FAILED);
                    transactionRepository.saveAndFlush(transaccionBDxFailed);
                    return null;
                }
            }else {
                log.error("Transaccion no encontrada, no se proceso");
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,"Transaccion no encontrada, no se proceso");
            }
        }catch (PersistenceException ex) {
            // Manejar errores de la base de datos, como problemas de conexión o constraints
            log.error("Error al guardar transaccion en la base de datos. ", ex);
            throw new CustomException("Error al guardar transaccion en la base de datos. ", ex);
        } catch (Exception ex) {
            // Manejar cualquier otra excepción
            log.error("Error inesperado al guardar la transaccion. ", ex);
            throw new CustomException("Error inesperado al guardar la transaccion. ", ex);
        }
    }

    @Transactional
    public Transaction processTxnFail(String txnInId) {
        try {
            TransactionProcessingHistory txnProcessFail=new TransactionProcessingHistory();
            log.info("Registrando historico procesamiento fallido");
            txnProcessFail.setTxnInId(txnInId);
            txnProcessFail.setTxnProcessStatus(PROCESS_RETRY_PENDING);
            txnProcessFail.setTxnProcessDetail("PersistenceException - Error BD");
            txnProcessFail.setTxnProcessDate(new Date());
            transactionProcessingHistoryRepository.save(txnProcessFail);
            log.info("Registrando historico guardado");
            log.info("Cambiando a estado {} txn {}",PROCESS_FAILED, txnInId);
            Optional<Transaction> txnInxFailed =
                    getTransactionByTxnInId(txnInId);
            Transaction transactionBDxFailed = txnInxFailed.get();
            transactionBDxFailed.setTxnStatus(PROCESS_FAILED);
            log.info("Estado actualizado, transaccion {}, estado {}",txnInId,PROCESS_FAILED);
            return transactionRepository.saveAndFlush(transactionBDxFailed);
        }catch (PersistenceException ex) {
            // Manejar errores de la base de datos, como problemas de conexión o constraints
            log.error("Error al guardar transaccion en la base de datos. ", ex);
            throw new CustomException("Error al guardar transaccion en la base de datos. ", ex);
        } catch (Exception ex) {
            // Manejar cualquier otra excepción
            log.error("Error inesperado al guardar la transaccion. ", ex);
            throw new CustomException("Error inesperado al guardar la transaccion. ", ex);
        }
    }

    public Page<Transaction> searchTransactions(
            TransactionFilterDTO filtro) {

        Pageable pageable = PageRequest.of(
                filtro.getPage(),
                filtro.getSize());

        Date fechaInicio = null;
        Date fechaFin = null;

        if (filtro.getStartDate() != null) {
            fechaInicio = java.sql.Date.valueOf(filtro.getStartDate());
        }

        if (filtro.getEndDate() != null) {
            fechaFin = java.sql.Timestamp.valueOf(
                    filtro.getEndDate().atTime(23, 59, 59)
            );
        }

        return transactionRepository.searchTransactions(
                filtro.getStatus(),
                filtro.getType(),
                filtro.getOriginSystem(),
                fechaInicio,
                fechaFin,
                pageable);
    }

    public Optional<TransactionDetailDTO> getTransactionDetailByTxnInId(String id) {
        Optional<Transaction> txnIn =
                getTransactionByTxnInId(id);
        if (txnIn.isPresent()){
            List<TransactionProcessingHistory> txnInProcess =
                    transactionProcessingHistoryRepository.findListByTxnInId(id);
            TransactionDetailDTO detailDTO = new TransactionDetailDTO();
            detailDTO.setTxnInId(txnIn.get().getTxnInId());
            detailDTO.setTxnType(txnIn.get().getTxnType());
            detailDTO.setTxnStatus(txnIn.get().getTxnStatus());
            detailDTO.setTxnDate(txnIn.get().getTxnDate());
            detailDTO.setTxnDetail(txnIn.get().getTxnDetail());
            detailDTO.setTxnSystem(txnIn.get().getTxnSystem());
            detailDTO.setTxnTotal(txnIn.get().getTxnTotal());
            if (!txnInProcess.isEmpty()){
                detailDTO.setTxnProcessingHistory(txnInProcess);
            }
            return Optional.of(detailDTO);
        }else {
            return Optional.empty();
        }

    }
}
