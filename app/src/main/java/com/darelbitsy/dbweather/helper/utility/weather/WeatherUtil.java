package com.darelbitsy.dbweather.helper.utility.weather;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.adapters.database.DatabaseOperation;
import com.darelbitsy.dbweather.controller.api.adapters.network.GoogleGeocodeAdapter;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Darel Bitsy on 19/02/17.
 * Weather Utility class
 * That as useful method
 */

public class WeatherUtil {
    private WeatherUtil() {
        //Empty, because it's an utility class
        //So it's doesn't need to be initiated
    }

    private static HashMap<String, Integer> dayOfTheWeek = (HashMap<String, Integer>) Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault())
            .getDisplayNames(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

    public static Double[] getCoordinates(DatabaseOperation database) {
        return database.getCoordinates();

    }

    public static void saveCoordinates(double latitude, double longitude, DatabaseOperation database) {
        database.saveCoordinates(latitude, longitude);

    }

    public static int getDewPointInCelsius(double dewPoint) {
        return (int) Math.round((dewPoint - 32) * 5/9);
    }

    public static int getMoonPhase(double moonPhase) {
        if (moonPhase >= 0.75 ) {
            return R.drawable.last_quater_moon;
        }
        if (moonPhase >= 0.5 ) {
            return R.drawable.full_moon;
        }
        if (moonPhase >= 0.25 ) {
            return R.drawable.first_quater_moon;
        }
        return R.drawable.new_moon;
    }

    public static int getPrecipPourcentage(double precipProbability) {
        return (int) (precipProbability * 100);
    }

    public static int getTemperatureInInt(double temperature) {
        return (int) Math.round(temperature);
    }

    public static int getHumidityPourcentage(double humidity) {
        return (int) (humidity * 100);
    }

    public static int getWindSpeedMeterPerHour(double windSpeed) {
        return (int) Math.round(windSpeed);
    }

    public static int getCloudCoverPourcentage(double cloudCover) {
        return (int) cloudCover * 100;
    }

    public static String getHour(long timeInMilliseconds, String timeZone) {
        final DateTimeFormatter format =
                DateTimeFormatter.ofPattern("h a");

        return Instant.ofEpochSecond(timeInMilliseconds)
                .atZone(ZoneId.of(timeZone == null ? TimeZone.getDefault().getID() : timeZone))
                .format(format);
    }

    public static String getFormattedTime(long timeInMilliseconds, String timeZone) {
        final DateTimeFormatter format =
                DateTimeFormatter.ofPattern("h:mm a");

        return Instant.ofEpochSecond(timeInMilliseconds)
                .atZone(ZoneId.of(timeZone == null ? TimeZone.getDefault().getID() : timeZone))
                .format(format);
    }

    /**
     * Get Location name based on mLatitude and mLongitude
     * @param latitude the mLatitude from location
     * @param longitude the mLongitude from location
     * @return the location in format (City, Country)
     */
    public static String getLocationName(Context context, double latitude, double longitude) throws IOException {
        String cityInfoBuilder;
        try {
            Geocoder gcd = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                if (addresses.get(0).getMaxAddressLineIndex() > 2) {
                    cityInfoBuilder = String.format(Locale.getDefault(), "%s, %s, %s, %s",
                            addresses.get(0).getAddressLine(0),
                            addresses.get(0).getAddressLine(1),
                            addresses.get(0).getAddressLine(2),
                            addresses.get(0).getAddressLine(3));

                } else {
                    cityInfoBuilder = String.format(Locale.getDefault(), "%s, %s, %s",
                            addresses.get(0).getAddressLine(0),
                            addresses.get(0).getLocality(),
                            addresses.get(0).getCountryName());

                }

            } else { throw new IOException(); }
            if (cityInfoBuilder.contains("null")) { throw new IOException(); }

        } catch (IOException e) {
            cityInfoBuilder = getLocationWithGoogleMapApi(latitude, longitude);
        }
        return cityInfoBuilder;
    }

    private static String getLocationWithGoogleMapApi(double latitude, double longitude) throws IOException {
        return new GoogleGeocodeAdapter().getLocationByCoordinate(latitude, longitude);
    }

    public static String getDayOfTheWeek(long timeInMiliseconds, String timeZone) {
        final DateTimeFormatter format =
                DateTimeFormatter.ofPattern("EEEE");

        return Instant.ofEpochSecond(timeInMiliseconds)
                .atZone(ZoneId.of(timeZone == null ? TimeZone.getDefault().getID() : timeZone))
                .format(format);
    }

    private static int compareDay(long firstDay, long secondDay, String timeZone) {
        return dayOfTheWeek.get(WeatherUtil.getDayOfTheWeek(firstDay, timeZone))
                .compareTo(dayOfTheWeek.get(WeatherUtil.getDayOfTheWeek(secondDay, timeZone)));
    }

    public static int compareDay(long firstDay, long secondDay) {
        return compareDay(firstDay, secondDay, null);
    }

    private static boolean dayEquality(long firstDay, long secondDay, String timeZone) {
        return dayOfTheWeek.get(WeatherUtil.getDayOfTheWeek(firstDay, timeZone))
                .equals(dayOfTheWeek.get(WeatherUtil.getDayOfTheWeek(secondDay, timeZone)));
    }

    public static boolean dayEquality(long firstDay, long secondDay) {
        return dayEquality(firstDay, secondDay, null);
    }


    public static int getIconId(String icon) {
        int iconId = R.drawable.clear_day;
        switch (icon) {
            case "clear-night":
                iconId = R.drawable.clear_night;
                break;
            case "rain":
                iconId = R.drawable.rain;
                break;
            case "snow":
                iconId = R.drawable.snow;
                break;
            case "sleet":
                iconId = R.drawable.sleet;
                break;
            case "wind":
                iconId = R.drawable.wind;
                break;
            case "fog":
                iconId = R.drawable.fog;
                break;
            case "cloudy":
                iconId = R.drawable.cloudy;
                break;
            case "partly-cloudy-day":
                iconId = R.drawable.partly_cloudy;
                break;
            case "partly-cloudy-night":
                iconId = R.drawable.cloudy_night;
                break;
            default:
                break;
        }
        return iconId;
    }
}
