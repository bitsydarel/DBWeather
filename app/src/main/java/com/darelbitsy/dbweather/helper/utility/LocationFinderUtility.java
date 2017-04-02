package com.darelbitsy.dbweather.helper.utility;

import org.geonames.Toponym;
import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.geonames.WebService;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by Darel Bitsy on 02/04/17.
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
}
