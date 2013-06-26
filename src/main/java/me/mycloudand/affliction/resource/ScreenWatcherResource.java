package me.mycloudand.affliction.resource;


import me.mycloudand.affliction.model.ScanRegion;
import me.mycloudand.affliction.screen.ScreenCapture;
import me.mycloudand.affliction.screen.ScreenCaptureBuffer;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.awt.image.BufferedImage;

@Path("/screen")
@Produces(MediaType.APPLICATION_JSON)
public class ScreenWatcherResource {
    @Inject
    private ScanRegion scanRegion;
    @Inject
    private ScreenCapture scanner;
    @Inject
    private ScreenCaptureBuffer captureBuffer;

    @GET
    public ScanRegion status() {
        return scanRegion;
    }


    @GET
    @Path("/region")
    public ScanRegion region(
            @QueryParam("x") Integer x,
            @QueryParam("y") Integer y,
            @QueryParam("w") Integer w,
            @QueryParam("h") Integer h
    ) {
        if (x != null) {
            scanRegion.setX(x);
        }
        if (y != null) {
            scanRegion.setY(y);
        }
        if (w != null) {
            scanRegion.setW(w);
        }
        if (h != null) {
            scanRegion.setH(h);
        }
        return status();
    }

    private static Integer toInteger(Integer value, Double d) {
        return value != null ? value : d.intValue();
    }

    @GET
    @Path("/image")
    @Produces("image/png")
    public BufferedImage images() {
        System.out.println(captureBuffer);
        return captureBuffer.getLastImage();
    }
}
