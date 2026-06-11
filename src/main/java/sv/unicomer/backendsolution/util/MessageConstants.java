package sv.unicomer.backendsolution.util;

public final  class MessageConstants {

    //API RESPONSES
    public static final String OK = "200";
    public static final String CREATED = "201";
    public static final String NO_CONTENT = "204";
    public static final String BAD_REQUEST = "400";
    public static final String UNAUTHORIZED = "401";
    public static final String FORBIDDEN = "403";
    public static final String NOT_FOUND = "404";
    public static final String CONFLICT = "409";
    public static final String INTERNAL_SERVER_ERROR = "500";

    //TRANSACCION RESPUESTA
    public static final String SAVE_OK = "Guardado exitosamente";
    public static final String REC_NOT_FOUND = "No encontrado";
    public static final String REC_FOUND = "Encontrado";

    //CONSULTA RESPUESTA
    public static final String LIST_OK = "Listado exitosamente";

    //ESTADOS DE PROCESAMIENTO
    public static final String PROCESS_RECEIVED = "RECEIVED";
    public static final String PROCESS_PROCESSING = "PROCESSING";
    public static final String PROCESS_PROCESSED = "PROCESSED";
    public static final String PROCESS_FAILED = "FAILED";
    public static final String PROCESS_RETRY_PENDING = "RETRY_PENDING";
}
