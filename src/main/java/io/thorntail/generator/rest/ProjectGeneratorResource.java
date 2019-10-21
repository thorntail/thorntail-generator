package io.thorntail.generator.rest;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@ApplicationScoped
@Path("/generator")
public class ProjectGeneratorResource {
    private static final String THORNTAIL_VERSION = "2.5.0.Final";

    private TemplateEngine engine;

    public ProjectGeneratorResource() {
        engine = new TemplateEngine();

        ClassLoaderTemplateResolver textTemplateResolver = new ClassLoaderTemplateResolver(getClass().getClassLoader());
        textTemplateResolver.setTemplateMode(TemplateMode.TEXT);
        Set<String> javaResolvablePattern = new HashSet<>(Arrays.asList("*.java"));
        textTemplateResolver.setResolvablePatterns(javaResolvablePattern);

        ClassLoaderTemplateResolver xmlTemplateResolver = new ClassLoaderTemplateResolver(getClass().getClassLoader());
        xmlTemplateResolver.setTemplateMode(TemplateMode.XML);
        Set<String> xmlResolvablePattern = new HashSet<>(Arrays.asList("*.xml"));
        xmlTemplateResolver.setResolvablePatterns(xmlResolvablePattern);

        engine.addTemplateResolver(xmlTemplateResolver);
        engine.addTemplateResolver(textTemplateResolver);

        engine.addDialect(new ThorntailThymeleafDialect());
    }

    @GET
    @Produces("application/zip")
    public Response generate(
            @QueryParam("sv") @DefaultValue(THORNTAIL_VERSION) String thorntailVersion,
            @QueryParam("g") @DefaultValue("com.example") @NotNull(message = "Parameter 'g' (Group Id) must not be null") String groupId,
            @QueryParam("a") @DefaultValue("demo") @NotNull(message = "Parameter 'a' (Artifact Id) must not be null") String artifactId,
            @QueryParam("v") @DefaultValue("1.0.0-SNAPSHOT") @NotNull(message = "Parameter 'v' (Version) must not be null") String version,
            @QueryParam("p") String explicitPackage,
            @QueryParam("d") List<String> dependencies,
            @QueryParam("nested") @DefaultValue("true") boolean nestedOutput)
            throws Exception {
        // Remove empty values
        dependencies = new ArrayList<>(dependencies);
        dependencies.remove("");
        Context context = new Context();
        context.setVariable("groupId", groupId);
        context.setVariable("artifactId", artifactId);
        context.setVariable("version", version);
        context.setVariable("dependencies", dependencies);
        context.setVariable("thorntailVersion", thorntailVersion);

        String zipDirectory = nestedOutput ? (artifactId + "/") : "";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            zos.putNextEntry(new ZipEntry(zipDirectory + "src/main/java/"));
            zos.closeEntry();

            zos.putNextEntry(new ZipEntry(zipDirectory + "pom.xml"));
            zos.write(engine.process("templates/pom.tl.xml", context).getBytes());
            zos.closeEntry();

            if (enableJAXRS(dependencies)) {
                EndpointFilePathGenerator fpg = new EndpointFilePathGenerator(groupId, artifactId, explicitPackage);
                context.setVariable("endpointPackage", fpg.getEndpointPackage());
                zos.putNextEntry(new ZipEntry(zipDirectory + fpg.getEndpointFilePath()));
                zos.write(engine.process("templates/HelloWorldEndpoint.tl.java", context).getBytes());
                zos.putNextEntry(new ZipEntry(zipDirectory + fpg.getApplicationPath()));
                zos.write(engine.process("templates/RestApplication.tl.java", context).getBytes());
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
