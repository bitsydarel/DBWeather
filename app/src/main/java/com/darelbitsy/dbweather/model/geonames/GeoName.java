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

    public void setToponymName(final String toponymName) {
        this.toponymName = toponymName;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(final double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(final double longitude) {
        this.longitude = longitude;
    }

    public long getGeonameId() {
        return geonameId;
    }

    public void setGeonameId(final long geonameId) {
        this.geonameId = geonameId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(final String countryName) {
        this.countryName = countryName;
    }

    public String getFcl() {
        return fcl;
    }

    public void setFcl(final String fcl) {
        this.fcl = fcl;
    }

    public String getFcode() {
        return fcode;
    }

    public void setFcode(final String fcode) {
        this.fcode = fcode;
    }
}
