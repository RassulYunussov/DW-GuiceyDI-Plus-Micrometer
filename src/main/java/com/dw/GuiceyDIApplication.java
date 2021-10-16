package com.dw;

import com.dw.modules.MetricsInstrumentationModule;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import ru.vyarus.dropwizard.guice.GuiceBundle;

public class GuiceyDIApplication extends Application<GuiceyDIConfiguration> {

    public static void main(final String[] args) throws Exception {
        new GuiceyDIApplication().run(args);
    }

    @Override
    public String getName() {
        return "GuiceyDI";
    }

    @Override
    public void initialize(final Bootstrap<GuiceyDIConfiguration> bootstrap) {
        // TODO: application initialization
        bootstrap.addBundle(GuiceBundle.builder()
                .printAllGuiceBindings()
                .enableAutoConfig(getClass().getPackage().getName())
                .modules(new MetricsInstrumentationModule())
                .build());
    }

    @Override
    public void run(final GuiceyDIConfiguration configuration,
                    final Environment environment) {
        // TODO: implement application
    }

}
