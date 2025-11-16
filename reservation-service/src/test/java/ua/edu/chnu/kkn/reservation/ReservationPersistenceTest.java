package ua.edu.chnu.kkn.reservation;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ua.edu.chnu.kkn.reservation.reservation.entity.Reservation;

import java.time.LocalDate;

@QuarkusTest
public class ReservationPersistenceTest {

    @Test
    @Transactional
    public void testCreateReservation() {
        Reservation reservation = new Reservation();
        reservation.startDay = LocalDate.now().plusDays(5);
        reservation.endDay = LocalDate.now().plusDays(12);
        reservation.carId = 384L;
        reservation.persist();
        Assertions.assertNotNull(reservation.id);
        Assertions.assertEquals(1, Reservation.count());
        Reservation persistedReservation =
                Reservation.findById(reservation.id);
        Assertions.assertNotNull(persistedReservation);
        Assertions.assertEquals(reservation.carId,
                persistedReservation.carId);
    }
}
