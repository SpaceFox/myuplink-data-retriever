package fr.spacefox.myuplink.pages;

import fr.spacefox.myuplink.updater.Updater;
import fr.spacefox.myuplink.updater.model.Parameter;
import fr.spacefox.myuplink.updater.model.Value;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/")
public class HttpPages {

    private final PageConfig pageConfig;
    private final Template index;
    private final Updater updater;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.FRANCE);

    public HttpPages(PageConfig pageConfig, Template index, Updater updater) {
        this.pageConfig = pageConfig;
        this.index = index;
        this.updater = updater;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance home() {
        return index.data(
                "updateDate",
                dateFormatter.format(updater.getLastFetchTime()),
                "statuses",
                toStatuses(toMeasuresByCodes(updater.getLastFetchData())));
    }

    private Map<String, Measure> toMeasuresByCodes(Map<Parameter, Value> lastFetchData) {
        return lastFetchData.entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey().id(), this::toMeasure));
    }

    private Measure toMeasure(Map.Entry<Parameter, Value> d) {
        final var key = d.getKey();
        final var value = d.getValue();
        return new Measure(
                key.name(), value.strVal(), toIcon(value.time()), dateFormatter.format(value.time()), key.id());
    }

    private List<StatusTable> toStatuses(Map<String, Measure> measures) {
        final var configuredStatuses = pageConfig.statuses().stream()
                .map(status -> new StatusTable(
                        status.name(),
                        status.codes().stream().map(measures::get).toList()))
                .collect(Collectors.toCollection(ArrayList::new));
        if (pageConfig.displayAdditionalStatuses()) {
            final var configuredIds = pageConfig.statuses().stream()
                    .flatMap(statusType -> statusType.codes().stream())
                    .collect(Collectors.toSet());
            final var additionalCodes = new ArrayList<>(measures.keySet());
            additionalCodes.removeAll(configuredIds);
            configuredStatuses.add(new StatusTable(
                    "Autres mesures",
                    additionalCodes.stream().map(measures::get).toList()));
        }
        return configuredStatuses;
    }

    private String toIcon(ZonedDateTime time) {
        final var now = ZonedDateTime.now();
        if (time.isBefore(now.minusDays(1))) {
            return "❌";
        }
        if (time.isBefore(now.minusHours(1))) {
            return "⚠️";
        }
        if (time.isBefore(now.minusMinutes(10))) {
            return "☑️";
        }
        return "✅";
    }
}
