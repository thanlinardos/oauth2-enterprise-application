package com.thanlinardos.resource_server.environment;

import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.context.refresh.ConfigDataContextRefresher;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import java.util.*;

public class CustomConfigDataContextRefresher extends ConfigDataContextRefresher {

    private final boolean displayValues;

    public CustomConfigDataContextRefresher(ConfigurableApplicationContext context,
                                            RefreshScope scope,
                                            RefreshAutoConfiguration.RefreshProperties properties,
                                            boolean displayValues) {
        super(context, scope, properties);
        this.displayValues = displayValues;
    }

    public synchronized Object customRefresh() {
        Object changes = displayValues ? refreshEnvironmentAndFetchPropertyValueMap() : refreshEnvironment();
        this.getScope().refreshAll();
        return changes;
    }

    public synchronized Map<String, Object> refreshEnvironmentAndFetchPropertyValueMap() {
        Map<String, Object> before = extract(this.getContext().getEnvironment().getPropertySources());
        updateEnvironment();
        Map<String, Object> changes = changes(before, extract(this.getContext().getEnvironment().getPropertySources()));
        this.getContext().publishEvent(new EnvironmentChangeEvent(this.getContext(), changes.keySet()));
        return changes;
    }

    private Map<String, Object> changes(Map<String, Object> before, Map<String, Object> after) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : before.entrySet()) {
            if (!after.containsKey(entry.getKey())) {
                result.put(entry.getKey(), null);
            } else if (!Objects.equals(entry.getValue(), after.get(entry.getKey()))) {
                result.put(entry.getKey(), after.get(entry.getKey()));
            }
        }
        for (Map.Entry<String, Object> entry : after.entrySet()) {
            if (!before.containsKey(entry.getKey())) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    private Map<String, Object> extract(MutablePropertySources propertySources) {
        Map<String, Object> result = new HashMap<>();
        List<PropertySource<?>> sources = new ArrayList<>();
        for (PropertySource<?> source : propertySources) {
            sources.addFirst(source);
        }
        for (PropertySource<?> source : sources) {
            if (!this.standardSources.contains(source.getName())) {
                extract(source, result);
            }
        }
        return result;
    }

    private void extract(PropertySource<?> parent, Map<String, Object> result) {
        if (parent instanceof CompositePropertySource propertySource) {
            try {
                List<PropertySource<?>> sources = new ArrayList<>();
                for (PropertySource<?> source : propertySource.getPropertySources()) {
                    sources.addFirst(source);
                }
                for (PropertySource<?> source : sources) {
                    extract(source, result);
                }
            }
            catch (Exception ignored) {
                // Ignore exceptions that may occur while extracting properties from composite sources
            }
        } else if (parent instanceof EnumerablePropertySource<?> propertySource) {
            for (String key : propertySource.getPropertyNames()) {
                result.put(key, parent.getProperty(key));
            }
        }
    }
}
