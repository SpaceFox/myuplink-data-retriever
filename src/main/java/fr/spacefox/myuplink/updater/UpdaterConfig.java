package fr.spacefox.myuplink.updater;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

import java.util.Locale;

@ConfigMapping(prefix = "myuplink")
public interface UpdaterConfig {

    String deviceId();

    Locale locale();

    // NOT a Duration to be able to set it to "off" (for test purposes, this would have no meaning)
    @WithDefault("PT1M")
    String frequency();
}
