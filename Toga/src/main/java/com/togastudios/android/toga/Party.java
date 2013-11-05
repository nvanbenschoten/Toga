package com.togastudios.android.toga;

import android.location.Location;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;
import java.util.UUID;

@ParseClassName("Party")
public class Party extends ParseObject {

    private static final String TAG = "Party";

    public final static String idKey = "partyId";
    public final static String titleKey = "title";
    public final static String latitudeKey = "latitude";
    public final static String longitudeKey = "longitude";
    public final static String locationStringKey = "locationString";
    public final static String descriptionKey = "description";
    public final static String dateKey = "date";
    public final static String typeKey = "type";
    public final static String hostKey = "host";
    public final static String schoolKey = "school";
    public final static String themeIdKey = "themeId";

    /**
     * Enumeration of partyTypes.
     * TODO Actually use this enumeration
     */
    public enum partyType {PUBLIC_PARTY, PRIVATE_PARTY, VIP_PARTY}

    /**
     * Blank public constructor needed for Parse.
     */
    public Party() {}

    /**
     * Initializes all necessary fields in the Party object.
     * Should be called directly after the blank constructor.
     */
    public void initParty() {
        this.setId();
        this.setType(-1);
        this.setThemeId(0);
        this.setTitle("");
        this.setSchool("");
        this.setDescription("");
    }

    /**
     * Gets the Party object's id.
     * @return String containing the party id
     */
    public String getId() {
        return getString(idKey);
    }

    /**
     * Sets the Party object's id to a random UUID.
     */
    public void setId() {
        put(idKey, UUID.randomUUID().toString());
    }

    /**
     * Gets the Party object's title.
     * @return String containing the party title
     */
    public String getTitle() {
        return getString(titleKey);
    }

    /**
     * Sets the Party object's title.
     * @param title String containing the party title
     */
    public void setTitle(String title) {
        put(titleKey, title);
    }

    /**
     * Gets the Party object's location.
     * @return Location containing the party's latitude and longitude
     */
    public Location getLocation() {
        Location location = new Location(TAG + " : getLocation()");
        location.setLongitude(getDouble(longitudeKey));
        location.setLatitude(getDouble(latitudeKey));

        return location;
    }

    /**
     * Sets the Party object's location.
     * @param location Location containing the party's latitude and longitude
     */
    public void setLocation(Location location) {
        put(latitudeKey, location.getLatitude());
        put(longitudeKey, location.getLongitude());
    }

    /**
     * Gets the Party object's location String.
     * @return String containing the party's human readable location
     */
    public String getLocationString() {
        return getString(locationStringKey);
    }

    /**
     * Sets the Party object's location String.
     * @param location String containing the party's human readable location
     */
    public void setLocationString(String location) {
        put(locationStringKey, location);
    }

    /**
     * Gets the Party object's description.
     * @return String containing the party's description
     */
    public String getDescription() {
        return getString(descriptionKey);
    }

    /**
     * Sets the Party object's description.
     * @param description String containing the party's description
     */
    public void setDescription(String description) {
        put(descriptionKey, description);
    }

    /**
     * Gets the Party object's Date.
     * @return Date containing the party's date
     */
    public Date getDate() {
        return getDate(dateKey);
    }

    /**
     * Sets the Party object's Date.
     * @param date Date containing the party's date
     */
    public void setDate(Date date) {
        put(dateKey, date);
    }

    /**
     * Gets the Party object's type.
     * @return int corresponding to the party's type
     */
    public int getType() {
        return getInt(typeKey);
    }

    /**
     * Sets the Party object's type.
     * @param type int corresponding to the party's type
     */
    public void setType(int type) {
        put(typeKey, type);
    }

    /**
     * Gets the Party object's theme id.
     * @return int corresponding to the party's theme id
     */
    public int getThemeId() {
        return getInt(themeIdKey);
    }

    /**
     * Sets the Party object's theme id.
     * @param type int corresponding to the party's theme id
     */
    public void setThemeId(int type) {
        put(themeIdKey, type);
    }

    /**
     * Gets the Party object's host.
     * @return String containing the party's host
     */
    public String getHost() throws ParseException {
        ParseUser user = (ParseUser)get(hostKey);
        user.fetchIfNeeded();
        return user.getUsername();
    }

    /**
     * Sets the Party object's host.
     * @param student String containing the party's host
     */
    public void setHost(ParseUser student) {
        put(hostKey, student);
    }

    /**
     * Gets the Party object's school.
     * @return String containing the party's school
     */
    public String getSchool() {
        return getString(schoolKey);
    }

    /**
     * Sets the Party object's school.
     * @param school String containing the party's school
     */
    public void setSchool(String school) {
        put(schoolKey, school);
    }

}
