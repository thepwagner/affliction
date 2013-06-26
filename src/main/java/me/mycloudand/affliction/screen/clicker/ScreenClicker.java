package me.mycloudand.affliction.screen.clicker;

import me.mycloudand.affliction.TargetBuffer;
import me.mycloudand.affliction.model.Pixel;
import me.mycloudand.affliction.model.ScanRegion;
import me.mycloudand.affliction.screen.FriendlyRobot;
import me.mycloudand.affliction.screen.clickable.ClickableTargetsEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.image.BufferedImage;

import static me.mycloudand.affliction.screen.clickable.PixelMatcher.isHotPixel;

/**
 * Drives a {@link me.mycloudand.affliction.screen.ScreenCapture} at some interval.
 */
@Singleton
public class ScreenClicker {
    private static final Logger log = LoggerFactory.getLogger(ScreenClicker.class);

    @Inject
    private TargetBuffer targetBuffer;
    @Inject
    private FriendlyRobot robot;
    @Inject
    private ScanRegion region;

    public synchronized void onClickableTargetsEvent(ClickableTargetsEvent clickableTargetsEvent) {
        long clickStart = System.currentTimeMillis();

        for (Pixel clickTarget : clickableTargetsEvent.getTargets()) {
            if (!targetBuffer.getTargets().contains(clickTarget)) {
                continue;
            }

            Pixel confirmedTarget = confirmPixel(clickTarget);
            if (confirmedTarget != null) {
                for (int i = 0; confirmedTarget != null && i < 5; i++) {
                    robot.tap(confirmedTarget);
                    try {
                        Thread.sleep(300);
                    } catch (Exception e) {
                        log.warn(null, e);
                    }
                    confirmedTarget = confirmPixel(clickTarget);
                }
            }
            targetBuffer.removeTarget(clickTarget);
        }

        long clickTimer = System.currentTimeMillis() - clickStart;
        log.info("Processed {} targets in {}ms", clickableTargetsEvent.getTargets().size(), clickTimer);
    }

    private Pixel confirmPixel(Pixel clickTarget) {
        int sampleSize = 4;
        int halfSampleSize = sampleSize / 2;

        log.trace("Considering {},{}", clickTarget.getX(), clickTarget.getY());
        int sampleOffsetX = clickTarget.getX() - halfSampleSize;
        int sampleOffsetY = clickTarget.getY() - halfSampleSize;
        Rectangle sampleRect = new Rectangle(region.getX() + sampleOffsetX, region.getY() + sampleOffsetY, sampleSize, sampleSize);
        BufferedImage imageSample = robot.createScreenCapture(sampleRect);

        // Search the sample for our magic color:
        for (int x = 0; x < imageSample.getWidth(); x++) {
            for (int y = 0; y < imageSample.getHeight(); y++) {
                int rgb = imageSample.getRGB(x, y);
                if (isHotPixel(rgb)) {
                    return new Pixel(sampleOffsetX + x, sampleOffsetY + y);
                }
            }
        }
        return null;
    }
}
