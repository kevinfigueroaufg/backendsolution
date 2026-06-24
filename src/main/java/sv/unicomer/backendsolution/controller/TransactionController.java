package sv.unicomer.backendsolution.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv.unicomer.backendsolution.dto.TransactionDetailDTO;
import sv.unicomer.backendsolution.dto.TransactionFilterDTO;
import sv.unicomer.backendsolution.dto.TransactionInDTO;
import sv.unicomer.backendsolution.entity.Transaction;
import sv.unicomer.backendsolution.entity.TransactionProcessingHistory;
import sv.unicomer.backendsolution.service.TransactionService;

import java.util.List;
import java.util.Optional;

import static sv.unicomer.backendsolution.util.MessageConstants.*;

@RestController
@RequestMapping("/transacciones")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
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
    public ResponseEntity<String> saveTxn(@RequestBody TransactionInDTO txnIn) {
        Transaction response = transactionService.saveTxn(txnIn);

        return response != null ? ResponseEntity.ok(
                "Transaccion " + response.getTxnType() +
                        " guardada exitosamente. Idtxn Ext: " + response.getTxnInId() +
                        " Idtxn Interno: " + response.getId() +
                        " Estado: " + response.getTxnStatus() ) : ResponseEntity.badRequest().body("Error al procesar transaccion");
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
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
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
        Transaction response = transactionService.processTxn(txnInId);
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
    public ResponseEntity<List<TransactionProcessingHistory>> getAllTransactionsProcessed() {
        return ResponseEntity.ok(transactionService.getAllTransactionsProcessed());
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
        Transaction response = transactionService.processTxnFail(txnInId);
        return response != null ? ResponseEntity.ok(
                "Transaccion " + response.getTxnInId() +
                        " procesada fallida. ") : ResponseEntity.badRequest().body("Error al procesar transaccion");

    }

    @Operation(
            summary = "Consulta lista transacciones con filtros",
            description = "Consulta lista transacciones con filtros"
    )
    @ApiResponses({
            @ApiResponse(responseCode = NOT_FOUND, description = REC_NOT_FOUND),
            @ApiResponse(responseCode = OK, description = LIST_OK)
    })
    @PostMapping("/buscarTxns")
    public ResponseEntity<Page<Transaction>> searchTransactions(
            @RequestBody TransactionFilterDTO filtro) {

        return ResponseEntity.ok(
                transactionService.searchTransactions(filtro));
    }

    @Operation(
            summary = "Busca un transaccion en especifico y muestra detalle",
            description = "Busca un transaccion en especifico y muestra detalle"
    )
    @ApiResponses({
            @ApiResponse(responseCode = NOT_FOUND, description = REC_NOT_FOUND),
            @ApiResponse(responseCode = OK, description = LIST_OK),
            @ApiResponse(responseCode = BAD_REQUEST, description = REC_BAD_REQUEST)
    })

    @PostMapping("/txnDetail/{txnInId}")
    public ResponseEntity<Optional<TransactionDetailDTO>> getTransactionDetailByTxnInId(@PathVariable String txnInId) {
        Optional<TransactionDetailDTO> response = transactionService.getTransactionDetailByTxnInId(txnInId);
        return response.isPresent() ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }
}
