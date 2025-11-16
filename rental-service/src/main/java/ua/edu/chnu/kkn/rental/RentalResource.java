package ua.edu.chnu.kkn.rental;

import io.quarkus.logging.Log;
import jakarta.ws.rs.*;
import org.jboss.resteasy.reactive.RestPath;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.List;

@Path("rental")
public class RentalResource {

    private final AtomicLong id = new AtomicLong();

    @Path("start/{userId}/{reservationId}")
    @POST
    public Rental start(@RestPath String userId, @RestPath Long reservationId) {
        Log.infof("Start rental: userId = %s, reservationId = %s", userId, reservationId);
        Rental rental = new Rental();
        rental.userId = userId;
        rental.reservationId = reservationId;
        rental.startDate = LocalDate.now();
        rental.active = true;
        rental.persist();
        return rental;
    }

    @PUT
    @Path("/end/{userId}/{reservationId}")
    public Rental end(String userId, Long reservationId) {
        Log.infof("Ending rental for %s with reservation %s",
                userId, reservationId);
        Optional<Rental> optionalRental = Rental
                .findByUserAndReservationIdsOptional(userId, reservationId);
        if (optionalRental.isPresent()) {
            Rental rental = optionalRental.get();
            rental.endDate = LocalDate.now();
            rental.active = false;
            rental.update();
            return rental;
        } else {
            throw new NotFoundException("Rental not found");
        }
    }

    @GET
    public List<Rental> list() {
        return Rental.listAll();
    }

    @GET
    @Path("/active")
    public List<Rental> listActive() {
        return Rental.listActive();
    }
}
