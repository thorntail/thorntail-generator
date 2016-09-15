package org.swarm.generator.rest;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@ApplicationScoped
@Path("/generator")
public class ProjectGeneratorResource {
    private static final String WILDFLY_SWARM_VERSION = "2016.9";

    TemplateEngine engine;

    public ProjectGeneratorResource() {
        engine = new TemplateEngine();
        engine.setTemplateResolver(new ClassLoaderTemplateResolver(getClass().getClassLoader()));
    }

    @GET
    @Produces("application/zip")
    public Response generate(
            @QueryParam("g") @NotNull(message = "Parameter 'g' (Group Id) must not be null") String groupId,
            @QueryParam("a") @NotNull(message = "Parameter 'a' (Artifact Id) must not be null") String artifactId,
            @QueryParam("dep") List<String> dependencies)
            throws Exception {
        Context context = new Context();
        context.setVariable("groupId", groupId);
        context.setVariable("artifactId", artifactId);
        context.setVariable("dependencies", dependencies);
        context.setVariable("swarmVersion", WILDFLY_SWARM_VERSION);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            zos.putNextEntry(new ZipEntry(artifactId + "/src/main/java/"));
            zos.closeEntry();

            zos.putNextEntry(new ZipEntry(artifactId + "/pom.xml"));
            zos.write(engine.process("templates/pom.tl.xml", context).getBytes());
            zos.closeEntry();

            if (enableJAXRS(dependencies)) {
                zos.putNextEntry(new ZipEntry(artifactId + "/src/main/java/com/example/rest/HelloWorldEndpoint.java"));
                zos.write(engine.process("templates/HelloWorldEndpoint.tl.java", context).getBytes());
                zos.closeEntry();
            }
        }

        return Response
                .ok(baos.toByteArray())
                .type("application/zip")
                .header("Content-Disposition", "attachment; filename=\"" + artifactId + ".zip\"")
                .build();
    }

    private boolean enableJAXRS(List<String> dependencies) {
        if (dependencies == null || dependencies.size() == 0) {
            return true;
        }
        return dependencies.stream().anyMatch(d -> d.contains("jaxrs") || d.contains("microprofile"));
    }

}
