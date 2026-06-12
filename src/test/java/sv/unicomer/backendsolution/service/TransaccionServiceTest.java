package sv.unicomer.backendsolution.service;

import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import sv.unicomer.backendsolution.dto.TransaccionDetailDTO;
import sv.unicomer.backendsolution.dto.TransaccionFiltroDTO;
import sv.unicomer.backendsolution.dto.TransaccionInDTO;
import sv.unicomer.backendsolution.entity.TipoTransaccion;
import sv.unicomer.backendsolution.entity.Transaccion;
import sv.unicomer.backendsolution.entity.TransaccionProcessingHistory;
import sv.unicomer.backendsolution.exceptions.CustomException;
import sv.unicomer.backendsolution.repository.TransaccionProcessingHistoryRepository;
import sv.unicomer.backendsolution.repository.TransaccionRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static sv.unicomer.backendsolution.util.MessageConstants.PROCESS_FAILED;

@ExtendWith(MockitoExtension.class)
class TransaccionServiceTest {

    @Mock
    private TransaccionRepository transaccionRepository;

    @Mock
    private TipoTransaccionService tipoTransaccionService;

    @InjectMocks
    private TransaccionService transaccionService;

    @Mock
    private TransaccionProcessingHistoryRepository transaccionProcessingHistoryRepository;

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

    @Test
    void guardarTransaccion_ID_duplcado() {

        TransaccionInDTO dto = new TransaccionInDTO();
        dto.setTxnInId("TXN001");

        Transaccion existente = new Transaccion();

        TransaccionService spyService = Mockito.spy(transaccionService);

        doReturn(Optional.of(existente))
                .when(spyService)
                .getTransaccionByTxnInId("TXN001");

        Exception ex = assertThrows(
                Exception.class,
                () -> spyService.saveTxn(dto)
        );

        System.out.println(ex.getClass().getName());
    }

    @Test
    void guardarTransaccion_tipo_inexistente() {

        TransaccionInDTO dto = new TransaccionInDTO();
        dto.setTxnInId("TXN001");
        dto.setTxnTipo("INVALIDO");
        dto.setTxnDetalle("Detalle");

        TransaccionService spyService = Mockito.spy(transaccionService);

        doReturn(Optional.empty())
                .when(spyService)
                .getTransaccionByTxnInId("TXN001");

        when(tipoTransaccionService.getTipoTransaccionByNombre("INVALIDO"))
                .thenReturn(Optional.empty());

        Exception ex = assertThrows(
                Exception.class,
                () -> spyService.saveTxn(dto)
        );

        assertTrue(ex.getMessage().contains("Error"));
    }

    @Test
    void guardar_transaccion_descripcion_nula() {

        TransaccionInDTO dto = new TransaccionInDTO();
        dto.setTxnInId("TXN001");
        dto.setTxnTipo("PAGO");
        dto.setTxnDetalle(null);

        TipoTransaccion tipo = new TipoTransaccion();

        TransaccionService spyService = Mockito.spy(transaccionService);

        doReturn(Optional.empty())
                .when(spyService)
                .getTransaccionByTxnInId("TXN001");

        when(tipoTransaccionService.getTipoTransaccionByNombre("PAGO"))
                .thenReturn(Optional.of(tipo));

        Exception ex = assertThrows(
                Exception.class,
                () -> spyService.saveTxn(dto)
        );

        assertNotNull(ex);
    }

    @Test
    void guardar_Transaccion_error_persistencia() {

        TransaccionInDTO dto = new TransaccionInDTO();
        dto.setTxnInId("TXN001");
        dto.setTxnTipo("PAGO");
        dto.setTxnDetalle("Detalle");

        TipoTransaccion tipo = new TipoTransaccion();

        TransaccionService spyService = Mockito.spy(transaccionService);

        doReturn(Optional.empty())
                .when(spyService)
                .getTransaccionByTxnInId("TXN001");

        when(tipoTransaccionService.getTipoTransaccionByNombre("PAGO"))
                .thenReturn(Optional.of(tipo));

        when(transaccionRepository.save(any(Transaccion.class)))
                .thenThrow(new PersistenceException("Error BD"));

        Exception ex = assertThrows(
                Exception.class,
                () -> spyService.saveTxn(dto)
        );

        assertNotNull(ex);
    }

    @Test
    void procesar_transaccion_OK() {

        Transaccion txnInicial = new Transaccion();
        txnInicial.setTxnInId("TXN001");
        txnInicial.setTxnEstado("PENDING");

        Transaccion txnFinal = new Transaccion();
        txnFinal.setTxnInId("TXN001");
        txnFinal.setTxnEstado("PROCESSING");

        Transaccion txnProcesada = new Transaccion();
        txnProcesada.setTxnInId("TXN001");
        txnProcesada.setTxnEstado("PROCESSED");

        TransaccionService spyService = Mockito.spy(transaccionService);

        doReturn(Optional.of(txnInicial))
                .doReturn(Optional.of(txnFinal))
                .when(spyService)
                .getTransaccionByTxnInId("TXN001");

        when(transaccionRepository.saveAndFlush(any(Transaccion.class)))
                .thenReturn(txnProcesada);

        Transaccion resultado = spyService.processTxn("TXN001");

        assertNotNull(resultado);
        assertEquals("TXN001", resultado.getTxnInId());

        verify(transaccionProcessingHistoryRepository)
                .save(any(TransaccionProcessingHistory.class));

        verify(transaccionRepository, times(2))
                .saveAndFlush(any(Transaccion.class));
    }

    @Test
    void procesar_transaccion_txn_no_encontrada() {

        TransaccionService spyService = Mockito.spy(transaccionService);

        doReturn(Optional.empty())
                .when(spyService)
                .getTransaccionByTxnInId("TXN001");

        assertThrows(
                CustomException.class,
                () -> spyService.processTxn("TXN001")
        );
    }

    @Test
    void procesar_transaccion_txn_ya_procesada() {

        Transaccion txn = new Transaccion();
        txn.setTxnInId("TXN001");
        txn.setTxnEstado("PROCESSED");

        TransaccionService spyService = Mockito.spy(transaccionService);

        doReturn(Optional.of(txn))
                .when(spyService)
                .getTransaccionByTxnInId("TXN001");

        Transaccion resultado = spyService.processTxn("TXN001");

        assertNull(resultado);

        verify(transaccionRepository, never()).saveAndFlush(any());
    }

    @Test
    void procesar_transaccion_excepcion_persistencia() {

        Transaccion txnInicial = new Transaccion();
        txnInicial.setTxnInId("TXN001");
        txnInicial.setTxnEstado("PENDING");

        Transaccion txnFailed = new Transaccion();
        txnFailed.setTxnInId("TXN001");

        TransaccionService spyService = Mockito.spy(transaccionService);

        doReturn(Optional.of(txnInicial))
                .doReturn(Optional.of(txnFailed))
                .when(spyService)
                .getTransaccionByTxnInId("TXN001");

        when(transaccionProcessingHistoryRepository.save(any()))
                .thenThrow(new PersistenceException("Error BD"))
                .thenReturn(new TransaccionProcessingHistory());

        Transaccion resultado = spyService.processTxn("TXN001");

        assertNull(resultado);

        verify(transaccionProcessingHistoryRepository, times(2))
                .save(any());

        verify(transaccionRepository, atLeastOnce())
                .saveAndFlush(any(Transaccion.class));
    }

    @Test
    void procesar_transaccion_excepcion_generica() {

        Transaccion txnInicial = new Transaccion();
        txnInicial.setTxnInId("TXN001");
        txnInicial.setTxnEstado("PENDING");

        Transaccion txnFailed = new Transaccion();
        txnFailed.setTxnInId("TXN001");

        TransaccionService spyService = Mockito.spy(transaccionService);

        doReturn(Optional.of(txnInicial))
                .doReturn(Optional.of(txnFailed))
                .when(spyService)
                .getTransaccionByTxnInId("TXN001");

        when(transaccionProcessingHistoryRepository.save(any()))
                .thenThrow(new RuntimeException("Error inesperado"))
                .thenReturn(new TransaccionProcessingHistory());

        Transaccion resultado = spyService.processTxn("TXN001");

        assertNull(resultado);

        verify(transaccionProcessingHistoryRepository, times(2))
                .save(any());

        verify(transaccionRepository, atLeastOnce())
                .saveAndFlush(any(Transaccion.class));
    }

    @Test
    void buscarTransacciones_Verificacion_filtro() {

        TransaccionFiltroDTO filtro = new TransaccionFiltroDTO();
        filtro.setPage(0);
        filtro.setSize(10);
        filtro.setEstado("PENDING");
        filtro.setTipo("PAGO");
        filtro.setSistemaOrigen("APP");
        filtro.setFechaInicio(LocalDate.of(2026, 6, 1));
        filtro.setFechaFin(LocalDate.of(2026, 6, 12));

        Page<Transaccion> pageEsperada =
                new PageImpl<>(Collections.emptyList());

        when(transaccionRepository.buscarTransacciones(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(Pageable.class)))
                .thenReturn(pageEsperada);

        Page<Transaccion> resultado =
                transaccionService.buscarTransacciones(filtro);

        assertNotNull(resultado);
        assertEquals(pageEsperada, resultado);

        verify(transaccionRepository).buscarTransacciones(
                eq("PENDING"),
                eq("PAGO"),
                eq("APP"),
                eq(java.sql.Date.valueOf("2026-06-01")),
                eq(java.sql.Timestamp.valueOf("2026-06-12 23:59:59")),
                eq(PageRequest.of(0, 10))
        );
    }

    @Test
    void buscarTransaccion_TxnOKconHistorial() {

        String txnId = "TXN001";

        Transaccion txn = new Transaccion();
        txn.setTxnInId(txnId);
        txn.setTxnTipo("PAGO");
        txn.setTxnEstado("PROCESSED");
        txn.setTxnDetalle("Detalle");
        txn.setTxnSistema("APP");
        txn.setTxnTotal(0.00);

        TransaccionProcessingHistory history =
                new TransaccionProcessingHistory();
        history.setTxnInId(txnId);

        List<TransaccionProcessingHistory> historial =
                List.of(history);

        TransaccionService spyService =
                Mockito.spy(transaccionService);

        doReturn(Optional.of(txn))
                .when(spyService)
                .getTransaccionByTxnInId(txnId);

        when(transaccionProcessingHistoryRepository
                .findListByTxnInId(txnId))
                .thenReturn(historial);

        Optional<TransaccionDetailDTO> resultado =
                spyService.getTransaccionDetailByTxnInId(txnId);

        assertTrue(resultado.isPresent());

        TransaccionDetailDTO dto = resultado.get();

        assertEquals(txnId, dto.getTxnInId());
        assertEquals("PAGO", dto.getTxnTipo());
        assertEquals("PROCESSED", dto.getTxnEstado());
        assertEquals(historial, dto.getTxnProcessingHistory());

        verify(transaccionProcessingHistoryRepository)
                .findListByTxnInId(txnId);
    }

    @Test
    void buscarTransaccion_TxnOKsinHistorial() {

        String txnId = "TXN001";

        Transaccion txn = new Transaccion();
        txn.setTxnInId(txnId);
        txn.setTxnTipo("PAGO");

        TransaccionService spyService =
                Mockito.spy(transaccionService);

        doReturn(Optional.of(txn))
                .when(spyService)
                .getTransaccionByTxnInId(txnId);

        when(transaccionProcessingHistoryRepository
                .findListByTxnInId(txnId))
                .thenReturn(Collections.emptyList());

        Optional<TransaccionDetailDTO> resultado =
                spyService.getTransaccionDetailByTxnInId(txnId);

        assertTrue(resultado.isPresent());

        TransaccionDetailDTO dto = resultado.get();

        assertNull(dto.getTxnProcessingHistory());

        verify(transaccionProcessingHistoryRepository)
                .findListByTxnInId(txnId);
    }

    @Test
    void emularTransaccionAFallida_OK() {

        String txnId = "TXN001";

        Transaccion txn = new Transaccion();
        txn.setTxnInId(txnId);
        txn.setTxnEstado("PROCESSING");

        Transaccion txnGuardada = new Transaccion();
        txnGuardada.setTxnInId(txnId);
        txnGuardada.setTxnEstado(PROCESS_FAILED);

        TransaccionService spyService = Mockito.spy(transaccionService);

        doReturn(Optional.of(txn))
                .when(spyService)
                .getTransaccionByTxnInId(txnId);

        when(transaccionProcessingHistoryRepository.save(any(
                TransaccionProcessingHistory.class)))
                .thenReturn(new TransaccionProcessingHistory());

        when(transaccionRepository.saveAndFlush(any(Transaccion.class)))
                .thenReturn(txnGuardada);

        Transaccion resultado = spyService.processTxnFail(txnId);

        assertNotNull(resultado);
        assertEquals(PROCESS_FAILED, resultado.getTxnEstado());

        verify(transaccionProcessingHistoryRepository)
                .save(any(TransaccionProcessingHistory.class));

        verify(transaccionRepository)
                .saveAndFlush(any(Transaccion.class));
    }
}
