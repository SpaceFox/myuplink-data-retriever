package fr.spacefox.myuplink.pages;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import java.util.List;

@ConfigMapping(prefix = "page")
public interface PageConfig {

    List<StatusType> statuses();

    @WithDefault("false")
    boolean displayAdditionalStatuses();

    interface StatusType {
        String name();

        List<String> codes();
    }
}
