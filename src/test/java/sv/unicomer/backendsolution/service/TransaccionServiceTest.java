package sv.unicomer.backendsolution.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.unicomer.backendsolution.dto.TransaccionInDTO;
import sv.unicomer.backendsolution.entity.TipoTransaccion;
import sv.unicomer.backendsolution.entity.Transaccion;
import sv.unicomer.backendsolution.repository.TransaccionRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransaccionServiceTest {

    @Mock
    private TransaccionRepository transaccionRepository;

    @Mock
    private TipoTransaccionService tipoTransaccionService;

    @InjectMocks
    private TransaccionService transaccionService;

    @Test
    void guardarTransaccion() {

        TransaccionInDTO dto = new TransaccionInDTO();
        dto.setTxnInId("123");
        dto.setTxnTipo("COMPRA");
        dto.setTxnDetalle("Detalle");
        dto.setTxnSistema("WEB");

        TipoTransaccion tipo = new TipoTransaccion();

        when(tipoTransaccionService.getTipoTransaccionByNombre("COMPRA"))
                .thenReturn(Optional.of(tipo));

        when(transaccionRepository.findByTxnInId("123"))
                .thenReturn(Optional.empty());

        when(transaccionRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transaccion resultado = transaccionService.saveTxn(dto);

        assertNotNull(resultado);

        verify(transaccionRepository).save(any());
    }
}
