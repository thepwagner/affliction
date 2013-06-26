package me.mycloudand.affliction.screen;

import me.mycloudand.affliction.model.Pixel;
import me.mycloudand.affliction.model.ScanRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: pwagner
 * Date: 19/06/13
 * Time: 6:40 AM
 * To change this template use File | Settings | File Templates.
 */
@Singleton
public class FriendlyRobot {
    private static final Logger log = LoggerFactory.getLogger(FriendlyRobot.class);

    @Inject
    private Random rand;
    @Inject
    private ScanRegion region;

    private final Robot robot;

    public FriendlyRobot() {
        try {
            robot = new Robot(findScreen());
        } catch (AWTException e) {
            log.warn(null, e);
            throw new IllegalStateException(e);
        }
    }

    public synchronized BufferedImage createScreenCapture(Rectangle captureRect) {
        return robot.createScreenCapture(captureRect);
    }

    public synchronized void tap(Pixel clickTarget) {
        robot.mouseMove(region.getX() + clickTarget.getX(), region.getX() + clickTarget.getY());
        robot.delay(rand.nextInt(10) + 10);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.delay(rand.nextInt(5) + 20);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        robot.delay(200);
    }


    private GraphicsDevice findScreen() {
        GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        if (devices.length == 1) {
            return devices[0];
        }

        // For my personal multimonitor setup; deal with it.
        for (GraphicsDevice screenDevice : devices) {
            GraphicsConfiguration screenConfiguration = screenDevice.getDefaultConfiguration();
            Rectangle screenBounds = screenConfiguration.getBounds();
            if (screenBounds.getWidth() == 1680) {
                return screenDevice;
            }
        }
        return null;
    }


    public synchronized void drag(int x, int y) {

        safeSleep(100);
        int midX = region.getX() + (region.getW() / 2);
        int midY = region.getY() + (region.getH() / 2);
        log.trace("Centering to {},{}", midX, midY);

        robot.mouseMove(midX, midX);
        robot.delay(rand.nextInt(10) + 10);
        robot.mousePress(InputEvent.BUTTON1_MASK);

        int endX = midX + x;
        int endY = midY + y;

        int steps = 200;
        for (int i = 0; i < steps; i++) {
            int ix = ((endX * i) / steps) + (midX * (steps - i) / steps);
            int iy = ((endY * i) / steps) + (midY * (steps - i) / steps);

            robot.mouseMove(ix, iy);
            safeSleep(1);
        }
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        safeSleep(200);
    }

    private void safeSleep(long l) {
        try {
            Thread.sleep(l);
        } catch (Exception e) {
            log.warn(null, e);
        }
    }
}
