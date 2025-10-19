package com.thanlinardos.cloud_config_server;

import com.thanlinardos.spring_enterprise_library.time.TimeFactory;
import com.thanlinardos.spring_enterprise_library.time.api.TimeProvider;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy(false)
@Component
public class AppTimeFactory extends TimeFactory {

    /**
     * Constructor for AppTimeFactory.
     *
     * @param timeProvider the TimeProvider to use for getting the current date and time.
     */
    public AppTimeFactory(TimeProvider timeProvider) {
        super(timeProvider);
    }
}
