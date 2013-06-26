package me.mycloudand.affliction.screen;


import java.awt.image.BufferedImage;

/**
 * The screen was captured.
 */
public class CaptureScreenEvent {
    private final BufferedImage image;

    public CaptureScreenEvent(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }
}
