package com.example.controller;

import java.util.HashMap;
import java.util.Map;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/actuator/health")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HealthCheckController {

   @GET
public Response health() {
    try {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", System.currentTimeMillis());


        return Response.ok(status).build();
    } catch (Exception e) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", "DOWN");
        error.put("timestamp", System.currentTimeMillis());
        error.put("error", e.getMessage());
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                       .entity(error)
                       .build();
    }
}

}
