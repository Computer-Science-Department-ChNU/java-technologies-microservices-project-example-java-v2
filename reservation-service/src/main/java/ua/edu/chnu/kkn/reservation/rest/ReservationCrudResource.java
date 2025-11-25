package ua.edu.chnu.kkn.reservation.rest;

import io.quarkus.hibernate.reactive.rest.data.panache.PanacheEntityResource;
import io.quarkus.rest.data.panache.ResourceProperties;
import ua.edu.chnu.kkn.reservation.reservation.entity.Reservation;

@ResourceProperties(path = "/admin/reservation")
public interface ReservationCrudResource extends PanacheEntityResource<Reservation, Long> {
}
