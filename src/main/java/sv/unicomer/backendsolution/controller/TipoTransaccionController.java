package sv.unicomer.backendsolution.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sv.unicomer.backendsolution.entity.TipoTransaccion;
import sv.unicomer.backendsolution.service.TipoTransaccionService;

import java.util.List;

@RestController
@RequestMapping("/catalogos")
public class TipoTransaccionController {

    private final TipoTransaccionService tipoTransaccionService;

    public TipoTransaccionController(TipoTransaccionService tipoTransaccionService) {
        this.tipoTransaccionService = tipoTransaccionService;
    }
    @GetMapping("/tipotransacciones")
    public ResponseEntity<List<TipoTransaccion>> getAllTipotransacciones() {
        return ResponseEntity.ok(tipoTransaccionService.getAllTipotransacciones());
    }

    @GetMapping("/tipotransacciones/{nombre}")
    public ResponseEntity<TipoTransaccion> getOrdersByID(@PathVariable String nombre) {
        return tipoTransaccionService.getTipoTransaccionByNombre(nombre)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
