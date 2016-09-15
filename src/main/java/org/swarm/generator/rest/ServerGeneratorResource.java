package org.swarm.generator.rest;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.Size;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.wildfly.swarm.arquillian.resolver.ShrinkwrapArtifactResolvingHelper;
import org.wildfly.swarm.spi.api.JARArchive;
import org.wildfly.swarm.tools.ArtifactSpec;
import org.wildfly.swarm.tools.BuildTool;

/**
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@ApplicationScoped
@Path("/server")
public class ServerGeneratorResource {

    @GET
    @Produces("application/java-archive")
    public Response generate(
            @QueryParam("n") @DefaultValue("server") String serverName,
            @QueryParam("d") @Size(min = 1) List<String> dependencies)
            throws Exception {
        // Sort dependencies
        Collections.sort(dependencies);

        // Setup BuildTool
        BuildTool buildTool = getBuildTool();
        String version = getVersion();

        dependencies.stream().filter(d -> !d.isEmpty()).forEach(d -> {
            buildTool.fraction(ArtifactSpec.fromMscGav("org.wildfly.swarm:" + d + ":" + version));
        });
        // TODO: Cache this? Maybe write to a temporary file and return the file contents when asked.
        // Export to byte array and return
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        buildTool.build().as(ZipExporter.class).exportTo(baos);
        return Response
                .ok(baos.toByteArray())
                .type("application/java-archive")
                .header("Content-Disposition", "attachment; filename=\"" + serverName + ".jar\"")
                .build();
    }

    private BuildTool getBuildTool() {
        BuildTool buildTool = new BuildTool().hollow(true);
        buildTool.projectArchive(ShrinkWrap.create(JARArchive.class, "server.jar").add(EmptyAsset.INSTANCE, "empty.txt"));
        buildTool.fractionList(org.wildfly.swarm.fractionlist.FractionList.get());
        buildTool.artifactResolvingHelper(ShrinkwrapArtifactResolvingHelper.defaultInstance());
        return buildTool;
    }

    private String getVersion() {
        return BuildTool.class.getPackage().getImplementationVersion();
    }
}
