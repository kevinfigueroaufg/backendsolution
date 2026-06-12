#Descripcion
Se desarrollo una plataforma de integración recibe transacciones desde sistemas externos. 
Cada transacción se registra, valida, procesa y queda disponible para consulta posterior.

#Requisitos Tecnicos
JDK 17
IntellijIDEA
Postman
Github
BD H2
Mozilla Firefox/Internet Explorer/Google Chrome

#Cómo ejecutar el proyecto
1. Descargue el archivo comprimido backend_unicomer desde el enlace WeTransfer link
2. Descomprima el archivo descargado, dentro de la carpeta backend_unicomer ejecute el archivo iniciar_backend.bat espere que inicie el servidor y el proyecto

NOTA: El codigo del proyecto esta en el repositorio github https://github.com/kevinfigueroaufg/backendsolution.git, descargue/clone y ejecute en la IDE de preferencia con RUN (IntellijIDEA)

#Ejecucion de pruebas
1. Ingrese a la carpeta descargada (backend_unicomer), dentro esta un archivo Unicomer.postman_collection.json contiene las colecciones de peticiones de Postman
para set de pruebas del proyecto
2. Importe el archivo de colecciones en Postman para poder visualizarlas
3. Dentro de postman ubique una coleccion llamada Unicomer, dentro de ella habran diversos request pero en nuestro caso para pruebas utilizaemos los de la carpeta
Transacciones
4. Los request de la coleccion que son necesarios para probar el flujo de la solucion son Guardar transaccion, Procesar transaccion, Buscar transaccion, Consultar transacciones lista.
En el caso que se desee emular un escenario fallido debera ejecutarse los request Procesar transaccion Fail, Procesar transaccion, Buscar transaccion en ese orden
5. El reporte de covertura se genera con jacoco puede ocupar las tareas clean y test de maven en la IDE

#Ejemplos de requests/responses
NOTA: Consulte SWAGGER en http://localhost:8081/swagger-ui/index.html o http://localhost:8081/v3/api-docs cuando el proyecto este desplegado
...Guardar transaccion...
request
{

    "txnInId": "329c85b8ff824ed8b4fa577a26df3aaa",
    "txnTipo": "RECARGA TELEFONICA",
    "txnSistema": "TELECOM BILL",
    "txnDetalle": "Superpack 4D",
    "txnTotal": 4.00

}
response
Transaccion RECARGA TELEFONICA guardada exitosamente. Idtxn Ext: 329c85b8ff824ed8b4fa577a26df3aaa Idtxn Interno: 1 Estado: PROCESSED
...Buscar transaccion...
request
http://localhost:8081/transacciones/txnDetail/329c85b8ff824ed8b4fa577a26df3aaa
response
{
    "txnInId": "329c85b8ff824ed8b4fa577a26df3aaa",
    "txnTipo": "RECARGA TELEFONICA",
    "txnEstado": "PROCESSED",
    "txnSistema": "TELECOM BILL",
    "txnDetalle": "Superpack 4D",
    "txnDate": "2026-06-12T05:37:32.986+00:00",
    "txnTotal": 4.0,
    "txnProcessingHistory": [
        {
            "id": 1,
            "txnInId": "329c85b8ff824ed8b4fa577a26df3aaa",
            "txnProcessEstado": "PROCESSED",
            "txnProcessDetalle": "Procesado",
            "txnProcessDate": "2026-06-12T05:37:33.018+00:00"
        }
    ]
}

#Decisiones técnicas relevantes
Cumplimiento de requisitos tecnicos solicitados

#Supuestos realizados
Guardado de transaccion
Procesamiento de transaccion
Busqueda de transaccion individual
Consulta de transacciones (listado con paginacion)

#Pendientes o mejoras futuras
*Procesamiento de transacciones por lote
*UUID para id de transacciones internas
*Catalogo de sistemas permitidos
*Limites en los montos transaccionados
*Logs internos almacenados en BD
*Migracion a otro motor de BD, con BD diseñada adecuadamente
*Despliegue en contenedor
