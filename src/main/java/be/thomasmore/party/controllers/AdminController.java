package be.thomasmore.party.controllers;

import be.thomasmore.party.model.Artist;
import be.thomasmore.party.model.Party;
import be.thomasmore.party.model.Venue;
import be.thomasmore.party.repositories.ArtistRepository;
import be.thomasmore.party.repositories.PartyRepository;
import be.thomasmore.party.repositories.VenueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private Logger logger = LoggerFactory.getLogger(AdminController.class);
    private String applicationName = "It's Party Time!!";
    @Autowired
    private PartyRepository partyRepository;
    @Autowired
    private VenueRepository venueRepository;
    @Autowired
    private ArtistRepository artistRepository;

    @GetMapping({"/edit-party", "/edit-party/{partyId}"})
    public String editParty(@PathVariable(required = false) int partyId, Model model) {
        Optional<Party> optionalPartyFromDb = partyRepository.findById(partyId);
        Party party = (optionalPartyFromDb.isPresent()) ? optionalPartyFromDb.get() : null;
        model.addAttribute("appName", applicationName);
        model.addAttribute("party", party);
        model.addAttribute("venues", venueRepository.findAll());
        model.addAttribute("artists", artistRepository.findAll());
        return "admin/edit-party";
    }

    @PostMapping({"/edit-party/{partyId}"})
    public String editPartyPost(@PathVariable(required = false) int partyId,
                                @RequestParam String partyName,
                                @RequestParam String partyDate,
                                @RequestParam String partyVenue,
                                @RequestParam String[] partyArtists,
                                Model model) {
        logger.info(String.format("editParty %d -- name=%s, date=%s, venue=%s, artists=%s\n", partyId, partyName, partyDate, partyVenue, Arrays.toString(partyArtists)));

        Optional<Party> partyFromDb = partyRepository.findById(partyId);
        if (partyFromDb.isPresent() && !partyName.isBlank()) {
            Party party = partyFromDb.get();
            party.setName(partyName);
            party.setDateFromString(partyDate);
            if (!party.getVenue().getVenueName().equals(partyVenue)) {
                Optional<Venue> venueFromDb = venueRepository.findVenueByVenueName(partyVenue);
                if (venueFromDb.isPresent()) {
                    party.setVenue(venueFromDb.get());
                }
            }
            ArrayList<Artist> artistList = new ArrayList<>();
            for (String artistNameString : partyArtists) {
                Optional<Artist> artistFromDb = artistRepository.findArtistByName(artistNameString);
                if (artistFromDb.isPresent())
                    artistList.add(artistFromDb.get());
            }
            party.setArtists(artistList);
            partyRepository.save(party);
        }
        return "redirect:/party/" + partyId;
    }

    @GetMapping({"/create-party"})
    public String createParty(Model model) {
        model.addAttribute("appName", applicationName);
        model.addAttribute("venues", venueRepository.findAll());
        model.addAttribute("artists", artistRepository.findAll());
        return "admin/create-party";
    }

    @PostMapping({"/create-party"})
    public String createPartyPost(@RequestParam String partyName,
                                  @RequestParam String partyDate,
                                  @RequestParam String partyVenue,
                                  @RequestParam(required = false) String[] partyArtists,
                                  Model model) {
        logger.info(String.format("createParty name=%s, date=%s, venue=%s, artists=%s\n", partyName, partyDate, partyVenue, Arrays.toString(partyArtists)));

        Optional<Party> partyFromDb = partyRepository.findPartyByName(partyName);
        Optional<Venue> venueFromDb = venueRepository.findVenueByVenueName(partyVenue);
        if (!partyFromDb.isPresent() && venueFromDb.isPresent() && !partyName.isBlank()) {
            Party party = new Party();
            party.setName(partyName);
            party.setDateFromString(partyDate);
            party.setVenue(venueFromDb.get());
            ArrayList<Artist> artistList = new ArrayList<>();
            if (partyArtists != null)
                for (String artistNameString : partyArtists) {
                    Optional<Artist> artistFromDb = artistRepository.findArtistByName(artistNameString);
                    if (artistFromDb.isPresent())
                        artistList.add(artistFromDb.get());
                }
            party.setArtists(artistList);
            partyRepository.save(party);
        }
        return "redirect:/parties";

    }
}
