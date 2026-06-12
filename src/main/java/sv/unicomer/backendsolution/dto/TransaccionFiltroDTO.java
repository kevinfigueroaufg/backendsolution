package sv.unicomer.backendsolution.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TransaccionFiltroDTO {

    private String estado;
    private String tipo;
    private String sistemaOrigen;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    private int page = 0;
    private int size = 20;
}
