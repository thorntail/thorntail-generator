package [# th:utext="${endpointPackage}"/];

[# th:if="${#thorntail.hasDependency('cdi') || #thorntail.hasDependency('microprofile')}"]import javax.enterprise.context.ApplicationScoped;[/]
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

[# th:if="${#thorntail.hasDependency('cdi') || #thorntail.hasDependency('microprofile')}"]@ApplicationScoped[/]
@Path("/hello")
public class HelloWorldEndpoint {
    @GET
    @Produces("text/plain")
    public Response doGet() {
        return Response.ok("Hello from Thorntail!").build();
    }
}
