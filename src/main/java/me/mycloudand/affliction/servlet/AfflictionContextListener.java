package me.mycloudand.affliction.servlet;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;

public class AfflictionContextListener extends GuiceServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(AfflictionContextListener.class);
    private static final ImmutableMap<String, String> SERVLET_CONFIG = ImmutableMap.of("com.sun.jersey.api.json.POJOMappingFeature", "true");

    static {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                log.warn(null, e);
            }
        });
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        super.contextInitialized(servletContextEvent);
        log.info("Startup.");
    }

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new JerseyServletModule() {
            @Override
            protected void configureServlets() {
                install(new AfflictionModule());
                serve("/*")
                        .with(GuiceContainer.class, SERVLET_CONFIG);
            }
        });
    }
}
