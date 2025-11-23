package fr.spacefox.myuplink.client;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

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
        String zoneId) {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0");

    public boolean isEnum() {
        return enumValues != null && !enumValues.isEmpty();
    }

    public String cleanedStrVal() {
        if (isEnum()) {
            final var formattedValue = DECIMAL_FORMAT.format(value);
            return enumValues.stream()
                    .filter(enumValue -> Objects.equals(enumValue.value(), formattedValue))
                    .map(EnumValue::text)
                    .findAny()
                    .orElse(strVal());
        }
        return strVal();
    }
}
