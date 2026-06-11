package sv.unicomer.backendsolution.service;

import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sv.unicomer.backendsolution.dto.TransaccionInDTO;
import sv.unicomer.backendsolution.entity.TipoTransaccion;
import sv.unicomer.backendsolution.entity.Transaccion;
import sv.unicomer.backendsolution.entity.TransaccionProcessingHistory;
import sv.unicomer.backendsolution.exceptions.CustomException;
import sv.unicomer.backendsolution.repository.TransaccionProcessingHistoryRepository;
import sv.unicomer.backendsolution.repository.TransaccionRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static sv.unicomer.backendsolution.util.MessageConstants.*;

@Service
@Slf4j
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final TransaccionProcessingHistoryRepository transaccionProcessingHistoryRepository;
    private final TipoTransaccionService tipoTransaccionService;

    public TransaccionService(TransaccionRepository transaccionRepository, TransaccionProcessingHistoryRepository transaccionProcessingHistoryRepository, TipoTransaccionService tipoTransaccionService) {
        this.transaccionRepository = transaccionRepository;
        this.transaccionProcessingHistoryRepository = transaccionProcessingHistoryRepository;
        this.tipoTransaccionService = tipoTransaccionService;
    }

    @Transactional
    public Transaccion saveTxn(TransaccionInDTO transaccionDTO) {
        try {
            Optional<TipoTransaccion> tipo =
                    tipoTransaccionService.getTipoTransaccionByNombre(transaccionDTO.getTxnTipo());
            Optional<Transaccion> txnInId =
                    getTransaccionByTxnInId(transaccionDTO.getTxnInId());
            if (txnInId.isEmpty()) {
                if (tipo.isPresent()) {
                    if (transaccionDTO.getTxnDetalle() != null) {
                        if (!transaccionDTO.getTxnDetalle().equals("")) {
                            Transaccion txn = new Transaccion();
                            txn.setTxnInId(transaccionDTO.getTxnInId());
                            txn.setTxnTipo(transaccionDTO.getTxnTipo());
                            txn.setTxnEstado(PROCESS_RECEIVED);
                            txn.setTxnSistema(transaccionDTO.getTxnSistema());
                            txn.setTxnDetalle(transaccionDTO.getTxnDetalle());
                            txn.setTxnDate(new Date());
                            txn.setTxnTotal(transaccionDTO.getTxnTotal());
                            log.info("Objeto guardado, procesando guardado txn");
                            return transaccionRepository.save(txn);
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

    public Optional<Transaccion> getTransaccionByTxnInId(String id) {
        return transaccionRepository.findByTxnInId(id);
    }

    public List<Transaccion> getAllTransacciones() {
        return transaccionRepository.findAll();
    }

    public List<TransaccionProcessingHistory> getAllTransaccionesProcesadas() {
        return transaccionProcessingHistoryRepository.findAll();
    }

    @Transactional
    public Transaccion processTxn(String txnInId) {
        try {
            Optional<Transaccion> txnIn =
                    getTransaccionByTxnInId(txnInId);
            if (txnIn.isPresent()) {
                Transaccion transaccionBD = txnIn.get();
                log.info("Transaccion encontrada, cambiando a estado {}",PROCESS_PROCESSING);
                transaccionBD.setTxnEstado(PROCESS_PROCESSING);
                transaccionRepository.saveAndFlush(transaccionBD);
                log.info("Estado actualizado");
                try{
                    TransaccionProcessingHistory txnProcess=new TransaccionProcessingHistory();
                    log.info("Registrando historico procesamiento");
                    txnProcess.setTxnInId(txnInId);
                    txnProcess.setTxnProcessEstado(PROCESS_PROCESSED);
                    txnProcess.setTxnProcessDetalle("Procesado");
                    txnProcess.setTxnProcessDate(new Date());
                    transaccionProcessingHistoryRepository.save(txnProcess);
                    log.info("Registrando historico guardado");
                    log.info("Cambiando a estado {} txn {}",PROCESS_PROCESSED, txnInId);
                    Optional<Transaccion> txnInxUpdate =
                            getTransaccionByTxnInId(txnInId);
                    Transaccion transaccionBDxUpdate = txnInxUpdate.get();
                    transaccionBDxUpdate.setTxnEstado(PROCESS_PROCESSED);
                    log.info("Estado actualizado, transaccion {}, estado {}",txnInId,PROCESS_PROCESSED);
                    return transaccionRepository.saveAndFlush(transaccionBDxUpdate);
                }catch (PersistenceException ex) {
                    // Manejar errores de la base de datos, como problemas de conexión o constraints
                    log.error("Error al guardar transaccion en la base de datos. ", ex);
                    TransaccionProcessingHistory txnProcessFail=new TransaccionProcessingHistory();
                    log.info("Registrando historico procesamiento fallido");
                    txnProcessFail.setTxnInId(txnInId);
                    txnProcessFail.setTxnProcessEstado(PROCESS_RETRY_PENDING);
                    txnProcessFail.setTxnProcessDetalle("PersistenceException - Error BD");
                    txnProcessFail.setTxnProcessDate(new Date());
                    transaccionProcessingHistoryRepository.save(txnProcessFail);
                    log.info("Registrando historico guardado");
                    log.info("Cambiando a estado {} txn {}",PROCESS_FAILED, txnInId);
                    Optional<Transaccion> txnInxFailed =
                            getTransaccionByTxnInId(txnInId);
                    Transaccion transaccionBDxFailed = txnInxFailed.get();
                    transaccionBDxFailed.setTxnEstado(PROCESS_FAILED);
                    log.info("Estado actualizado, transaccion {}, estado {}",txnInId,PROCESS_FAILED);
                    transaccionRepository.saveAndFlush(transaccionBDxFailed);
                    return null;
                } catch (Exception ex) {
                    // Manejar cualquier otra excepción
                    log.error("Error inesperado al guardar la transaccion. ", ex);
                    TransaccionProcessingHistory txnProcessFail=new TransaccionProcessingHistory();
                    log.info("Registrando historico procesamiento fallido");
                    txnProcessFail.setTxnInId(txnInId);
                    txnProcessFail.setTxnProcessEstado(PROCESS_RETRY_PENDING);
                    txnProcessFail.setTxnProcessDetalle("PersistenceException - Error BD");
                    txnProcessFail.setTxnProcessDate(new Date());
                    transaccionProcessingHistoryRepository.save(txnProcessFail);
                    log.info("Registrando historico guardado");
                    log.info("Cambiando a estado {} txn {}",PROCESS_FAILED, txnInId);
                    Optional<Transaccion> txnInxFailed =
                            getTransaccionByTxnInId(txnInId);
                    Transaccion transaccionBDxFailed = txnInxFailed.get();
                    transaccionBDxFailed.setTxnEstado(PROCESS_FAILED);
                    log.info("Estado actualizado, transaccion {}, estado {}",txnInId,PROCESS_FAILED);
                    transaccionRepository.saveAndFlush(transaccionBDxFailed);
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
    public Transaccion processTxnFail(String txnInId) {
        try {
            TransaccionProcessingHistory txnProcessFail=new TransaccionProcessingHistory();
            log.info("Registrando historico procesamiento fallido");
            txnProcessFail.setTxnInId(txnInId);
            txnProcessFail.setTxnProcessEstado(PROCESS_RETRY_PENDING);
            txnProcessFail.setTxnProcessDetalle("PersistenceException - Error BD");
            txnProcessFail.setTxnProcessDate(new Date());
            transaccionProcessingHistoryRepository.save(txnProcessFail);
            log.info("Registrando historico guardado");
            log.info("Cambiando a estado {} txn {}",PROCESS_FAILED, txnInId);
            Optional<Transaccion> txnInxFailed =
                    getTransaccionByTxnInId(txnInId);
            Transaccion transaccionBDxFailed = txnInxFailed.get();
            transaccionBDxFailed.setTxnEstado(PROCESS_FAILED);
            log.info("Estado actualizado, transaccion {}, estado {}",txnInId,PROCESS_FAILED);
            return transaccionRepository.saveAndFlush(transaccionBDxFailed);
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
}
