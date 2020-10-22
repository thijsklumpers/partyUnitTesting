package be.thomasmore.party.repositories;

import be.thomasmore.party.model.PartyAnimal;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PartyAnimalRepository extends CrudRepository<PartyAnimal, Integer> {
    Optional<PartyAnimal> findByUserUsername(String userName);
}
