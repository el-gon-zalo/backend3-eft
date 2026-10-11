Instrucciones de arranque: 

 
Desplegar el proyecto mediante Docker Compose. Inmediatamente, el servicio backend poblará la base de datos translog, en el puerto 3306, con los resultados del proceso Batch. 

El cliente pide un token a auth-server con su client_id, secret y scope. 

Llama a un BFF con Authorization: Bearer <token>. 

El BFF valida el JWT con las claves públicas de auth-server (JWKS) y comprueba el scope del canal o admin.access. 

El BFF reenvía el mismo token a translog-batch (http://backend:8080). 

translog-batch valida el token, consulta MySQL y responde. 

Para operar sobre movimientos, el cliente llama directamente a mf-service. 

mf-service guarda el cambio en MySQL y publica o consume los eventos en Kafka. 

Al arrancar, cada servicio lee su configuración de config-server. 


Tecnologías utilizadas: 

 

Lenguaje y framework: Java 17, Spring Boot 3.5, Spring Cloud 2025.0.0 

Procesamiento por lotes: Spring Batch (partición en 4 workers, chunk de 50) 

Base de datos: MySQL 8.0 

Mensajería: Apache Kafka 3.9.1 

Configuración centralizada: Spring Cloud 

Descubrimiento de servicios: Eureka 

Seguridad: Protocolo OAuth2. Spring Authorization Server + Resource Server (JWT, client_credentials) 

Contenedores: Docker y Docker Compose (builds multietapa con Maven) 

Nube: AWS EC2 Ubuntu 



Servicios y puertos: 

 

Base de datos MySQL: Puerto 3306 

Kafka: Puerto 9092 

Config-server: Puerto 8888 

Discovery-server: Puerto 8761 

Auth-server: Puerto 9000 

Backend (Spring Batch): Puerto 8080 

BFF-cajero: Puerto 8081 

BFF-mobile: Puerto 8082 

BFF-web: Puerto 8083 

Mf-service (Manejo de eventos con patrón Saga): Puerto 8084 

 


Endpoints: 

Puerto 9000: Obtener token (auth-server):  
 

POST http://<IP>:9000/oauth2/token  

Auth: Basic (client / secret)  

Body (x-www-form-urlencoded): grant_type=client_credentials, scope=admin.access 

 

El parámetro scope es obligatorio: sin él el token sale sin permisos y los servicios responden 403. 

 

Resumen de Endpoints: 

 

Puerto 8080: 

 

MovimientoFinanciero 

GET /{id}, GET /todos 

EstadoFinanciero 

GET /{id}, GET /todos, GET /cuenta/{cuentaId} 

Interes 

GET /{id}, GET /todos, GET /cuenta/{cuentaId} 

 

Puertos 8081, 8082, 8083:  

 

<Bff> puede ser web, mobile o cajero 

 

GET  

 

Estados financieros 

 

<Bff>/estados-financieros/todos 

<Bff>/estados-financieros/{id} 

<Bff>/estados-financieros/cuenta/{cuentaId} 

 

Intereses 

 

<Bff>/intereses/todos 

<Bff>/intereses /{id} 

<Bff>/intereses /cuenta/{cuentaId} 

 

Movimientos financieros 

 

<Bff>/movimientos-financieros/todos 

<Bff>/movimientos-financieros/{id} 

 

 

Puerto 8084: 

 

Método 

Ruta 

Acción 

POST 

/api/movimientos-financieros 

Crea un movimiento (fecha, monto, tipo) en estado PENDING 

GET 

/api/movimientos-financieros 

Lista todos 

GET 

/api/movimientos-financieros/{id} 

Consulta uno 

POST 

/api/movimientos-financieros/{id}/approve 

Publica el evento de aprobación (pasa a CONFIRMED) 

POST 

/api/movimientos-financieros/{id}/reject 

Rechazo, con body {"reason": "..."} (pasa a CANCELLED) 

POST 

/api/movimientos-financieros/{id}/replay-created 

Republica el evento Created con el mismo eventId 

 

 
