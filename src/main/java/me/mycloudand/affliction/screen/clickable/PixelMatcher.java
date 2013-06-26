package me.mycloudand.affliction.screen.clickable;

import com.google.common.collect.Maps;
import me.mycloudand.affliction.model.Pixel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * Scans the raw image for the pixels we find interesting.
 */
@Singleton
public class PixelMatcher {
    private static final Logger log = LoggerFactory.getLogger(PixelMatcher.class);

    private static final int SCAN_RESOLUTION_X = 2;
    private static final int SCAN_RESOLUTION_Y = 2;

    private static final int BUILDING_DOLLAR_SIGN = -79053;
    private static final int CHECK_ON_RESTAURANT = -13421773;
    private static final int CASH_REGISTER_ON_RESTAURANT = -4565709;

    public Map<Pixel, Integer> scanPixels(BufferedImage image) {
        Map<Pixel, Integer> hotPixels = Maps.newHashMap();

        long scanStart = System.currentTimeMillis();
        for (int x = 0; x < image.getWidth(); x += SCAN_RESOLUTION_X) {
            for (int y = 0; y < image.getHeight(); y += SCAN_RESOLUTION_Y) {
                int pixelRgb = image.getRGB(x, y);
                if (isHotPixel(pixelRgb)) {
                    Pixel hotPixel = new Pixel(x, y);
                    hotPixels.put(hotPixel, pixelRgb);
                }
            }
        }
        long scanEnd = System.currentTimeMillis();
        long scanTimer = scanEnd - scanStart;
        log.trace("Scanned screenshot in {}ms, found {} pixels", scanTimer, hotPixels.size());
        return hotPixels;
    }

    public static boolean isHotPixel(int rgbColor) {
        return rgbColor == BUILDING_DOLLAR_SIGN || rgbColor == CHECK_ON_RESTAURANT || rgbColor == CASH_REGISTER_ON_RESTAURANT;
    }
}
