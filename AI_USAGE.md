#Herramienta utilizada
ChatGPT
Google
stackoverflow
Maven repository
Springboot Doc

#Motivo de su uso
Investigacion, correcccion o corroborar codigo
Copia de dependencias o librerias
Adicion de Swagger (no solo OpenAPI) al proyecto
Validacion de funcion Page y Query contra documentacion springboot
Creacion de bash para ejecucion del proyecto War

#Algunos promps
*cuando consulto http://localhost:8081/swagger-ui.html me da Whitelabel Error Page
se solvento con springdoc.api-docs.enabled=true
springdoc.swagger-ui.enabled=true y se descarto la clase SecurityFilterChain disable (esto por motivos de ciberseguridad esta prohibido deshabilitar el csrf)

*Endpoint con pageable en sprinboot
sugerio public Page<Transaccion> getTransacciones, se acepto por haberlo implementado previamente, 
se descarto el uso de JpaSpecificationExecutor<Transaccion> por complegidad y desconocimiento,
se adicono @Query para personalizacion en los filtros

Las validaciones se realizaron a traves de pruebas con los request de las colecciones Postman y Debug