package phoneaddiction.easyapps.ms.com.application.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * for shared preferences
 */

public class SPUtil {
    private static final String SHARED_PREFS_FILE = "easy_apps_prefs";
    private static final String TAG = "SPUtils";

    public static class SPKeys{
        public static final String KEY_USAGE_DATA_BELOW_L = "usage_data_below_l";
        public static final String KEY_MOST_RECENT = "recent_app";
        public static final String LAST_SELECTION = "last_selection";
        public static final String DAILY_LIMIT_MAP ="daily_limit_map";
        public static final String WEEKLY_LIMIT_MAP ="weekly_limit_map";
        public static final String LAST_EVENT_TIME = "last_event_time";
        public static final String PHONE_UNLOCK_DATA = "phone_unlock_data";
        public static final String LAST_EVENT = "last_event";
        public static final String TOTAL_ACTIVE_TIME_TODAY="active_time";
        public static final String PHONE_LIMIT="phone_time";
    }

    /**
     * Set a String value in the preferences editor, to be written back once
     * apply or apply are called.
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference. Supplying null
     *    as the value is equivalent to calling remove String with
     *    this key.
     * @return Returns true if the new values were successfully written
     * to persistent storage.
     */
    public static void putString(Context context , String key, String value) {
        if(context==null || context.getApplicationContext()==null){
            return;
        }
        try {
            SharedPreferences mSharedPreferences = context.getApplicationContext().getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
            LogUtils.info(TAG, value);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(key, value);
            editor.apply();
        }catch (Exception e){
            LogUtils.error(TAG ,"error in shared preference", e);
        }
    }

    /**
     * Set a String value in the preferences editor, to be written back once
     * apply or apply are called.
     *
     * @param key   The name of the preference to modify.
     * @return Returns true if the new values were successfully written
     * to persistent storage.
     */
    public static String getString(Context context , String key,@NonNull String defaultValue) {
        if(context==null || context.getApplicationContext()==null){
            return defaultValue;
        }
        try {
            SharedPreferences mSharedPreferences = context.getApplicationContext().getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
            return mSharedPreferences.getString(key,defaultValue);
        }catch (Exception e){
            LogUtils.error(TAG ,"error in shared preference", e);
        }
        return defaultValue;
    }

    /**
     * Set a String value in the preferences editor, to be written back once
     * apply or apply are called.
     *
     * @param key   The name of the preference to modify.
     * @return Returns true if the new values were successfully written
     * to persistent storage.
     */
    public static int getInt(Context context , String key, int defaultValue) {
        if(context==null || context.getApplicationContext()==null){
            return defaultValue;
        }
        try {
            SharedPreferences mSharedPreferences = context.getApplicationContext().getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
            return mSharedPreferences.getInt(key,defaultValue);
        }catch (Exception e){
            LogUtils.error(TAG ,"error in shared preference", e);
        }
        return defaultValue;
    }

    /**
     * Set a String value in the preferences editor, to be written back once
     * apply or apply are called.
     *
     * @param key   The name of the preference to modify.
     * @return Returns true if the new values were successfully written
     * to persistent storage.
     */
    public static long getLong(Context context , String key, long defaultValue) {
        if(context==null || context.getApplicationContext()==null){
            return defaultValue;
        }
        try {
            SharedPreferences mSharedPreferences = context.getApplicationContext().getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
            return mSharedPreferences.getLong(key,defaultValue);
        }catch (Exception e){
            LogUtils.error(TAG ,"error in shared preference", e);
        }
        return defaultValue;
    }

    /**
     * Set a String value in the preferences editor, to be written back once
     * apply or apply are called.
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference. Supplying null
     *    as the value is equivalent to calling remove String with
     *    this key.
     * @return Returns true if the new values were successfully written
     * to persistent storage.
     */
    public static void putInt(Context context , String key, int value) {
        if(context==null || context.getApplicationContext()==null){
            return;
        }
        try {
            SharedPreferences mSharedPreferences = context.getApplicationContext().getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putInt(key, value);
            editor.apply();
        }catch (Exception e){
            LogUtils.error(TAG ,"error in shared preference", e);
        }
    }

    /**
     * Set a String value in the preferences editor, to be written back once
     * apply or apply are called.
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference. Supplying null
     *    as the value is equivalent to calling remove String with
     *    this key.
     * @return Returns true if the new values were successfully written
     * to persistent storage.
     */
    public static void putLong(Context context , String key, long value) {
        if(context==null || context.getApplicationContext()==null){
            return;
        }
        try {
            SharedPreferences mSharedPreferences = context.getApplicationContext().getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putLong(key, value);
            editor.apply();
        }catch (Exception e){
            LogUtils.error(TAG ,"error in shared preference", e);
        }
    }


}
