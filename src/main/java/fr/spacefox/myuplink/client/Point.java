package fr.spacefox.myuplink.client;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record Point(
        String category,
        String parameterId,
        String parameterName,
        String parameterUnit,
        boolean writable,
        OffsetDateTime timestamp,
        BigDecimal value,
        String strVal,
        List<String> smartHomeCategories,
        BigDecimal minValue,
        BigDecimal maxValue,
        BigDecimal stepValue,
        List<EnumValue> enumValues,
        String scaledValue,
        String zoneId) {}
