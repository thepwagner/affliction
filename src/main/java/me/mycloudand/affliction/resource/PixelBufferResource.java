package me.mycloudand.affliction.resource;

import me.mycloudand.affliction.model.Pixel;
import me.mycloudand.affliction.screen.ScreenCaptureBuffer;
import me.mycloudand.affliction.screen.clickable.PixelBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.awt.image.BufferedImage;
import java.util.Set;

@Path("/pixels")
public class PixelBufferResource {
    private static final Logger log = LoggerFactory.getLogger(PixelBufferResource.class);

    private static final int COLOUR_HOT_PIXEL = 0xFF0000;

    @Inject
    private ScreenCaptureBuffer screenCaptureBuffer;
    @Inject
    private PixelBuffer pixelBuffer;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Set<Pixel> points() {
        return pixelBuffer.getGlobalPixels();
    }

    @GET
    @Produces("image/png")
    @Path("/image")
    public BufferedImage markedUpImage() {
        BufferedImage image = screenCaptureBuffer.getLastImage();
        Set<Pixel> hotPixels = pixelBuffer.getGlobalPixels();
        for (Pixel hotPixel : hotPixels) {
            image.setRGB(hotPixel.getX(), hotPixel.getY(), COLOUR_HOT_PIXEL);
        }
        return image;
    }
}
