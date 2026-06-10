package sv.unicomer.backendsolution.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv.unicomer.backendsolution.dto.TransaccionInDTO;
import sv.unicomer.backendsolution.entity.TipoTransaccion;
import sv.unicomer.backendsolution.entity.Transaccion;
import sv.unicomer.backendsolution.service.TransaccionService;

import java.util.List;

@RestController
@RequestMapping("/transacciones")
public class TransaccionController {

    private final TransaccionService transaccionService;

    public TransaccionController(TransaccionService transaccionService) {
        this.transaccionService = transaccionService;
    }

    @PostMapping("/saveTxn")
    public ResponseEntity<String> saveTxn(@RequestBody TransaccionInDTO txnIn) {
        Transaccion response = transaccionService.saveTxn(txnIn);
        if (response != null) {
            return ResponseEntity.ok("Transaccion "+response.getTxnTipo()+" guardada exitosamente. Idtxn Ext: " + response.getTxnInId()+" Idtxn Interno: "+response.getId()+" Estado: "+response.getTxnEstado());
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/transacciones")
    public ResponseEntity<List<Transaccion>> getAllTransacciones() {
        return ResponseEntity.ok(transaccionService.getAllTransacciones());
    }
}
