package ua.edu.chnu.kkn.rental.reservation;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(baseUri = "http://localhost:8081")
@Path("admin")
public interface ReservationClient {

    @GET
    @Path("reservation/{id}")
    Reservation getById(Long id);
}
