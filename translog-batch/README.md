# TransLog Batch - Semana 3

Ejemplo  de Spring Batch con:

- Job y Steps
- Chunk processing
- Reader / Processor / Writer
- Particiones Manager/Worker
- `ExecutionContext`
- `TaskExecutorPartitionHandler`
- procesamiento paralelo
- Retry
- Backoff
- SkipPolicy
- SkipListener
- PostgreSQL
- JobRepository
- reporte final CSV

## Requisitos

- Java 17
- Maven 3.9+
- Docker + Docker Compose

## 1. Levantar PostgreSQL

```bash
docker compose up -d
```

## 2. Ejecutar

Linux/macOS:

```bash
./mvnw spring-boot:run
```

o con Maven instalado:

```bash
mvn spring-boot:run
```

Windows:

```powershell
mvn spring-boot:run
```

## 3. Resultado esperado

El archivo contiene 15 viajes.

- 2 son inválidos y se omiten mediante `SkipPolicy`.
- El viaje 7 simula un error temporal en el primer intento.
- El error temporal se recupera mediante `RetryPolicy`.
- Se esperan 13 registros persistidos.

El reporte queda en:

```text
output/resumen_viajes.csv
```

## 4. Consultar la base

```bash
docker exec -it translog-postgres psql -U translog -d translog
```

Luego:

```sql
SELECT * FROM trip_processed ORDER BY id;

SELECT
    step_name,
    status,
    read_count,
    write_count,
    process_skip_count
FROM batch_step_execution
ORDER BY step_execution_id;
```

## 5. Cambiar cantidad de particiones

En `application.properties`:

```properties
app.grid-size=3
app.pool-size=3
```

Prueba por ejemplo:

```properties
app.grid-size=5
app.pool-size=3
```

Así podrás observar que particiones y threads no son lo mismo.
