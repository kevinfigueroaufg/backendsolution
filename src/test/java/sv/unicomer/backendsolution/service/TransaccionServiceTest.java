package sv.unicomer.backendsolution.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.unicomer.backendsolution.dto.TransaccionInDTO;
import sv.unicomer.backendsolution.entity.TipoTransaccion;
import sv.unicomer.backendsolution.entity.Transaccion;
import sv.unicomer.backendsolution.repository.TransaccionRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

        // Arrange
        TransaccionInDTO dto = new TransaccionInDTO();
        dto.setTxnInId("TXN001");
        dto.setTxnTipo("PAGO");
        dto.setTxnDetalle("Compra realizada");
        dto.setTxnSistema("WEB");
        dto.setTxnTotal(100.00);

        TipoTransaccion tipo = new TipoTransaccion();

        Transaccion transaccionProcesada = new Transaccion();
        transaccionProcesada.setTxnInId("TXN001");
        transaccionProcesada.setTxnEstado("PROCESSED");

        TransaccionService spyService = Mockito.spy(transaccionService);

        doReturn(Optional.of(tipo))
                .when(tipoTransaccionService)
                .getTipoTransaccionByNombre("PAGO");

        doReturn(Optional.empty())
                .when(spyService)
                .getTransaccionByTxnInId("TXN001");

        doReturn(transaccionProcesada)
                .when(spyService)
                .processTxn("TXN001");

        // Act
        Transaccion resultado = spyService.saveTxn(dto);

        // Assert
        assertNotNull(resultado);
        assertEquals("TXN001", resultado.getTxnInId());

        verify(transaccionRepository).save(any(Transaccion.class));
        verify(spyService).processTxn("TXN001");
    }
}
