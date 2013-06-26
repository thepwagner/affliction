package me.mycloudand.affliction.screen;

import com.google.common.eventbus.Subscribe;
import me.mycloudand.affliction.model.Pixel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;


@Singleton
public class AlertCloser {
    private static final Logger log = LoggerFactory.getLogger(AlertCloser.class);
    @Inject
    private FriendlyRobot robot;

    @Subscribe
    public void onScreenCapture(CaptureScreenEvent captureScreenEvent) {
        BufferedImage image = captureScreenEvent.getImage();

        int magicPixel = image.getRGB(538, 318);
        if (magicPixel == -4013374) {
            log.trace("Popup is open");
            robot.tap(new Pixel(538, 318));
        }
    }
}
