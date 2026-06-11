package sv.unicomer.backendsolution.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv.unicomer.backendsolution.dto.TransaccionInDTO;
import sv.unicomer.backendsolution.entity.Transaccion;
import sv.unicomer.backendsolution.entity.TransaccionProcessingHistory;
import sv.unicomer.backendsolution.service.TransaccionService;

import java.util.List;

import static sv.unicomer.backendsolution.util.MessageConstants.*;

@RestController
@RequestMapping("/transacciones")
public class TransaccionController {

    private final TransaccionService transaccionService;

    public TransaccionController(TransaccionService transaccionService) {
        this.transaccionService = transaccionService;
    }

    @Operation(
            summary = "Guardar transaccion",
            description = "Guarda transaccion externa"
    )
    @ApiResponses({
            @ApiResponse(responseCode = OK, description = SAVE_OK),
            @ApiResponse(responseCode = NOT_FOUND, description = REC_NOT_FOUND)
    })
    @PostMapping("/saveTxn")
    public ResponseEntity<String> saveTxn(@RequestBody TransaccionInDTO txnIn) {
        Transaccion response = transaccionService.saveTxn(txnIn);

        return response != null ? ResponseEntity.ok(
                "Transaccion " + response.getTxnTipo() +
                        " guardada exitosamente. Idtxn Ext: " + response.getTxnInId() +
                        " Idtxn Interno: " + response.getId() +
                        " Estado: " + response.getTxnEstado() ) : ResponseEntity.badRequest().body("Error al procesar transaccion");
    }

    @Operation(
            summary = "Lista de transacciones",
            description = "Lista de transacciones almacenadas"
    )
    @ApiResponses({
            @ApiResponse(responseCode = NOT_FOUND, description = REC_NOT_FOUND),
            @ApiResponse(responseCode = OK, description = LIST_OK)
    })
    @GetMapping("/transacciones")
    public ResponseEntity<List<Transaccion>> getAllTransacciones() {
        return ResponseEntity.ok(transaccionService.getAllTransacciones());
    }

    @Operation(
            summary = "Procesa las transacciones recibidas",
            description = "Procesa las transacciones recibidas y reprocesa fallidas"
    )
    @ApiResponses({
            @ApiResponse(responseCode = NOT_FOUND, description = REC_NOT_FOUND),
            @ApiResponse(responseCode = OK, description = LIST_OK),
            @ApiResponse(responseCode = BAD_REQUEST, description = REC_BAD_REQUEST)
    })
    @PostMapping("/processTxn/{txnInId}")
    public ResponseEntity<String> processTxn(@PathVariable String txnInId) {
        Transaccion response = transaccionService.processTxn(txnInId);
            return response != null ? ResponseEntity.ok(
                    "Transaccion " + response.getTxnInId() +
                            " procesada exitosamente. ") : ResponseEntity.badRequest().body("Error al procesar transaccion");

    }

    @Operation(
            summary = "Lista de transacciones procesadas (Historico)",
            description = "Lista de transacciones procesadas (Historico)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = NOT_FOUND, description = REC_NOT_FOUND),
            @ApiResponse(responseCode = OK, description = LIST_OK)
    })
    @GetMapping("/transaccionesProcesadas")
    public ResponseEntity<List<TransaccionProcessingHistory>> getAllTransaccionesProcesadas() {
        return ResponseEntity.ok(transaccionService.getAllTransaccionesProcesadas());
    }

    @Operation(
            summary = "Emula estados de transaccion fallida",
            description = "Emula estados de transaccion fallida en estado recibidio"
    )
    @ApiResponses({
            @ApiResponse(responseCode = NOT_FOUND, description = REC_NOT_FOUND),
            @ApiResponse(responseCode = OK, description = LIST_OK),
            @ApiResponse(responseCode = BAD_REQUEST, description = REC_BAD_REQUEST)
    })
    @PostMapping("/processTxnFail/{txnInId}")
    public ResponseEntity<String> processTxnFail(@PathVariable String txnInId) {
        Transaccion response = transaccionService.processTxnFail(txnInId);
        return response != null ? ResponseEntity.ok(
                "Transaccion " + response.getTxnInId() +
                        " procesada fallida. ") : ResponseEntity.badRequest().body("Error al procesar transaccion");

    }
}
