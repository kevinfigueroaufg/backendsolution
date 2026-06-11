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
import sv.unicomer.backendsolution.exceptions.CustomException;
import sv.unicomer.backendsolution.repository.TransaccionRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final TipoTransaccionService tipoTransaccionService;

    public TransaccionService(TransaccionRepository transaccionRepository, TipoTransaccionService tipoTransaccionService) {
        this.transaccionRepository = transaccionRepository;
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
                            txn.setTxnEstado("RECEIVED");
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
}
