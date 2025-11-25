package ua.edu.chnu.kkn.inventory.service;

import io.quarkus.grpc.GrpcService;
import io.quarkus.logging.Log;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ua.edu.chnu.kkn.inventory.model.*;
import ua.edu.chnu.kkn.inventory.repository.CarInventory;
import ua.edu.chnu.kkn.inventory.repository.CarRepository;

import java.util.Optional;

@GrpcService
public class GrpcInventoryService implements InventoryService {

    @Inject
    CarRepository repository;

    @Override
    @Transactional
    @Blocking
    public Multi<CarResponse> add(Multi<InsertCarRequest> requests) {
        return requests
                .map(request -> {
                    Car car = new Car();
                    car.licensePlateNumber = request.getLicensePlateNumber();
                    car.manufacturer = request.getManufacturer();
                    car.model = request.getModel();
                    car.id = CarInventory.ids.incrementAndGet();
                    return car;
                }).onItem().invoke(car -> {
                    QuarkusTransaction.requiringNew().run( () -> {
                        repository.persist(car);
                        Log.info("Persisting " + car);
                    });
                }).map(car -> CarResponse.newBuilder()
                        .setLicensePlateNumber(car.licensePlateNumber)
                        .setManufacturer(car.manufacturer)
                        .setModel(car.model)
                        .setId(car.id)
                        .build());
    }

    @Override
    @Transactional
    @Blocking
    public Uni<CarResponse> remove(RemoveCarRequest request) {
        Optional<Car> optionalCar = repository
                .findByLicensePlateNumberOptional(request.getLicensePlateNumber());
        if (optionalCar.isPresent()) {
            Car removedCar = optionalCar.get();
            repository.delete(removedCar);
            return Uni.createFrom().item(CarResponse.newBuilder()
                    .setLicensePlateNumber(removedCar.licensePlateNumber)
                    .setManufacturer(removedCar.manufacturer)
                    .setModel(removedCar.model)
                    .setId(removedCar.id)
                    .build());
        }
        return Uni.createFrom().nullItem();
    }
}
