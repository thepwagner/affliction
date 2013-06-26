package me.mycloudand.affliction;

import me.mycloudand.affliction.screen.ScreenCapture;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Singleton
public class AfflictionScheduler {
    @Inject
    private Provider<ScreenCapture> captureFactory;

    private final ScheduledExecutorService executor;
    private ScheduledFuture<?> captureFuture;

    public AfflictionScheduler() {
        executor = Executors.newScheduledThreadPool(8);
    }

    public void setPollInterval(int interval) {
        // Cancel running task (if any).
        if (captureFuture != null) {
            captureFuture.cancel(true);
            captureFuture = null;
        }

        if (interval > 0) {
            captureFuture = executor.scheduleWithFixedDelay(captureFactory.get(), interval, interval, TimeUnit.MILLISECONDS);
        }
    }

    public Long getPollInterval() {
        return (captureFuture == null || captureFuture.isCancelled()) ? 0 : captureFuture.getDelay(TimeUnit.MILLISECONDS);
    }
}
