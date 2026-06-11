package sv.unicomer.backendsolution.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sv.unicomer.backendsolution.entity.TipoTransaccion;
import sv.unicomer.backendsolution.service.TipoTransaccionService;

import java.util.List;

import static sv.unicomer.backendsolution.util.MessageConstants.*;
import static sv.unicomer.backendsolution.util.MessageConstants.LIST_OK;

@RestController
@RequestMapping("/catalogos")
public class TipoTransaccionController {

    private final TipoTransaccionService tipoTransaccionService;

    public TipoTransaccionController(TipoTransaccionService tipoTransaccionService) {
        this.tipoTransaccionService = tipoTransaccionService;
    }
    @Operation(
            summary = "Lista de tipos de transacciones permitidas",
            description = "Lista de tipos de transacciones permitidas"
    )
    @ApiResponses({
            @ApiResponse(responseCode = NOT_FOUND, description = REC_NOT_FOUND),
            @ApiResponse(responseCode = OK, description = LIST_OK)
    })
    @GetMapping("/tipotransacciones")
    public ResponseEntity<List<TipoTransaccion>> getAllTipotransacciones() {
        return ResponseEntity.ok(tipoTransaccionService.getAllTipotransacciones());
    }

    @Operation(
            summary = "Busqueda de tipo transaccion especifica",
            description = "Busqueda de tipo transaccion especifica"
    )
    @ApiResponses({
            @ApiResponse(responseCode = NOT_FOUND, description = REC_NOT_FOUND),
            @ApiResponse(responseCode = OK, description = REC_FOUND)
    })
    @GetMapping("/tipotransacciones/{nombre}")
    public ResponseEntity<TipoTransaccion> getOrdersByID(@PathVariable String nombre) {
        return tipoTransaccionService.getTipoTransaccionByNombre(nombre)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
