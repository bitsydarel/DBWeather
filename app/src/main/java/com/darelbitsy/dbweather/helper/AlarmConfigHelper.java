package com.darelbitsy.dbweather.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.darelbitsy.dbweather.adapters.database.DatabaseOperation;
import com.darelbitsy.dbweather.helper.holder.ConstantHolder;
import com.darelbitsy.dbweather.helper.receiver.AlarmWeatherReceiver;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static com.darelbitsy.dbweather.helper.holder.ConstantHolder.PREFS_NAME;

/**
 * Created by Darel Bitsy on 30/01/17.
 * Alarm manager config for notification
 */

public class AlarmConfigHelper {
    public static final String MY_ACTION = "com.darelbitsy.dbweather.ACTIVATE_NOTIFICATION";
    public static final String LAST_NOTIFICATION_PENDING_INTENT_ID = "last_pending_intent_id";

    private final AlarmManager mAlarmManagerMorning;
    private final AlarmManager mAlarmManagerAfternoon;
    private final AlarmManager mAlarmManagerNight;
    private final PendingIntent mPendingIntentMorning;
    private final PendingIntent mPendingIntentAfternoon;
    private final PendingIntent mPendingIntentNight;

    private Calendar calendarMorning;
    private Calendar calendarAfternoon;
    private Calendar calendarNight;

    private Calendar currentCalendar;
    private PendingIntent currentPendingIntent;

    private final Context mContext;

    public AlarmConfigHelper(final Context context) {
        mContext = context;
        mAlarmManagerMorning = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        mAlarmManagerAfternoon = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        mAlarmManagerNight = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        final Intent notificationLIntent = new Intent(context, AlarmWeatherReceiver.class);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            notificationLIntent.setFlags(0);
        } else {
            notificationLIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        notificationLIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        notificationLIntent.setAction(MY_ACTION);

        mPendingIntentMorning = PendingIntent.getBroadcast(context,
                7124,
                notificationLIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mPendingIntentAfternoon = PendingIntent.getBroadcast(context,
                7125,
                notificationLIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mPendingIntentNight = PendingIntent.getBroadcast(context,
                7126,
                notificationLIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AndroidThreeTen.init(context);
    }

    private AlarmManager getCurrentAlarm() {
        final Date currentDate = new Date();

        if (calendarMorning.getTime().compareTo(currentDate) > 0) {
            currentCalendar = calendarMorning;
            currentPendingIntent = mPendingIntentMorning;
            mContext.getSharedPreferences(PREFS_NAME, mContext.MODE_PRIVATE)
                    .edit()
                    .putInt(LAST_NOTIFICATION_PENDING_INTENT_ID, 7124)
                    .apply();
            return mAlarmManagerMorning;
        }

        if (calendarAfternoon.getTime().compareTo(currentDate) > 0) {
            currentCalendar = calendarAfternoon;
            currentPendingIntent = mPendingIntentAfternoon;
            mContext.getSharedPreferences(PREFS_NAME, mContext.MODE_PRIVATE)
                    .edit()
                    .putInt(LAST_NOTIFICATION_PENDING_INTENT_ID, 7125)
                    .apply();
            return mAlarmManagerAfternoon;
        }
        if (calendarNight.getTime().compareTo(currentDate) > 0) {
            currentCalendar = calendarNight;
            currentPendingIntent = mPendingIntentNight;
            mContext.getSharedPreferences(PREFS_NAME, mContext.MODE_PRIVATE)
                    .edit()
                    .putInt(LAST_NOTIFICATION_PENDING_INTENT_ID, 7126)
                    .apply();
            return mAlarmManagerNight;
        }

        calendarMorning.add(Calendar.DATE, +1);
        currentCalendar = calendarMorning;
        currentPendingIntent = mPendingIntentMorning;
        mContext.getSharedPreferences(PREFS_NAME, mContext.MODE_PRIVATE)
                .edit()
                .putInt(LAST_NOTIFICATION_PENDING_INTENT_ID, 7124)
                .apply();
        return mAlarmManagerMorning;
    }

    private void setAlarm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getCurrentAlarm().setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    currentCalendar.getTimeInMillis(),
                    currentPendingIntent);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final AlarmManager alarmManager = getCurrentAlarm();
            final AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(currentCalendar.getTimeInMillis(), currentPendingIntent);
            alarmManager.setAlarmClock(alarmClockInfo, currentPendingIntent);

        } else  if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
            getCurrentAlarm().setExact(AlarmManager.RTC_WAKEUP,
                    currentCalendar.getTimeInMillis(),
                    currentPendingIntent);

        } else {
            getCurrentAlarm().setRepeating(AlarmManager.RTC_WAKEUP,
                    currentCalendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    currentPendingIntent);
        }
        Log.i(ConstantHolder.TAG,
                "Set Alarm for Date: "+currentCalendar.getTime().toString());
    }

    public void cancelClothingNotificationAlarm() {
        mAlarmManagerMorning.cancel(mPendingIntentMorning);
        mAlarmManagerAfternoon.cancel(mPendingIntentAfternoon);
        mAlarmManagerNight.cancel(mPendingIntentNight);
    }

    private void setCalendars() {
        final TimeZone timeZone = TimeZone.getTimeZone(getCurrentTimeZone(mContext));
        final Date currentDate = new Date();

        calendarMorning = Calendar.getInstance(timeZone, Locale.getDefault());
        calendarMorning.setTime(currentDate);
        calendarMorning.set(Calendar.HOUR_OF_DAY, 7);
        calendarMorning.set(Calendar.MINUTE, 30);
        calendarMorning.set(Calendar.SECOND, 0);

        calendarAfternoon = Calendar.getInstance(timeZone, Locale.getDefault());
        calendarAfternoon.setTime(currentDate);
        calendarAfternoon.set(Calendar.HOUR_OF_DAY, 12);
        calendarAfternoon.set(Calendar.MINUTE, 30);
        calendarAfternoon.set(Calendar.SECOND, 0);

        calendarNight = Calendar.getInstance(timeZone, Locale.getDefault());
        calendarNight.setTime(currentDate);
        calendarNight.set(Calendar.HOUR_OF_DAY, 18);
        calendarNight.set(Calendar.MINUTE, 30);
        calendarNight.set(Calendar.SECOND, 0);
    }

    private static String getCurrentTimeZone(final Context context) {
        final String timezone = DatabaseOperation.newInstance(context)
                .getWeatherData().getTimezone();
        return timezone == null ? TimeZone.getDefault().getID() : timezone;
    }

    public void setClothingNotificationAlarm() {
        setCalendars();
        setAlarm();
        Log.i(ConstantHolder.TAG, "ALARM DONE AT "+System.currentTimeMillis());
    }
}
