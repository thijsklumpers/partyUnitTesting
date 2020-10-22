package be.thomasmore.party.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Venue {


    @Id
    private int id;
    private String venueName;
    private boolean infoAvailableMaxNumberOfPersons = false;
    private int maxNumberOfPersons = 0;
    private boolean areOwnDrinksAllowed = false;
    private boolean isFoodPossible = false;
    private String linkMoreInfo;

    public Venue() {
    }

    public Venue(int id, String venueName, String linkMoreInfo) {
        this.id = id;
        this.venueName = venueName;
        this.linkMoreInfo = linkMoreInfo;
    }

    public Venue(int id, String venueName, int maxNumberOfPersons, boolean areOwnDrinksAllowed, boolean isFoodPossible, String linkMoreInfo) {
        this.id = id;
        this.venueName = venueName;
        this.infoAvailableMaxNumberOfPersons = true;
        this.maxNumberOfPersons = maxNumberOfPersons;
        this.areOwnDrinksAllowed = areOwnDrinksAllowed;
        this.isFoodPossible = isFoodPossible;
        this.linkMoreInfo = linkMoreInfo;
    }

    public Venue(int id, String venueName, boolean areOwnDrinksAllowed, boolean isFoodPossible, String linkMoreInfo) {
        this.id = id;
        this.venueName = venueName;
        this.areOwnDrinksAllowed = areOwnDrinksAllowed;
        this.isFoodPossible = isFoodPossible;
        this.linkMoreInfo = linkMoreInfo;
    }


    public int getId() {
        return id;
    }

    public boolean isInfoAvailableMaxNumberOfPersons() {
        return infoAvailableMaxNumberOfPersons;
    }

    public int getMaxNumberOfPersons() {
        return maxNumberOfPersons;
    }

    public void setMaxNumberOfPersons(int maxNumberOfPersons) {
        this.maxNumberOfPersons = maxNumberOfPersons;
        this.infoAvailableMaxNumberOfPersons = true;
    }


    public boolean isAreOwnDrinksAllowed() {
        return areOwnDrinksAllowed;
    }

    public void setAreOwnDrinksAllowed(boolean areOwnDrinksAllowed) {
        this.areOwnDrinksAllowed = areOwnDrinksAllowed;
    }

    public boolean isFoodPossible() {
        return isFoodPossible;
    }

    public void setFoodPossible(boolean foodPossible) {
        isFoodPossible = foodPossible;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getLinkMoreInfo() {
        return linkMoreInfo;
    }

    public void setLinkMoreInfo(String linkMoreInfo) {
        this.linkMoreInfo = linkMoreInfo;
    }
}
