package fr.spacefox.myuplink.client;

import io.quarkus.oidc.client.filter.OidcClientFilter;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import java.util.List;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "myuplink")
@OidcClientFilter
@Path("/")
public interface DevicesResource {

    @GET
    @Produces("application/json")
    @Path("v3/devices/{deviceId}/points")
    List<Point> getPoints(@PathParam("deviceId") String deviceId, @HeaderParam("Accept-Language") String locale);
}
