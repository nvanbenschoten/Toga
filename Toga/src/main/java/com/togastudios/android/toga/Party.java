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

    public Party() {

    }

    public void initParty() {
        this.setId();
        this.setType(-1);
        this.setTitle("");
        this.setSchool("");
        this.setDescription("");
    }

    public String getId() {
        return getString(idKey);
    }

    public void setId() {
        put(idKey,UUID.randomUUID().toString());
    }

    public String getTitle() {
        return getString(titleKey);
    }

    public void setTitle(String title) {
        put(titleKey, title);
    }

    public Location getLocation() {
        Location location = new Location("Parse");
        location.setLongitude(getDouble(longitudeKey));
        location.setLatitude(getDouble(latitudeKey));

        return location;
    }

    public void setLocation(Location location) {
        put(latitudeKey, location.getLatitude());
        put(longitudeKey, location.getLongitude());
    }

    public String getLocationString() {
        return getString(locationStringKey);
    }

    public void setLocationString(String location) {
        put(locationStringKey, location);
    }

    public String getDescription() {
        return getString(descriptionKey);
    }

    public void setDescription(String description) {
        put(descriptionKey, description);
    }

    public Date getDate() {
        return getDate(dateKey);
    }

    public void setDate(Date date) {
        put(dateKey, date);
    }

    public int getType() {
        return getInt(typeKey);
    }

    public void setType(int type) {
        put(typeKey, type);
    }

    public String getHost() throws ParseException {
        ParseUser user = (ParseUser)get(hostKey);
        user.fetchIfNeeded();
        return user.getUsername();
    }

    public void setHost(ParseUser student) {
        put(hostKey, student);
    }

    public void setSchool(String school) {
        put(schoolKey, school);
    }

    public String getSchool() {
        return getString(schoolKey);
    }
}
