package me.mycloudand.affliction.resource;

import me.mycloudand.affliction.model.ScanRegion;
import me.mycloudand.affliction.screen.FriendlyRobot;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/camera")
public class CameraResource {
    @Inject
    private FriendlyRobot robot;
    @Inject
    private ScanRegion region;

    @GET
    public Response move(@QueryParam("dir") String dir) {
        if ("n".equalsIgnoreCase(dir)) {
            robot.drag(0, region.getH() / 2);
        } else if ("s".equalsIgnoreCase(dir)) {
            robot.drag(0, region.getH() / -2);
        } else if ("e".equalsIgnoreCase(dir)) {
            robot.drag(region.getW() / -2, 0);
        } else if ("w".equalsIgnoreCase(dir)) {
            robot.drag(region.getW() / 2, 0);
        }

        return Response.ok(dir)
                .build();
    }
}
