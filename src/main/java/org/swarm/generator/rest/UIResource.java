package org.swarm.generator.rest;

import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Created by bob on 8/25/17.
 */
@ApplicationScoped
@Path("/")
public class UIResource {

    @GET
    @Path("/")
    public Response redirect() {
        return Response.temporaryRedirect(URI.create("http://wildfly-swarm.io/generator/")).build();
    }
}
