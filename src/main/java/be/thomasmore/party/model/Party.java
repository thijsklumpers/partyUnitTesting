package be.thomasmore.party.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

@Entity
public class Party {
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "party_generator")
    @SequenceGenerator(name = "party_generator", sequenceName = "party_seq", initialValue = 0, allocationSize = 1)
    @Id
    private Integer id;
    private String name;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;
    @ManyToOne(fetch = FetchType.LAZY)
    private Venue venue;
    @ManyToMany
    private Collection<Artist> artists;
    @ManyToMany(mappedBy = "parties")
    private Collection<PartyAnimal> partyAnimals;

    public Party() {
    }

    public Integer getId() {
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

    public String getDateAsString() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MMM-dd");
        return (date!=null) ?  formatter.format(date) : "";
    }

    public void setDateFromString(String dateString) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MMM-dd");
        try {
            date = formatter.parse(dateString);
        } catch (ParseException e) {
        }
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public Collection<Artist> getArtists() {
        return artists;
    }

    public void setArtists(Collection<Artist> artists) {
        this.artists = artists;
    }

    public Collection<PartyAnimal> getPartyAnimals() {
        return partyAnimals;
    }
}
