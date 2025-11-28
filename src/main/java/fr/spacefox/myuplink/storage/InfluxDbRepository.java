package fr.spacefox.myuplink.storage;

import com.influxdb.v3.client.InfluxDBClient;
import com.influxdb.v3.client.Point;
import com.influxdb.v3.client.write.WritePrecision;
import fr.spacefox.myuplink.updater.model.Parameter;
import fr.spacefox.myuplink.updater.model.Value;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;
import org.jboss.logging.Logger;

@ApplicationScoped
public class InfluxDbRepository {
    private static final Logger LOG = Logger.getLogger(InfluxDbRepository.class);

    private final InfluxDbConfig config;
    private InfluxDBClient client;

    public InfluxDbRepository(InfluxDbConfig config) {
        this.config = config;
    }

    @PostConstruct
    void init() {
        LOG.debugf("Creating InfluxDB Client %s/%s", config.host(), config.database());
        client = InfluxDBClient.getInstance(config.host(), config.token(), config.database());
        LOG.infof("InfluxDB Client %s/%s successfully created", config.host(), config.database());
    }

    @PreDestroy
    void tearDown() {
        LOG.debugf("Closing InfluxDB Client %s/%s", config.host(), config.database());
        try {
            client.close();
            LOG.infof("InfluxDB Client %s/%s/%s successfully closed", config.host(), config.database());
        } catch (Exception e) {
            LOG.errorf(
                    e,
                    "Error on closing InfluxDB Client %s/%s/%s: %s",
                    config.host(),
                    config.database(),
                    e.getMessage());
        }
    }

    public void store(Map<Parameter, Value> parameters) {
        client.writePoints(toPoints(parameters));
    }

    private List<Point> toPoints(Map<Parameter, Value> parameters) {
        return parameters.entrySet().stream().map(this::toPoint).toList();
    }

    private Point toPoint(Map.Entry<Parameter, Value> entry) {
        final var parameter = entry.getKey();
        final var value = entry.getValue();
        final var point = Point.measurement(parameter.id())
                .setTag("name", parameter.name())
                .setTag("unit", parameter.unit())
                .setField("value", value.value())
                .setTimestamp(value.time().toEpochSecond(), WritePrecision.S);
        if (!parameter.enumValues().isEmpty()) {
            // Use the mapping done by the API in strVal
            point.setField("enumVal", value.strVal());
        }
        return point;
    }
}
