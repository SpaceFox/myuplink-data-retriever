package fr.spacefox.myuplink.updater.model;

import fr.spacefox.myuplink.client.EnumValue;
import fr.spacefox.myuplink.client.Point;

import java.util.List;

public record Parameter(String id, String name, String unit, List<EnumValue> enumValues) {
    public static Parameter from(Point point) {
        return new Parameter(point.parameterId(), point.parameterName(), point.parameterUnit(), point.enumValues());
    }
}
