package ua.edu.chnu.kkn.reservation.rest;

import io.quarkus.logging.Log;
import io.smallrye.graphql.client.GraphQLClient;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestQuery;
import ua.edu.chnu.kkn.reservation.inventory.Car;
import ua.edu.chnu.kkn.reservation.inventory.GraphQLInventoryClient;
import ua.edu.chnu.kkn.reservation.inventory.InventoryClient;
import ua.edu.chnu.kkn.reservation.rental.Rental;
import ua.edu.chnu.kkn.reservation.rental.RentalClient;
import ua.edu.chnu.kkn.reservation.reservation.entity.Reservation;

import java.time.LocalDate;
import java.util.*;

@Path("reservation")
@Produces(MediaType.APPLICATION_JSON)
public class ReservationResource {

    @Inject
    SecurityContext context;

    private final InventoryClient inventoryClient;
    private final RentalClient rentalClient;

    public ReservationResource(
            @GraphQLClient("inventory") GraphQLInventoryClient inventoryClient,
            @RestClient RentalClient rentalClient) {
        this.rentalClient = rentalClient;
        this.inventoryClient = inventoryClient;
    }

    @GET
    @Path("availability")
    public Collection<Car> availability(
            @RestQuery LocalDate startDate,
            @RestQuery LocalDate endDate
    ) {
        List<Car> availableCars = inventoryClient.allCars();
        Map<Long, Car> carsById = new HashMap<>();
        for (Car car : availableCars) {
            carsById.put(car.id, car);
        }
        List<Reservation> reservations = Reservation.listAll();
        for (Reservation reservation : reservations) {
            if (reservation.isReserved(startDate, endDate)) {
                carsById.remove(reservation.carId);
            }
        }
        return carsById.values();
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Transactional
    public Reservation create(Reservation reservation) {
        reservation.userId = context.getUserPrincipal() != null ?
                context.getUserPrincipal().getName() : "anonymous";
        reservation.persist();
        if (reservation.startDay.equals(LocalDate.now())) {
            Rental rental = rentalClient.start(reservation.userId, reservation.id);
            Log.info("Successfully started rental " + rental);
        }
        return reservation;
    }

    @GET
    @Path("all")
    public Collection<Reservation> allReservations() {
        String userId = context.getUserPrincipal() != null ?
                context.getUserPrincipal().getName() : null;
        return Reservation.<Reservation>streamAll()
                .filter(reservation -> userId == null ||
                        userId.equals(reservation.userId))
                .toList();
    }
}
