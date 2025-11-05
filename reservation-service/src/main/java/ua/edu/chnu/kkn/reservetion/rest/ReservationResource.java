package ua.edu.chnu.kkn.reservetion.rest;

import io.quarkus.logging.Log;
import io.smallrye.graphql.client.GraphQLClient;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestQuery;
import ua.edu.chnu.kkn.reservetion.inventory.Car;
import ua.edu.chnu.kkn.reservetion.inventory.GraphQLInventoryClient;
import ua.edu.chnu.kkn.reservetion.inventory.InventoryClient;
import ua.edu.chnu.kkn.reservetion.rental.Rental;
import ua.edu.chnu.kkn.reservetion.rental.RentalClient;
import ua.edu.chnu.kkn.reservetion.reservation.Reservation;
import ua.edu.chnu.kkn.reservetion.reservation.ReservationsRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Path("reservation")
@Produces(MediaType.APPLICATION_JSON)
public class ReservationResource {

    @Inject
    SecurityContext context;

    private final ReservationsRepository reservationsRepository;
    private final InventoryClient inventoryClient;
    private final RentalClient rentalClient;

    public ReservationResource(
            ReservationsRepository reservationsRepository,
            @GraphQLClient("inventory") GraphQLInventoryClient inventoryClient,
            @RestClient RentalClient rentalClient) {
        this.reservationsRepository = reservationsRepository;
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
        List<Reservation> reservations = reservationsRepository.findAll();
        for (Reservation reservation : reservations) {
            if (reservation.isReserved(startDate, endDate)) {
                carsById.remove(reservation.carId);
            }
        }
        return carsById.values();
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public Reservation create(Reservation reservation) {
        reservation.userId = context.getUserPrincipal() != null ?
                context.getUserPrincipal().getName() : "anonymous";
        Reservation result = reservationsRepository.save(reservation);
        if (reservation.startDay.equals(LocalDate.now())) {
            Rental rental = rentalClient.start(reservation.userId, result.id);
            Log.info("Successfully started rental " + rental);
        }
        return result;
    }

    @GET
    @Path("all")
    public Collection<Reservation> allReservations() {
        String userId = context.getUserPrincipal() != null ?
                context.getUserPrincipal().getName() : null;
        return reservationsRepository.findAll()
                .stream()
                .filter(reservation -> userId == null ||
                        userId.equals(reservation.userId))
                .toList();
    }
}
