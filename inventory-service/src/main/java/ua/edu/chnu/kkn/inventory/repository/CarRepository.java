package ua.edu.chnu.kkn.inventory.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import ua.edu.chnu.kkn.inventory.model.Car;

import java.util.Optional;

@ApplicationScoped
public class CarRepository implements PanacheRepository<Car> {

    public Optional<Car> findByLicensePlateNumberOptional(String licensePlateNumber) {
        return find("licensePlateNumber", licensePlateNumber)
                .firstResultOptional();
    }
}
