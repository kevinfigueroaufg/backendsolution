package sv.unicomer.backendsolution.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TransactionFilterDTO {

    private String status;
    private String type;
    private String originSystem;
    private LocalDate startDate;
    private LocalDate endDate;

    private int page = 0;
    private int size = 20;
}
