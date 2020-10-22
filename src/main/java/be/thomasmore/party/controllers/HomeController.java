package be.thomasmore.party.controllers;

import be.thomasmore.party.model.Artist;
import be.thomasmore.party.model.Party;
import be.thomasmore.party.model.PartyAnimal;
import be.thomasmore.party.model.Venue;
import be.thomasmore.party.repositories.ArtistRepository;
import be.thomasmore.party.repositories.PartyAnimalRepository;
import be.thomasmore.party.repositories.PartyRepository;
import be.thomasmore.party.repositories.VenueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Collection;
import java.util.Optional;

@Controller
public class HomeController {
    private Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private PartyRepository partyRepository;
    @Autowired
    private VenueRepository venueRepository;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private PartyAnimalRepository partyAnimalRepository;

    private String applicationName = "It's Party Time!!";

    @GetMapping({"/", "/parties"})
    public String parties(Authentication authentication, Model model) {
        String loggedInName = "nobody";
        String partyAnimalName = "";
        if (authentication != null) {
            loggedInName = authentication.getName();
            Optional<PartyAnimal> partyAnimal = partyAnimalRepository.findByUserUsername(loggedInName);
            partyAnimalName = (partyAnimal.isPresent()) ? partyAnimal.get().getName() : loggedInName;
        }
        logger.info(String.format("logged in: %s", loggedInName));
        model.addAttribute("appName", applicationName);
        model.addAttribute("parties", partyRepository.findAll());
        model.addAttribute("partyAnimalName", partyAnimalName);
        return "parties";
    }

    @GetMapping({"/party/{partyId}"})
    public String party(@PathVariable int partyId, Principal principal, Model model) {
        model.addAttribute("appName", applicationName);
        Optional<Party> selectedPartyFromDb = partyRepository.findById(partyId);
        addPartyInModel(model, selectedPartyFromDb);

        Party foundPartyForAnimal = null;
        if (principal != null) { // if logged in
            Optional<PartyAnimal> partyAnimalFromDb = partyAnimalRepository.findByUserUsername(principal.getName());
            if (partyAnimalFromDb.isPresent() && selectedPartyFromDb.isPresent()) {
                PartyAnimal partyAnimal = partyAnimalFromDb.get();
                foundPartyForAnimal = findPartyById(partyAnimal.getParties(), selectedPartyFromDb.get().getId());
            }
        }
        model.addAttribute("isGoing", foundPartyForAnimal != null);
        return "party";
    }


    @GetMapping({"/party"})
    public String party(Model model) {
        model.addAttribute("appName", applicationName);
        model.addAttribute("party", null);
        return "party";
    }

    @PostMapping({"/going/{partyId}"})
    public String going(@PathVariable int partyId, Principal principal, Model model) {
        if (principal != null) {
            Optional<PartyAnimal> partyAnimalFromDb = partyAnimalRepository.findByUserUsername(principal.getName());
            Optional<Party> selectedPartyFromDb = partyRepository.findById(partyId);

            if (partyAnimalFromDb.isPresent() && selectedPartyFromDb.isPresent()) {
                PartyAnimal partyAnimal = partyAnimalFromDb.get();
                Party foundPartyForAnimal = findPartyById(partyAnimal.getParties(), selectedPartyFromDb.get().getId());
                if (foundPartyForAnimal == null)
                    partyAnimal.getParties().add(selectedPartyFromDb.get());
                else
                    partyAnimal.getParties().remove(foundPartyForAnimal);
                partyAnimalRepository.save(partyAnimal);
            }
        }
        return "redirect:/party/" + partyId;
    }

    private Party findPartyById(Collection<Party> parties, int partyId) {
        for (Party p : parties) {
            if (p.getId() == partyId) return p;
        }
        return null;
    }

    private void addPartyInModel(Model model, Optional<Party> optionalPartyFromDb) {
        long nrOfVenues = partyRepository.count();
        if (optionalPartyFromDb.isPresent()) {
            Party party = optionalPartyFromDb.get();
            int partyId = party.getId();
            model.addAttribute("party", party);
            model.addAttribute("idOfPrevParty", partyId > 0 ? partyId - 1 : nrOfVenues - 1);
            model.addAttribute("idOfNextParty", partyId < nrOfVenues - 1 ? partyId + 1 : 0);
        } else {
            model.addAttribute("partyId", null);
        }
    }

    @GetMapping({"/venues", "/venues/{size}"})
    public String venues(@PathVariable(required = false) String size,
                         @RequestParam(required = false) Integer minNrOfPersons,
                         @RequestParam(required = false) Integer maxNrOfPersons,
                         @RequestParam(required = false) String venueNameSearchString,
                         Model model) {
        logger.info("Here we are!!!");
        logger.info(String.format("params: min = %d, max=%d, venueName=%s", minNrOfPersons, maxNrOfPersons, venueNameSearchString));

        model.addAttribute("appName", applicationName);
        model.addAttribute("filterButtons", new String[]{"all", "S", "M", "L", "XL"});

        if (minNrOfPersons == null && maxNrOfPersons == null && size != null) {
            if (size.equals("S")) {
                minNrOfPersons = null;
                maxNrOfPersons = 100;
            } else if (size.equals("M")) {
                minNrOfPersons = 100;
                maxNrOfPersons = 300;
            } else if (size.equals("L")) {
                minNrOfPersons = 300;
                maxNrOfPersons = 500;
            } else if (size.equals("XL")) {
                minNrOfPersons = 500;
                maxNrOfPersons = null;
            }
        }
        logger.info(String.format("interpreted: min = %d, max=%d, venueN vame=%s", minNrOfPersons, maxNrOfPersons, venueNameSearchString));
        model.addAttribute("venues", venueRepository.findVenuesBySearchCriteria(minNrOfPersons, maxNrOfPersons, venueNameSearchString));
        model.addAttribute("minNrOfPersons", minNrOfPersons);
        model.addAttribute("maxNrOfPersons", maxNrOfPersons);
        model.addAttribute("venueNameSearchString", venueNameSearchString);
        model.addAttribute("size", size);
        return "venues";
    }

    @GetMapping({"/venue"})
    public String venue(Model model) {
        model.addAttribute("appName", applicationName);
        Venue venue = null;
        model.addAttribute("venue", null);
        return "venue";
    }

    @GetMapping({"/venue/{venueId}"})
    public String venue(@PathVariable int venueId, Model model) {
        model.addAttribute("appName", applicationName);
        Optional<Venue> optionalVenueFromDb = venueRepository.findById(venueId);
        long nrOfVenues = venueRepository.count();
        if (optionalVenueFromDb.isEmpty()) {
            model.addAttribute("parties", new Party[]{});
        } else {
            Venue venue = optionalVenueFromDb.get();
            model.addAttribute("venue", venue);
            model.addAttribute("idOfPrevVenue", venueId > 0 ? venueId - 1 : nrOfVenues - 1);
            model.addAttribute("idOfNextVenue", venueId < nrOfVenues - 1 ? venueId + 1 : 0);
            model.addAttribute("parties", partyRepository.findPartiesByVenue(venue));
        }
        return "venue";
    }

    @GetMapping({"/artists"})
    public String artists(Model model) {
        model.addAttribute("appName", applicationName);
        model.addAttribute("artists", artistRepository.findAll());
        return "artists";
    }

    @GetMapping({"/artist/{artistId}"})
    public String artist(@PathVariable int artistId, Model model) {
        model.addAttribute("appName", applicationName);
        Optional<Artist> optionalArtistFromDb = artistRepository.findById(artistId);
        addArtistInModel(artistId, model, optionalArtistFromDb);
        return "artist";
    }

    private void addArtistInModel(@PathVariable int artistId, Model
            model, Optional<Artist> optionalArtistFromDb) {
        if (optionalArtistFromDb.isPresent()) {
            long nrOfArtists = artistRepository.count();
            model.addAttribute("artist", optionalArtistFromDb.get());
            model.addAttribute("idOfPrevArtist", artistId > 0 ? artistId - 1 : nrOfArtists - 1);
            model.addAttribute("idOfNextArtist", artistId < nrOfArtists - 1 ? artistId + 1 : 0);
        } else {
            model.addAttribute("artist", null);
        }
    }


    @GetMapping({"/animal/{animalId}"})
    public String animal(@PathVariable int animalId, Model model) {
        model.addAttribute("appName", applicationName);
        Optional<PartyAnimal> optionalAnimalFromDb = partyAnimalRepository.findById(animalId);
        if (optionalAnimalFromDb.isPresent()) {
            model.addAttribute("animal", optionalAnimalFromDb.get());
        } else {
            model.addAttribute("animal", null);
        }

        return "animal";
    }

}
