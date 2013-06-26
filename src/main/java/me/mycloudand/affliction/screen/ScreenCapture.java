package me.mycloudand.affliction.screen;

import com.google.common.eventbus.EventBus;
import me.mycloudand.affliction.model.ScanRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Takes screenshots of a configured region.
 */
@Singleton
public class ScreenCapture implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ScreenCapture.class);

    @Inject
    private ScanRegion region;
    @Inject
    private FriendlyRobot robot;
    @Inject
    private EventBus eventBus;

    @Override
    public void run() {
        try {
            Rectangle captureRect = region.asRectangle();
            BufferedImage regionImage = robot.createScreenCapture(captureRect);
            log.trace("Screenshot captured.");
            eventBus.post(new CaptureScreenEvent(regionImage));
        } catch (Throwable t) {
            log.warn(null, t);
        }
    }
}
