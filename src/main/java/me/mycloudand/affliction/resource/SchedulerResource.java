package me.mycloudand.affliction.resource;


import me.mycloudand.affliction.AfflictionScheduler;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Path("/schedule")
@Produces(MediaType.APPLICATION_JSON)
public class SchedulerResource {
    @Inject
    private AfflictionScheduler scheduler;

    @GET
    public Map<String, String> status(@QueryParam("screen") Integer screenInterval) {
        if (screenInterval != null) {
            scheduler.setPollInterval(screenInterval);
        }

        Map<String, String> status = new HashMap<String, String>();
        status.put("screen", String.valueOf(scheduler.getPollInterval()));
        return status;
    }
}
