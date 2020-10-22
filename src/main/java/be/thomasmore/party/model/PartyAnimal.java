package be.thomasmore.party.model;

import org.hibernate.annotations.Fetch;
import org.springframework.data.repository.cdi.Eager;

import javax.persistence.*;
import java.util.Collection;

@Entity
public class PartyAnimal {
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "party_animal_generator")
    @SequenceGenerator(name = "party_animal_generator", sequenceName = "party_animal_seq", initialValue = 0, allocationSize = 1)
    @Id
    int id;
    String name;
    @ManyToMany
    private Collection<Party> parties;
    @OneToOne(fetch = FetchType.LAZY)
    User user;


    public PartyAnimal() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Party> getParties() {
        return parties;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
