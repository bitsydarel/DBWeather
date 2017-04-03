package com.darelbitsy.dbweather.helper.utility;

import org.geonames.Toponym;
import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.geonames.WebService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;

/**
 * Created by Darel Bitsy on 02/04/17.
 * Utility class to access city and country location
 */

public class LocationFinderUtility {
    private LocationFinderUtility(){
//        Utility class so empty constructor
    }

    public static Single<List<Toponym>> getLocationInfoFromName(String query) {
        return Single.create(emitter -> {
            try {
                ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
                searchCriteria.setQ(query);
                WebService.setConnectTimeOut(25);
                WebService.setReadTimeOut(25);
                WebService.setUserName("bitsydarel");

                ToponymSearchResult searchResult = WebService.search(searchCriteria);
                if (!emitter.isDisposed()) { emitter.onSuccess(searchResult.getToponyms()); }

            } catch (Exception e) { if (!emitter.isDisposed()) { emitter.onError(e); }  }
        });
    }

    public static Single<List<Toponym>> mockGetLocationInfoFromName(String query) {
        return Single.create( emitter -> {
            List<Toponym> toponymList = new ArrayList<>();
            Toponym one = new Toponym();
            one.setName("Ternopil");
            one.setCountryName("Ukraine");
            one.setContinentCode("East Europe");

            Toponym two = new Toponym();
            two.setName("Kiev");
            two.setCountryName("Ukraine");
            two.setContinentCode("East Europe");

            Toponym three = new Toponym();
            three.setName("Odessa");
            three.setCountryName("Ukraine");
            three.setContinentCode("East Europe");

            Toponym four = new Toponym();
            four.setName("Lviv");
            four.setCountryName("Ukraine");
            four.setContinentCode("East Europe");

            toponymList.add(one);
            toponymList.add(two);
            toponymList.add(three);
            toponymList.add(four);
            try {
                emitter.onSuccess(toponymList);

            } catch (Exception e) { if (!emitter.isDisposed()) { emitter.onError(e); } }
        });
    }
}
