package ua.edu.chnu.kkn.reservetion.reservation;

import java.io.Serializable;
import java.time.LocalDate;

public class Reservation {
    public String userId;
    public Long id;
    public final Long carId;
    public final LocalDate startDay;
    public final LocalDate endDay;

    public Reservation(Long carId, LocalDate startDay, LocalDate endDay) {
        this.carId = carId;
        this.startDay = startDay;
        this.endDay = endDay;
    }

    public boolean isReserved(LocalDate startDay, LocalDate endDay) {
        return (!(this.endDay.isBefore(startDay) || this.startDay.isAfter(endDay)));
    }
}
