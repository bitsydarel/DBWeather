package com.darelbitsy.dbweather.model.geonames;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Darel Bitsy on 03/04/17.
 * My implementation of the GeoName Api
 * With xml conversion support
 */

@Root(name = "geonames")
public class GeoNamesResult {

    @Element(name = "totalResultsCount")
    private long totalResultsCount;

    @Attribute(name = "style")
    private String style;

    @ElementList(entry = "geoname", inline = true, type = GeoName.class)
    private List<GeoName> mGeoName;

    public long getTotalResultsCount() {
        return totalResultsCount;
    }

    public void setTotalResultsCount(long totalResultsCount) {
        this.totalResultsCount = totalResultsCount;
    }

    public List<GeoName> getGeoName() {
        return mGeoName;
    }

    public void setGeoName(List<GeoName> geoName) {
        mGeoName = geoName;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }
}
