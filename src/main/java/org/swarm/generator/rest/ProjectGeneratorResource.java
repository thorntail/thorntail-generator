package org.swarm.generator.rest;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.enterprise.context.ApplicationScoped;
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
public class ProjectGeneratorResource
{
   private static final String WILDFLY_SWARM_VERSION = "1.0.0.Beta7";

   TemplateEngine engine;

   public ProjectGeneratorResource()
   {
      engine = new TemplateEngine();
      engine.setTemplateResolver(new ClassLoaderTemplateResolver(getClass().getClassLoader()));
   }

   @GET
   @Produces("application/zip")
   public Response generate(
            @QueryParam("g") String groupId,
            @QueryParam("a") String artifactId,
            @QueryParam("dep") String[] dependencies)
            throws Exception
   {
      Context context = new Context();
      context.setVariable("groupId", groupId);
      context.setVariable("artifactId", artifactId);
      context.setVariable("dependencies", dependencies);
      context.setVariable("swarmVersion", WILDFLY_SWARM_VERSION);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try (ZipOutputStream zos = new ZipOutputStream(baos))
      {
         zos.putNextEntry(new ZipEntry(artifactId + "/src/main/java/"));
         zos.closeEntry();

         zos.putNextEntry(new ZipEntry(artifactId + "/pom.xml"));
         zos.write(engine.process("templates/pom.tl.xml", context).getBytes());
         zos.closeEntry();

         if (enableJAXRS(dependencies))
         {
            zos.putNextEntry(new ZipEntry(artifactId + "/src/main/java/com/example/rest/RestApplication.java"));
            zos.write(engine.process("templates/RestApplication.tl.java", context).getBytes());
            zos.closeEntry();

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

   private boolean enableJAXRS(String[] dependencies)
   {
      if (dependencies == null || dependencies.length == 0)
      {
         return true;
      }
      return Arrays.stream(dependencies).anyMatch(d -> d.contains("jaxrs"));
   }

}