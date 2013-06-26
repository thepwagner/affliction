package me.mycloudand.affliction.resource;

import me.mycloudand.affliction.TargetBuffer;
import me.mycloudand.affliction.model.Pixel;
import me.mycloudand.affliction.screen.ScreenCaptureBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Set;

@Path("/targets")
public class TargetBufferResource {
    private static final Logger log = LoggerFactory.getLogger(TargetBufferResource.class);

    @Inject
    private ScreenCaptureBuffer screenCaptureBuffer;
    @Inject
    private TargetBuffer targetBuffer;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Set<Pixel> points() {
        return targetBuffer.getTargets();
    }

    @GET
    @Produces("image/png")
    @Path("/image")
    public BufferedImage markedUpImage() {
        BufferedImage image = screenCaptureBuffer.getLastImage();
        Set<Pixel> targets = targetBuffer.getTargets();
        log.trace("Painting {} points.", targets.size());

        Graphics2D graphics = (Graphics2D) image.getGraphics();

        for (Pixel hotPixel : targets) {
            int x = hotPixel.getX();
            int y = hotPixel.getY();

            graphics.setColor(Color.GREEN);
            graphics.drawOval(x - 20, y - 20, 40, 40);
        }
        return image;
    }
}
