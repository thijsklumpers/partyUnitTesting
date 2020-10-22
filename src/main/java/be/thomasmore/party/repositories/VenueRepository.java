package be.thomasmore.party.repositories;

import be.thomasmore.party.model.Venue;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VenueRepository extends CrudRepository<Venue, Integer> {
    public Optional<Venue> findVenueByVenueName(String venueName);

    @Query("SELECT v FROM Venue v WHERE " +
            " (:min is null or v.maxNumberOfPersons >= :min) and " +
            " (:max is null or v.maxNumberOfPersons <= :max) and " +
            " (:venueNameSearchString is null or v.venueName like %:venueNameSearchString%) ")
    public Iterable<Venue> findVenuesBySearchCriteria(@Param("min") Integer min,
                                                      @Param("max") Integer max,
                                                      @Param("venueNameSearchString") String venueNameSearchString);
}
