package sv.unicomer.backendsolution.service;

import org.springframework.stereotype.Service;
import sv.unicomer.backendsolution.entity.TipoTransaccion;
import sv.unicomer.backendsolution.entity.Transaccion;
import sv.unicomer.backendsolution.repository.TipoTransaccionRepository;
import sv.unicomer.backendsolution.repository.TransaccionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TipoTransaccionService {
    private final TipoTransaccionRepository tipoTransaccionRepository;

    public TipoTransaccionService(TipoTransaccionRepository tipoTransaccionRepository) {
        this.tipoTransaccionRepository = tipoTransaccionRepository;
        loadTipoTxn();
    }

    public void loadTipoTxn() {
        ArrayList<TipoTransaccion> avaliableTipoTransaccion= new ArrayList<TipoTransaccion>();
        avaliableTipoTransaccion.add(new TipoTransaccion("PAGO COLECTURIA"));
        avaliableTipoTransaccion.add(new TipoTransaccion("PAGO SERVICIO BASICO"));
        avaliableTipoTransaccion.add(new TipoTransaccion("PAGO TELEFONIA"));
        avaliableTipoTransaccion.add(new TipoTransaccion("CONSULTA DE SALDO"));
        avaliableTipoTransaccion.add(new TipoTransaccion("RECARGA TELEFONICA"));
        for (TipoTransaccion tipoTxn : avaliableTipoTransaccion) {
            tipoTransaccionRepository.save(tipoTxn);
        }
    }

    public List<TipoTransaccion> getAllTipotransacciones() {
        return tipoTransaccionRepository.findAll();
    }

    public Optional<TipoTransaccion> getTipoTransaccionByNombre(String nombre) {
        return tipoTransaccionRepository.findByTxnTipoNombre(nombre);
    }
}
