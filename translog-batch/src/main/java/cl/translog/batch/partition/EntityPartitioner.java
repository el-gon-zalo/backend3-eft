package cl.translog.batch.partition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/* Como las 3 entidades tienen la misma cantidad de entradas (1000), podemos utilizar
esta misma clase Partitioner para las 3 entidades. En caso contrario, habría que clonar esta
clase Partitioner para cada una de las entidades */

@Component
public class EntityPartitioner implements Partitioner {

    private static final Logger log =
            LoggerFactory.getLogger(EntityPartitioner.class);

    private final int totalRecords;

    public EntityPartitioner(
            @Value("${app.total-records}") int totalRecords) {
        this.totalRecords = totalRecords;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {

        Map<String, ExecutionContext> partitions =
                new LinkedHashMap<>();

        int partitionSize =
                (int) Math.ceil((double) totalRecords / gridSize);

        int start = 0;

        for (int i = 0; i < gridSize && start < totalRecords; i++) {

            int end =
                    Math.min(start + partitionSize - 1, totalRecords - 1);

            ExecutionContext context =
                    new ExecutionContext();

            context.putInt("start", start);
            context.putInt("end", end);
            context.putString("partitionName", "partition" + i);

            partitions.put("partition" + i, context);

            log.info(
                    "Creada partition{} -> start={}, end={}",
                    i,
                    start,
                    end
            );

            start = end + 1;
        }

        return partitions;
    }
}
