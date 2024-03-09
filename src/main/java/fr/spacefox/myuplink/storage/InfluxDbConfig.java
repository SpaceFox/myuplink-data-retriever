package fr.spacefox.myuplink.storage;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithConverter;

@ConfigMapping(prefix = "influx-db")
public interface InfluxDbConfig {

    String url();

    String org();

    String bucket();

    @WithConverter(CharArrayConverter.class)
    char[] token();
}
