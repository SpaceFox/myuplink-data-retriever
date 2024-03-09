package fr.spacefox.myuplink.updater.model;

import fr.spacefox.myuplink.client.Point;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record Value(OffsetDateTime time, BigDecimal value, String strVal) {
    public static Value from(Point point) {
        return new Value(point.timestamp(), point.value(), point.strVal());
    }
}
