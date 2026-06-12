package sv.unicomer.backendsolution.controller;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import sv.unicomer.backendsolution.dto.TransaccionDetailDTO;
import sv.unicomer.backendsolution.dto.TransaccionFiltroDTO;
import sv.unicomer.backendsolution.dto.TransaccionInDTO;
import sv.unicomer.backendsolution.entity.Transaccion;
import sv.unicomer.backendsolution.service.TransaccionService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
class TransaccionControllerTest {

    @InjectMocks
    private TransaccionController transaccionController;

    @Mock
    private TransaccionService transaccionService;

    @Test
    void saveTxn_OK() {

        TransaccionInDTO dto = new TransaccionInDTO();
        dto.setTxnInId("TXN001");

        Transaccion transaccion = new Transaccion();
        transaccion.setId(1);
        transaccion.setTxnInId("TXN001");
        transaccion.setTxnTipo("PAGO");
        transaccion.setTxnEstado("PROCESSED");

        when(transaccionService.saveTxn(dto))
                .thenReturn(transaccion);

        ResponseEntity<String> response =
                transaccionController.saveTxn(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(
                "Transaccion PAGO guardada exitosamente. Idtxn Ext: TXN001 Idtxn Interno: 1 Estado: PROCESSED",
                response.getBody()
        );

        verify(transaccionService).saveTxn(dto);
    }

    @Test
    void processTxn_Ok() {

        String txnInId = "TXN001";

        Transaccion transaccion = new Transaccion();
        transaccion.setTxnInId(txnInId);

        when(transaccionService.processTxn(txnInId))
                .thenReturn(transaccion);

        ResponseEntity<String> response =
                transaccionController.processTxn(txnInId);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(
                "Transaccion TXN001 procesada exitosamente. ",
                response.getBody()
        );

        verify(transaccionService).processTxn(txnInId);
    }

    @Test
    void processTxnFail_Ok() {

        String txnInId = "TXN001";

        Transaccion transaccion = new Transaccion();
        transaccion.setTxnInId(txnInId);

        when(transaccionService.processTxnFail(txnInId))
                .thenReturn(transaccion);

        ResponseEntity<String> response =
                transaccionController.processTxnFail(txnInId);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(
                "Transaccion TXN001 procesada fallida. ",
                response.getBody()
        );

        verify(transaccionService).processTxnFail(txnInId);
    }

    @Test
    void buscarTransacciones_Ok() {

        TransaccionFiltroDTO filtro = new TransaccionFiltroDTO();

        Page<Transaccion> pagina = new PageImpl<>(List.of());

        when(transaccionService.buscarTransacciones(filtro))
                .thenReturn(pagina);

        ResponseEntity<Page<Transaccion>> response =
                transaccionController.buscarTransacciones(filtro);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(pagina, response.getBody());

        verify(transaccionService).buscarTransacciones(filtro);
    }
    @Test
    void buscarTransaccion_Ok() {

        String txnInId = "TXN001";

        TransaccionDetailDTO detailDTO = new TransaccionDetailDTO();
        detailDTO.setTxnInId(txnInId);

        Optional<TransaccionDetailDTO> responseService =
                Optional.of(detailDTO);

        when(transaccionService.getTransaccionDetailByTxnInId(txnInId))
                .thenReturn(responseService);

        ResponseEntity<Optional<TransaccionDetailDTO>> response =
                transaccionController.getTransaccionDetailByTxnInId(txnInId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isPresent());
        assertEquals(
                txnInId,
                response.getBody().get().getTxnInId()
        );

        verify(transaccionService)
                .getTransaccionDetailByTxnInId(txnInId);
    }
}
