package com.dw.modules;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.dw.modules.interceptors.TimedInterceptor;
import io.micrometer.atlas.AtlasMeterRegistry;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Metrics;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MetricsInstrumentationModule extends AbstractModule {
    @Override
    protected void configure() {
        super.configure();
        var amr = new AtlasMeterRegistry(s -> null, Clock.SYSTEM);
        Metrics.addRegistry(amr);
        log.info("Configuration Metrics Instrumentation Module");
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Timed.class), new TimedInterceptor(amr));
    }
}
