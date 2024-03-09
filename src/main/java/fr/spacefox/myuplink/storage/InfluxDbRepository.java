package fr.spacefox.myuplink.storage;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.client.write.events.WriteErrorEvent;
import com.influxdb.client.write.events.WriteSuccessEvent;
import fr.spacefox.myuplink.updater.model.Parameter;
import fr.spacefox.myuplink.updater.model.Value;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Map;

@ApplicationScoped
public class InfluxDbRepository {
    private static final Logger LOG = Logger.getLogger(InfluxDbRepository.class);

    private final InfluxDbConfig config;
    private InfluxDBClient client;
    private WriteApi writeApi;

    public InfluxDbRepository(InfluxDbConfig config) {
        this.config = config;
    }

    @PostConstruct
    void init() {
        LOG.debugf("Creating InfluxDB Client %s/%s/%s", config.url(), config.org(), config.bucket());
        client = InfluxDBClientFactory.create(config.url(), config.token(), config.org(), config.bucket());
        writeApi = client.makeWriteApi();
        writeApi.listenEvents(WriteSuccessEvent.class, WriteSuccessEvent::logEvent);
        writeApi.listenEvents(WriteErrorEvent.class, WriteErrorEvent::logEvent);
        LOG.infof("InfluxDB Client %s/%s/%s successfully created", config.url(), config.org(), config.bucket());
    }

    @PreDestroy
    void tearDown() {
        LOG.debugf("Closing InfluxDB Client %s/%s/%s", config.url(), config.org(), config.bucket());
        writeApi.close();
        client.close();
        LOG.infof("InfluxDB Client %s/%s/%s successfully closed", config.url(), config.org(), config.bucket());
    }

    public void store(Map<Parameter, Value> parameters) {
        writeApi.writePoints(toPoints(parameters));
    }

    private List<Point> toPoints(Map<Parameter, Value> parameters) {
        return parameters.entrySet().stream().map(this::toPoint).toList();
    }

    private Point toPoint(Map.Entry<Parameter, Value> entry) {
        final var parameter = entry.getKey();
        final var value = entry.getValue();
        final var point = Point.measurement(parameter.id())
                .addTag("name", parameter.name())
                .addTag("unit", parameter.unit())
                .addField("value", value.value())
                .time(value.time().toEpochSecond(), WritePrecision.S);
        if (!parameter.enumValues().isEmpty()) {
            // Use the mapping done by the API in strVal
            point.addField("enumVal", value.strVal());
        }
        return point;
    }
}
