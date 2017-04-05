package com.darelbitsy.dbweather.model.geonames;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Darel Bitsy on 03/04/17.
 * My implementation of the geoname Xml Tag
 * in GeoName api xml response
 */

@Root(name = "geoname")
public class GeoName {

    @Element(name = "toponymName",required = false, type = String.class)
    private String toponymName;

    @Element(name = "name", type = String.class)
    private String name;

    @Element(name = "lat", type = Double.class)
    private double latitude;

    @Element(name = "lng", type = Double.class)
    private double longitude;

    @Element(name = "geonameId", required = false, type = Long.class)
    private long geonameId;

    @Element(name = "countryCode", type = String.class)
    private String countryCode;

    @Element(name = "countryName", type = String.class)
    private String countryName;

    @Element(name = "fcl", required = false, type = String.class)
    private String fcl;

    @Element(name = "fcode", required = false, type = String.class)
    private String fcode;

    public GeoName() {
    }

    public String getToponymName() {
        return toponymName;
    }

    public void setToponymName(String toponymName) {
        this.toponymName = toponymName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getGeonameId() {
        return geonameId;
    }

    public void setGeonameId(long geonameId) {
        this.geonameId = geonameId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getFcl() {
        return fcl;
    }

    public void setFcl(String fcl) {
        this.fcl = fcl;
    }

    public String getFcode() {
        return fcode;
    }

    public void setFcode(String fcode) {
        this.fcode = fcode;
    }
}
