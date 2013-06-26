package me.mycloudand.affliction.servlet;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import me.mycloudand.affliction.model.ScanRegion;
import me.mycloudand.affliction.resource.*;
import me.mycloudand.affliction.screen.AlertCloser;
import me.mycloudand.affliction.screen.clickable.PixelBuffer;
import me.mycloudand.affliction.screen.clicker.ScreenClicker;

import java.util.concurrent.Executors;

public class AfflictionModule extends AbstractModule {
    public static final String RAW_IMAGE = "raw-image";

    private final EventBus eventBus = new AsyncEventBus("Default EventBus", Executors.newCachedThreadPool());

    @Override
    public void configure() {
        bind(EventBus.class)
                .toInstance(eventBus);

        // Auto register all instances with the eventBus:
        bindListener(Matchers.any(), new TypeListener() {
            public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
                typeEncounter.register(new InjectionListener<I>() {
                    public void afterInjection(I i) {
                        eventBus.register(i);
                    }
                });
            }
        });

        bind(SchedulerResource.class);
        bind(ScreenWatcherResource.class);
        bind(PixelBufferResource.class);
        bind(TargetBufferResource.class);
        bind(CameraResource.class);

        bind(PixelBuffer.class)
                .asEagerSingleton();
        bind(ScreenClicker.class)
                .asEagerSingleton();
        bind(AlertCloser.class)
                .asEagerSingleton();

        // Magic values on my machine, deal with it:
        ScanRegion scanRegion = new ScanRegion();
        scanRegion.setX(35);
        scanRegion.setY(45);
        scanRegion.setW(1600);
        scanRegion.setH(850);

        bind(ScanRegion.class)
                .toInstance(scanRegion);
    }
}