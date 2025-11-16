package ua.edu.chnu.kkn.inventory.repository;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import ua.edu.chnu.kkn.inventory.model.Car;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class CarInventory {

    private List<Car> cars;

    public static final AtomicLong ids = new AtomicLong(0);

    @PostConstruct
    void initialize() {
        cars = new CopyOnWriteArrayList<>();
        initialData();
    }

    public List<Car> getCars() {
        return cars;
    }

    private void initialData() {
        Car mazda = new Car("ABC123", "Mazda", "6");
        mazda.id = ids.incrementAndGet();
        cars.add(mazda);
        Car ford = new Car("XYZ987", "Ford", "Mustang");
        ford.id = ids.incrementAndGet();
        cars.add(ford);
    }
}
