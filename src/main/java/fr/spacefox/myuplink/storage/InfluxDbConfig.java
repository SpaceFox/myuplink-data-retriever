package fr.spacefox.myuplink.storage;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithConverter;

@ConfigMapping(prefix = "influx-db")
public interface InfluxDbConfig {

    String host();

    String database();

    @WithConverter(CharArrayConverter.class)
    char[] token();
}
