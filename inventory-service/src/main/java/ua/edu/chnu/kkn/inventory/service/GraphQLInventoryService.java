package ua.edu.chnu.kkn.inventory.service;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;
import ua.edu.chnu.kkn.inventory.model.Car;
import ua.edu.chnu.kkn.inventory.repository.CarRepository;

import java.util.List;
import java.util.Optional;

@GraphQLApi
public class GraphQLInventoryService {

    @Inject
    CarRepository repository;

    @Query
    public List<Car> cars() {
        return repository.listAll();
    }

    @Transactional
    @Mutation
    public Car register(Car car) {
        repository.persist(car);
        return car;
    }

    @Mutation
    public boolean remove(String licensePlateNumber) {
        Optional<Car> toBeRemoved = repository.findByLicensePlateNumberOptional(licensePlateNumber);
        if (toBeRemoved.isPresent()) {
            repository.delete(toBeRemoved.get());
            return true;
        } else {
            return false;
        }
    }
}
