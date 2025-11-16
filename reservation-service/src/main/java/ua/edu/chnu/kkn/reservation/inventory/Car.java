package ua.edu.chnu.kkn.reservation.inventory;

import java.util.Objects;

public class Car {

    public Long id;
    public String licensePlateNumber;
    public String manufacturer;
    public String model;

    public Car() {
    }

    public Car(Long id, String licensePlateNumber, String manufacturer, String model) {
        this.id = id;
        this.licensePlateNumber = licensePlateNumber;
        this.manufacturer = manufacturer;
        this.model = model;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return Objects.equals(id, car.id) && Objects.equals(licensePlateNumber, car.licensePlateNumber)
                && Objects.equals(manufacturer, car.manufacturer) && Objects.equals(model, car.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, licensePlateNumber, manufacturer, model);
    }
}
