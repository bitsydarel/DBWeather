package com.darelbitsy.dbweather.utils.utility.weather;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.util.Log;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.models.datatypes.weather.HourlyData;
import com.darelbitsy.dbweather.utils.helper.DatabaseOperation;
import com.darelbitsy.dbweather.models.api.adapters.GoogleGeocodeAdapter;
import com.darelbitsy.dbweather.models.datatypes.weather.Currently;
import com.darelbitsy.dbweather.models.datatypes.weather.DailyData;
import com.darelbitsy.dbweather.models.datatypes.weather.Weather;
import com.darelbitsy.dbweather.models.datatypes.weather.WeatherInfo;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.TAG;

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

    private final static HashMap<String, Integer> dayOfTheWeek = (HashMap<String, Integer>) Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault())
            .getDisplayNames(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

    public static Double[] getCoordinates(@NonNull final DatabaseOperation database) {
        return database.getCoordinates();

    }

    public static int getDewPointInCelsius(final double dewPoint) {
        return (int) Math.round((dewPoint - 32) * 5/9);
    }

    public static int getMoonPhase(final double moonPhase) {
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

    public static int getPrecipPercentage(final double precipProbability) {
        return (int) (precipProbability * 100);
    }

    public static int getTemperatureInInt(final double temperature) {
        return (int) Math.round(temperature);
    }

    public static int getHumidityPercentage(final double humidity) {
        return (int) (humidity * 100);
    }

    public static int getWindSpeedMeterPerHour(final double windSpeed) {
        return (int) Math.round(windSpeed);
    }

    public static int getCloudCoverPercentage(final double cloudCover) {
        return (int) cloudCover * 100;
    }

    public static String getHour(final long timeInMilliseconds, final String timeZone) {
        final DateTimeFormatter format =
                DateTimeFormatter.ofPattern("h a");

        return Instant.ofEpochSecond(timeInMilliseconds)
                .atZone(ZoneId.of(timeZone == null ? TimeZone.getDefault().getID() : timeZone))
                .format(format);
    }

    public static String getFormattedTime(final long timeInMilliseconds, final String timeZone) {
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
    public static String getLocationName(final Context context, final double latitude, final double longitude) throws IOException {
        String cityInfoBuilder;
        try {
            final Geocoder gcd = new Geocoder(context, Locale.getDefault());
            final List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
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

        } catch (final IOException e) {
            cityInfoBuilder = getLocationWithGoogleMapApi(latitude, longitude);
        }
        return cityInfoBuilder;
    }

    private static String getLocationWithGoogleMapApi(final double latitude, final double longitude) throws IOException {
        return new GoogleGeocodeAdapter().getLocationByCoordinate(latitude, longitude);
    }

    public static String getDayOfTheWeek(final long timeInMilliseconds, final String timeZone) {
        final DateTimeFormatter format =
                DateTimeFormatter.ofPattern("EEEE");

        return Instant.ofEpochSecond(timeInMilliseconds)
                .atZone(ZoneId.of(timeZone == null ? TimeZone.getDefault().getID() : timeZone))
                .format(format);
    }

    private static int compareDay(final long firstDay, final long secondDay, final String timeZone) {
        return dayOfTheWeek.get(WeatherUtil.getDayOfTheWeek(firstDay, timeZone))
                .compareTo(dayOfTheWeek.get(WeatherUtil.getDayOfTheWeek(secondDay, timeZone)));
    }

    public static int compareDay(final long firstDay, final long secondDay) {
        return compareDay(firstDay, secondDay, null);
    }

    private static boolean dayEquality(final long firstDay, final long secondDay, final String timeZone) {
        return dayOfTheWeek.get(WeatherUtil.getDayOfTheWeek(firstDay, timeZone))
                .equals(dayOfTheWeek.get(WeatherUtil.getDayOfTheWeek(secondDay, timeZone)));
    }

    public static boolean dayEquality(final long firstDay, final long secondDay) {
        return dayEquality(firstDay, secondDay, null);
    }


    public static int getIconId(@NonNull final String icon) {
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

    public static Pair<List<WeatherInfo>, List<HourlyData>> parseWeather(@NonNull final Weather weather, @NonNull final Context context) {
        final List<WeatherInfo> weatherInfoList = new ArrayList<>();
        final Calendar calendar = Calendar.getInstance();
        final String currentDayName = calendar.getDisplayName(Calendar.DAY_OF_WEEK,
                Calendar.LONG,
                Locale.getDefault());

        Log.i(TAG, "CITY NAME + " + weather.getCityName());

        int count = 0;
        boolean isTodaySet = false;
        boolean isTomorrowSet = false;

        Integer currentDayIndex = null;

        while (count < 7) {
            for (final DailyData day : weather.getDaily().getData()) {
                if (count == 7) {
                    break;
                }

                final WeatherInfo weatherInfo = new WeatherInfo();

                if (!isTodaySet &&
                        currentDayName.equalsIgnoreCase(WeatherUtil.getDayOfTheWeek(day.getTime(),
                                weather.getTimezone()))) {

                    count = 0;
                    final Currently currently = weather.getCurrently();

                    weatherInfo.isCurrentWeather.set(true);

                    weatherInfo.locationName.set(weather.getCityName());
                    weatherInfo.icon.set(WeatherUtil.getIconId(currently.getIcon()));
                    weatherInfo.summary.set(currently.getSummary());

                    if ("rain".equalsIgnoreCase(currently.getIcon())) {
                        weatherInfo.videoBackgroundFile.set(R.raw.rain_background);

                    } else if ("snow".equalsIgnoreCase(currently.getIcon())) {
                        weatherInfo.videoBackgroundFile.set(R.raw.snow_background);
                    }

                    if ("sleet".equalsIgnoreCase(currently.getIcon())) {
                        weatherInfo.setSleet(true);
                    }

                    weatherInfo.time.set(String.format(Locale.getDefault(),
                            context.getString(R.string.time_label),
                            WeatherUtil.getFormattedTime(currently.getTime(), weather.getTimezone())));

                    weatherInfo.temperature.set(WeatherUtil.getTemperatureInInt(currently.getTemperature()));
                    weatherInfo.apparentTemperature.set(WeatherUtil.getTemperatureInInt(currently.getApparentTemperature()));

                    weatherInfo.windSpeed.set(String.format(Locale.ENGLISH,
                            context.getString(R.string.humidity_value),
                            WeatherUtil.getWindSpeedMeterPerHour(currently.getWindSpeed())));

                    weatherInfo.humidity.set(String.format(Locale.ENGLISH,
                            context.getString(R.string.humidity_value),
                            WeatherUtil.getHumidityPercentage(currently.getHumidity())));

                    weatherInfo.cloudCover.set(String.format(Locale.ENGLISH,
                            context.getString(R.string.cloudCoverValue),
                            WeatherUtil.getCloudCoverPercentage(currently.getCloudCover())));

                    if (currently.getPrecipType() == null) {
                        weatherInfo.precipitationType.set(context.getString(R.string.precipitation_default_value));

                    } else {
                        weatherInfo.precipitationType.set(String.format(Locale.getDefault(),
                                context.getString(R.string.precipeChanceTypeLabel),
                                currently.getPrecipType()));
                    }

                    weatherInfo.precipitationProbability.set(String.format(Locale.getDefault(),
                            context.getString(R.string.precipChanceValue),
                            WeatherUtil.getPrecipPercentage(currently.getPrecipProbability())));

                    weatherInfo.sunrise.set(WeatherUtil.getFormattedTime(day.getSunriseTime(), weather.getTimezone()));
                    weatherInfo.sunset.set(WeatherUtil.getFormattedTime(day.getSunsetTime(), weather.getTimezone()));

                    weatherInfoList.add(count, weatherInfo);

                    currentDayIndex = count++;
                    isTodaySet = true;

                } else if (currentDayIndex != null
                        && count == (currentDayIndex + 1)) {

                    weatherInfoList.add(1, convertToWeatherInfo(weather.getCityName(),
                            day, weatherInfo, weather.getTimezone(), context));

                    count++;
                    isTomorrowSet = true;

                } else if (isTodaySet && isTomorrowSet) {

                    weatherInfoList.add(count,
                            convertToWeatherInfo(weather.getCityName(), day, weatherInfo, weather.getTimezone(), context));

                    count++;
                }
            }
        }
        return new Pair<>(weatherInfoList, weather.getHourly().getData());
    }

    private static WeatherInfo convertToWeatherInfo(@NonNull final String locationName,
                                             @NonNull final DailyData day,
                                             @NonNull final WeatherInfo weatherInfo,
                                             @Nullable final String timeZone,
                                             @NonNull final Context context) {

        weatherInfo.isCurrentWeather.set(false);

        weatherInfo.locationName.set(locationName);
        weatherInfo.icon.set(WeatherUtil.getIconId(day.getIcon()));
        weatherInfo.summary.set(day.getSummary());

        weatherInfo.time.set(WeatherUtil.getDayOfTheWeek(day.getTime(), timeZone));

        weatherInfo.temperature.set(WeatherUtil.getTemperatureInInt(day.getTemperatureMax()));
        weatherInfo.apparentTemperature.set(WeatherUtil.getTemperatureInInt(day.getApparentTemperatureMax()));

        weatherInfo.windSpeed.set(String.format(Locale.ENGLISH,
                context.getString(R.string.humidity_value),
                WeatherUtil.getWindSpeedMeterPerHour(day.getWindSpeed())));

        weatherInfo.humidity.set(String.format(Locale.ENGLISH,
                context.getString(R.string.humidity_value),
                WeatherUtil.getHumidityPercentage(day.getHumidity())));

        weatherInfo.cloudCover.set(String.format(Locale.ENGLISH,
                context.getString(R.string.cloudCoverValue),
                WeatherUtil.getCloudCoverPercentage(day.getCloudCover())));

        if (day.getPrecipType() == null) {
            weatherInfo.precipitationType
                    .set(context.getString(R.string.precipitation_default_value));

        } else {
            weatherInfo.precipitationType
                    .set(String.format(Locale.getDefault(),
                            context.getString(R.string.precipeChanceTypeLabel),
                            day.getPrecipType()));
        }

        weatherInfo.precipitationProbability.set(String.format(Locale.getDefault(),
                context.getString(R.string.precipChanceValue),
                WeatherUtil.getPrecipPercentage(day.getPrecipProbability())));

        weatherInfo.sunrise.set(WeatherUtil.getFormattedTime(day.getSunriseTime(), timeZone));
        weatherInfo.sunset.set(WeatherUtil.getFormattedTime(day.getSunsetTime(), timeZone));

        return weatherInfo;
    }
}