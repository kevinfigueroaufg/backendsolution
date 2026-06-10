package sv.unicomer.backendsolution.service;

import jakarta.persistence.PersistenceException;
import org.springframework.stereotype.Service;
import sv.unicomer.backendsolution.entity.Transaccion;
import sv.unicomer.backendsolution.repository.TransaccionRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;

    public TransaccionService(TransaccionRepository transaccionRepository) {
        this.transaccionRepository = transaccionRepository;
    }

    
}
