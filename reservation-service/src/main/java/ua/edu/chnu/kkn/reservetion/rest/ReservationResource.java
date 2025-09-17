package ua.edu.chnu.kkn.reservetion.rest;

import io.quarkus.logging.Log;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestQuery;
import ua.edu.chnu.kkn.reservetion.inventory.Car;
import ua.edu.chnu.kkn.reservetion.inventory.InventoryClient;
import ua.edu.chnu.kkn.reservetion.rental.Rental;
import ua.edu.chnu.kkn.reservetion.rental.RentalClient;
import ua.edu.chnu.kkn.reservetion.reservation.Reservation;
import ua.edu.chnu.kkn.reservetion.reservation.ReservationsRepository;

import java.time.LocalDate;
import java.util.*;

@Path("reservation")
@Produces(MediaType.APPLICATION_JSON)
public class ReservationResource {

    private final ReservationsRepository reservationsRepository;
    private final InventoryClient inventoryClient;
    private final RentalClient rentalClient;

    public ReservationResource(
            ReservationsRepository reservationsRepository,
            InventoryClient inventoryClient,
            @RestClient RentalClient rentalClient) {
        this.reservationsRepository = reservationsRepository;
        this.inventoryClient = inventoryClient;
        this.rentalClient = rentalClient;
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
            carsById.put(car.id(), car);
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
        Reservation result = reservationsRepository.save(reservation);
        // this is just a dummy value for the time being
        String userId = "x";
        if (reservation.startDay.equals(LocalDate.now())) {
            Rental rental = rentalClient.start(userId, result.id);
            Log.info("Successfully start rental " + rental);
        }
        return result;
    }
}
