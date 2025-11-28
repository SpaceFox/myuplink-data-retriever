package fr.spacefox.myuplink.updater.model;

import fr.spacefox.myuplink.client.Point;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public record Value(ZonedDateTime time, BigDecimal value, String strVal) {
    public static Value from(Point point) {
        return new Value(
                point.timestamp().toInstant().atZone(ZoneId.of("Europe/Paris")), point.value(), point.cleanedStrVal());
    }
}
