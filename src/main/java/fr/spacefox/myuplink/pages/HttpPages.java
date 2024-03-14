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

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Path("/")
public class HttpPages {

    private static final List<String> ID_TO_KEEP = List.of(
            "40025", "40026", "49993", "43125", "40014", "40008", "40012", "40017", "40940", "40013", "43081", "41778",
            "43123", "43122", "40022", "40018", "43146", "40019", "50004", "49995", "49994", "43124", "43427", "40033",
            "40067", "40004", "43140", "43009", "40050", "50005", "43437", "40020");

    private final Template index;
    private final Updater updater;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.FRANCE);

    public HttpPages(Template index, Updater updater) {
        this.index = index;
        this.updater = updater;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance home() {
        return index.data(
                "updateDate",
                dateFormatter.format(updater.getLastFetchTime()),
                "measures",
                toMeasures(updater.getLastFetchData()));
    }

    private List<Measure> toMeasures(Map<Parameter, Value> data) {
        return data.entrySet().stream()
                .filter(d -> ID_TO_KEEP.contains(d.getKey().id()))
                .map(d -> {
                    final var value = d.getValue();
                    return new Measure(
                            d.getKey().name(),
                            value.strVal(),
                            toIcon(value.time()),
                            dateFormatter.format(value.time()));
                })
                .sorted(Comparator.comparing(Measure::title))
                .toList();
    }

    private String toIcon(OffsetDateTime time) {
        final var now = OffsetDateTime.now();
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
