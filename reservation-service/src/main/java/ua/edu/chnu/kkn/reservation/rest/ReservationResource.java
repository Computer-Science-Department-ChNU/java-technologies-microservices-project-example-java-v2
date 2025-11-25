package ua.edu.chnu.kkn.reservation.rest;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.graphql.client.GraphQLClient;
import io.smallrye.mutiny.Uni;
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
import java.util.stream.Collectors;

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
    public Uni<Collection<Car>> availability(
            @RestQuery LocalDate startDate,
            @RestQuery LocalDate endDate
    ) {
        Uni<List<Car>> availableCarsUni = inventoryClient.allCars();
        Uni<List<Reservation>> reservationsUni = Reservation.listAll();
        return Uni.combine().all().unis(availableCarsUni, reservationsUni)
                .with((availableCars, reservations) -> {
                    Map<Long, Car> carsById = new HashMap<>();
                    for (Car car : availableCars) {
                        carsById.put(car.id, car);
                    }
                    for (Reservation reservation : reservations) {
                        if (reservation.isReserved(startDate, endDate)) {
                            carsById.remove(reservation.carId);
                        }
                    }
                    return carsById.values();
                });
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @WithTransaction
    public Uni<Reservation> create(Reservation reservation) {
        reservation.userId = context.getUserPrincipal() != null ?
                context.getUserPrincipal().getName() : "anonymous";
        return reservation.<Reservation>persist().onItem()
                .call(persistedReservation -> {
                    Log.info("Successfully reserved reservation " + persistedReservation);
                    if (persistedReservation.startDay.equals(LocalDate.now())) {
                        return rentalClient.start(reservation.userId, reservation.id)
                                .onItem().invoke(rental -> Log.info("Successfully started rental " + rental))
                                .replaceWith(persistedReservation);
                    }
                    return Uni.createFrom().item(persistedReservation);
                });
    }

    @GET
    @Path("all")
    public Uni<Collection<Reservation>> allReservations() {
        String userId = context.getUserPrincipal() != null ?
                context.getUserPrincipal().getName() : null;
        return Reservation.<Reservation>listAll().onItem()
                .transform(reservations -> reservations.stream()
                        .filter(reservation -> userId == null || userId.equals(reservation.userId))
                        .toList()
                );
    }
}
