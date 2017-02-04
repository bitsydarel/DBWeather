package com.darelbitsy.dbweather.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.darelbitsy.dbweather.adapters.DatabaseOperation;
import com.darelbitsy.dbweather.receiver.AlarmWeatherReceiver;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Darel Bitsy on 30/01/17.
 */

public class AlarmConfigHelper {
    private final AlarmManager mAlarmManagerMorning;
    private final AlarmManager mAlarmManagerAfternoon;
    private final AlarmManager mAlarmManagerNight;
    private final PendingIntent mPendingIntentMorning;
    private final PendingIntent mPendingIntentAfternoon;
    private final PendingIntent mPendingIntentNight;

    private Calendar calendarMorning;
    private Calendar calendarAfternoon;
    private Calendar calendarNight;
    private final Context mContext;

    public AlarmConfigHelper(final Context context) {
        mContext = context;
        mAlarmManagerMorning = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        mAlarmManagerAfternoon = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        mAlarmManagerNight = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        mPendingIntentMorning = PendingIntent.getBroadcast(context,
                7124,
                new Intent(context, AlarmWeatherReceiver.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        mPendingIntentAfternoon = PendingIntent.getBroadcast(context,
                7125,
                new Intent(context, AlarmWeatherReceiver.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        mPendingIntentNight = PendingIntent.getBroadcast(context,
                7126,
                new Intent(context, AlarmWeatherReceiver.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        setCalendars();
    }

    public void setClothingNotificationAlarm() {
        setAlarm();
        Log.i("Feed", "ALARM DONE AT "+System.currentTimeMillis());
    }

    private void setAlarm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mAlarmManagerMorning.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    calendarMorning.getTimeInMillis(),
                    mPendingIntentMorning);

            mAlarmManagerAfternoon.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    calendarAfternoon.getTimeInMillis(),
                    mPendingIntentAfternoon);

            mAlarmManagerNight.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    calendarNight.getTimeInMillis(),
                    mPendingIntentNight);

        } else  if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
                mAlarmManagerMorning.setExact(AlarmManager.RTC_WAKEUP,
                        calendarMorning.getTimeInMillis(),
                        mPendingIntentMorning);

                mAlarmManagerAfternoon.setExact(AlarmManager.RTC_WAKEUP,
                        calendarAfternoon.getTimeInMillis(),
                        mPendingIntentAfternoon);

                mAlarmManagerNight.setExact(AlarmManager.RTC_WAKEUP,
                        calendarNight.getTimeInMillis(),
                        mPendingIntentNight);

        } else {
            mAlarmManagerMorning.setRepeating(AlarmManager.RTC_WAKEUP,
                    calendarMorning.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    mPendingIntentMorning);

            mAlarmManagerAfternoon.setRepeating(AlarmManager.RTC_WAKEUP,
                    calendarAfternoon.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    mPendingIntentAfternoon);

            mAlarmManagerNight.setRepeating(AlarmManager.RTC_WAKEUP,
                    calendarNight.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    mPendingIntentNight);
        }
        Log.i("Feed_Data",
                "Morning: "+calendarMorning.getTime().toString()+
                        " Afternoon: "+ calendarAfternoon.getTime().toString() +
                        " Night: " + calendarNight.getTime().toString());

        Toast.makeText(mContext,
                "Morning: "+calendarMorning.getTimeInMillis() +
                        " Afternoon: "+ calendarAfternoon.getTimeInMillis() +
                        " Night: " + calendarNight.getTimeInMillis(),
                Toast.LENGTH_LONG).show();
    }

    public void cancelClothingNotificationAlarm() {
        mAlarmManagerMorning.cancel(mPendingIntentMorning);
        mAlarmManagerAfternoon.cancel(mPendingIntentAfternoon);
        mAlarmManagerNight.cancel(mPendingIntentNight);
    }

    private void setCalendars() {
        final TimeZone timeZone = TimeZone.getTimeZone(getCurrentTimeZone(mContext));

        calendarMorning = Calendar.getInstance(timeZone, Locale.getDefault());
        calendarMorning.set(Calendar.HOUR_OF_DAY, 7);
        calendarMorning.set(Calendar.MINUTE, 30);
        calendarMorning.set(Calendar.SECOND, 0);

        calendarAfternoon = Calendar.getInstance(timeZone, Locale.getDefault());
        calendarAfternoon.set(Calendar.HOUR_OF_DAY, 12);
        calendarAfternoon.set(Calendar.MINUTE, 30);
        calendarAfternoon.set(Calendar.SECOND, 0);

        calendarNight = Calendar.getInstance(timeZone, Locale.getDefault());
        calendarNight.set(Calendar.HOUR_OF_DAY, 18);
        calendarNight.set(Calendar.MINUTE, 30);
        calendarNight.set(Calendar.SECOND, 0);
    }

    public static String getCurrentTimeZone(final Context context) {
        String timezone = new DatabaseOperation(context)
                .getCurrentWeatherFromDatabase().getTimeZone();
        return timezone == null ? TimeZone.getDefault().getID() : timezone;
    }
}
