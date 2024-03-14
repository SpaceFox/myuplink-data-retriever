package fr.spacefox.myuplink.updater;

import fr.spacefox.myuplink.client.DevicesResource;
import fr.spacefox.myuplink.client.Point;
import fr.spacefox.myuplink.storage.InfluxDbRepository;
import fr.spacefox.myuplink.updater.model.Parameter;
import fr.spacefox.myuplink.updater.model.Value;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class Updater {
    private static final Logger LOG = Logger.getLogger(Updater.class);
    private static final double NS_IN_MS = 1_000_000.0;

    private final UpdaterConfig config;
    private final DevicesResource devicesResource;
    private final InfluxDbRepository repository;

    private Map<Parameter, Value> lastFetchData = new HashMap<>();
    private OffsetDateTime lastFetchTime;

    public Updater(UpdaterConfig config, @RestClient DevicesResource devicesResource, InfluxDbRepository repository) {
        this.config = config;
        this.devicesResource = devicesResource;
        this.repository = repository;
    }

    @Scheduled(every = "{myuplink.frequency}")
    public void update() {
        final var start = System.nanoTime();
        final var points =
                devicesResource.getPoints(config.deviceId(), config.locale().toLanguageTag());
        final var parameters = toParameters(points);
        lastFetchData = parameters;
        lastFetchTime = OffsetDateTime.now();
        repository.store(parameters);
        LOG.infof(
                "Retrieved and stored %d parameters in %f ms",
                parameters.size(), (System.nanoTime() - start) / NS_IN_MS);
    }

    private Map<Parameter, Value> toParameters(List<Point> points) {
        return points.stream()
                .collect(Collectors.toMap(
                        Parameter::from,
                        Value::from,
                        // Found 2 values for the same parameter in the source feed: only take the first.
                        (first, second) -> first));
    }

    public Map<Parameter, Value> getLastFetchData() {
        return lastFetchData;
    }

    public OffsetDateTime getLastFetchTime() {
        return lastFetchTime;
    }
}
