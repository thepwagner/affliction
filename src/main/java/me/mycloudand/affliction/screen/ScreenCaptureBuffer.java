package me.mycloudand.affliction.screen;


import com.google.common.eventbus.Subscribe;

import javax.inject.Singleton;
import java.awt.image.BufferedImage;

/**
 *
 */
@Singleton
public class ScreenCaptureBuffer {
    private BufferedImage lastImage;

    @Subscribe
    public synchronized void onScreenCapture(CaptureScreenEvent captureEvent) {
        this.lastImage = captureEvent.getImage();
    }

    public synchronized BufferedImage getLastImage() {
        return lastImage;
    }
}
