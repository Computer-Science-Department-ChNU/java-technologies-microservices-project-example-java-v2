package ua.edu.chnu.kkn.inventory.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String licensePlateNumber;
    public String manufacturer;
    public String model;

    public Car() {}

    public Car(String licensePlateNumber, String manufacturer, String model) {
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
