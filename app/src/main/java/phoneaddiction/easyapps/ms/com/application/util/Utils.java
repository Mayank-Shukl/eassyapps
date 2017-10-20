package phoneaddiction.easyapps.ms.com.application.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Retention;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phoneaddiction.easyapps.ms.com.application.R;
import phoneaddiction.easyapps.ms.com.application.model.AppInfo;
import phoneaddiction.easyapps.ms.com.application.model.AppInfoBelowL;
import phoneaddiction.easyapps.ms.com.application.service.GsonUtils;

import static java.lang.annotation.RetentionPolicy.SOURCE;
import static phoneaddiction.easyapps.ms.com.application.Constants.TIME_LAST_24;
import static phoneaddiction.easyapps.ms.com.application.Constants.TIME_MONTH;
import static phoneaddiction.easyapps.ms.com.application.Constants.TIME_TODAY;
import static phoneaddiction.easyapps.ms.com.application.Constants.TIME_WEEK;
import static phoneaddiction.easyapps.ms.com.application.util.SPUtil.SPKeys.KEY_USAGE_DATA_BELOW_L;

/**
 * Created by MMT5762 on 02-07-2017.
 */

public class Utils {

    private static final String TAG = "UTILS";

    public static boolean isBelowAndroidL() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isEmptyCollection(Collection list) {
        return list == null || list.isEmpty();
    }

    public static boolean isEmptyMap(Map map) {
        return map == null || map.isEmpty();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static boolean isUsedApp(UsageStats usageStats) {
        return usageStats != null && usageStats.getPackageName() != null && usageStats.getTotalTimeInForeground() > 0;
    }

    public static boolean isOtherVisibleApp(Context context, String packageName) {
        return packageName != null && !context.getPackageName().equals(packageName)
                && context.getPackageManager().getLaunchIntentForPackage(packageName) != null;
    }

    public static int getWidthInDp(Activity activity){
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        return (int)(displayMetrics.widthPixels/displayMetrics.density);

    }

    public static long getAppInstallTime(Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo appInfo = null;
        try {
            appInfo = pm.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appInfo.firstInstallTime;
    }

    /**
     * @param usageStatsManager
     * @param diff
     * @return
     */
    public static String crossCheckForeGroundApp(UsageStatsManager usageStatsManager, long diff) {
        long time = System.currentTimeMillis();
        String packageName = null;
        diff = diff - 10 * DateUtils.SECOND_IN_MILLIS;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageEvents usageEvents = usageStatsManager.queryEvents(diff, time);
            UsageEvents.Event event = new UsageEvents.Event();
            // get last foreground event so that we can get last app in foreground
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND || event.getEventType() == UsageEvents.Event.USER_INTERACTION  ) {
                    packageName = event.getPackageName();
                }
            }
        }
        return packageName;
    }


    public static void setAppInfoForPackageName(Context context, String packageName, @NonNull AppInfo appInfo) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            appInfo.appName = applicationInfo.loadLabel(packageManager).toString();
            appInfo.packageName = packageName;
            appInfo.icon = applicationInfo.loadIcon(packageManager);
            setAppType(applicationInfo, appInfo);
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.error(TAG, "error in getting package detail", e);
        }
    }


    public static void setAppType(ApplicationInfo applicationInfo, AppInfo appInfo) {
        if ((applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1) {
            appInfo.setAppType("SYSTEM UPDATED");
        } else if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
            appInfo.setAppType("SYSTEM");
        } else {
            appInfo.setAppType("USER");
        }
    }

    @SuppressLint("WrongConstant")
    public static long getStartOfDay() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Log.d("mayank", "today " + cal.getTimeInMillis());
        return cal.getTimeInMillis();
    }

    public synchronized static Map<String, Integer> getLimitMap(Context context, String key) {
        String json = SPUtil.getString(context, key, null);
        Type type = new TypeToken<Map<String, Integer>>() {
        }.getType();
        return GsonUtils.deserializeJSON(json, type);
    }

    public static Integer getLimitForPackage(Context context, String packageName) {
        Map<String, Integer> map = getLimitMap(context, SPUtil.SPKeys.DAILY_LIMIT_MAP);
        return map.get(packageName);
    }

    public synchronized static void setLimitMap(Context context, String key, Map<String, Integer> map) {
        String json = GsonUtils.serializeToJson(map, HashMap.class);
        SPUtil.putString(context, key, json);
    }

    public synchronized static void removeLimit(Context context, String packageName) {
        Map<String, Integer> map = getLimitMap(context, SPUtil.SPKeys.DAILY_LIMIT_MAP);
        map.remove(packageName);
        setLimitMap(context, SPUtil.SPKeys.DAILY_LIMIT_MAP, map);
    }


    @Retention(SOURCE)
    @IntDef({
            TIME_TODAY,
            TIME_LAST_24,
            TIME_WEEK,
            TIME_MONTH
    })
    public @interface TimeTypes {
    }

    public static long getStartTimeFor(@TimeTypes int type) {
        switch (type) {
            case TIME_TODAY:
                return getStartOfDay();
            case TIME_LAST_24:
                return System.currentTimeMillis() - DateUtils.DAY_IN_MILLIS;
            case TIME_WEEK:
                return System.currentTimeMillis() - DateUtils.WEEK_IN_MILLIS;
            case TIME_MONTH:
                return System.currentTimeMillis() - DateUtils.DAY_IN_MILLIS * 30;
            default:
                return System.currentTimeMillis() - 100 * 1000L;

        }
    }

    public static boolean isFragmentActive(Fragment fragment) {
        return fragment != null && fragment.isAdded() && fragment.getView() != null && !fragment.isDetached();
    }

    public static int getIntervalType(@TimeTypes int type) {
        switch (type) {
            case TIME_TODAY:
                return UsageStatsManager.INTERVAL_DAILY;
            case TIME_LAST_24:
                return UsageStatsManager.INTERVAL_DAILY;
            case TIME_WEEK:
                return UsageStatsManager.INTERVAL_WEEKLY;
            case TIME_MONTH:
                return UsageStatsManager.INTERVAL_MONTHLY;
            default:
                return UsageStatsManager.INTERVAL_BEST;

        }
    }

    @org.jetbrains.annotations.Contract("null -> false")
    public static boolean isNullOrEmpty(String s) {
        return s != null && s.trim().length() <= 0;
    }

    public static void saveObjectToPreference(Context context, Object object, String key) {
        String data = GsonUtils.serializeToJson(object, object.getClass());
        SPUtil.putString(context, key, data);
    }

    public static Map<String, AppInfoBelowL> getUsageMapFromPreference(Context context) {
        String data = SPUtil.getString(context, KEY_USAGE_DATA_BELOW_L, "");
        java.lang.reflect.Type type = new TypeToken<HashMap<String, AppInfoBelowL>>() {
        }.getType();
        return GsonUtils.deserializeJSON(data, type);
    }


    public static void sortAppInfoTimeConsumed(ArrayList<AppInfo> appInfos) {
        Collections.sort(appInfos, new Comparator<AppInfo>() {
            @Override
            public int compare(AppInfo o1, AppInfo o2) {
                return o1.timeConsumed > o2.timeConsumed ? -1 : 1;
            }
        });
    }

    public static String getStringFormattedTime(long totalSeconds) {

        StringBuilder formattedTime = new StringBuilder();
        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;

        int seconds = (int) totalSeconds % SECONDS_IN_A_MINUTE;
        formattedTime.insert(0, seconds + "s");
        int totalMinutes = (int) totalSeconds / SECONDS_IN_A_MINUTE;
        int minutes = totalMinutes % MINUTES_IN_AN_HOUR;
        if (minutes > 0) {
            formattedTime.insert(0, minutes + "m ");
        }
        int hours = totalMinutes / MINUTES_IN_AN_HOUR;
        if (hours > 0) {
            formattedTime.insert(0, hours + "h ");
        }


        return formattedTime.toString();
    }

   /* public static ArrayList<Entry> getListForPieChart(Context context){
        ArrayList<Entry> list = new ArrayList<>();
        Map<String,Double> categoryMap = new HashMap<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            long endTime = System.currentTimeMillis();
            int interval = SPUtil.getInt(context, SPUtil.SPKeys.LAST_SELECTION, 0);
            long startTime = Utils.getStartTimeFor(interval);
            int intervalType = Utils.getIntervalType(interval);
            final UsageStatsManager usageStatsManager = (UsageStatsManager)context.getSystemService("usagestats");
            final List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(intervalType, startTime, endTime);
            for (UsageStats usageStats : queryUsageStats) {
                if (isUsedApp(usageStats) && isOtherVisibleApp(context, usageStats.getPackageName())) {

                }
            }

        }
        ret
    }*/

    private static Map<String,List<String>> getMapOfPackageNames(Context context){
        Map<String,List<String>> map = new HashMap<>();
        map.put("social", Arrays.asList(context.getResources().getStringArray(R.array.social)));
        map.put("dating", Arrays.asList(context.getResources().getStringArray(R.array.dating)));
        map.put("shopping", Arrays.asList(context.getResources().getStringArray(R.array.shoping)));
        map.put("chat", Arrays.asList(context.getResources().getStringArray(R.array.talk_chat)));
        map.put("streaming", Arrays.asList(context.getResources().getStringArray(R.array.video_stream)));
        return map;
    }

    public static long getTimeInForeGroundForPackageAboveL(long startTime, Context context, String packageName) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP || isNullOrEmpty(packageName)) {
            return -1;
        }
        long timeInForeground = 0;
        final UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");
        final List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, System.currentTimeMillis());
        for (UsageStats usageStats : queryUsageStats) {
            if (usageStats == null || usageStats.getFirstTimeStamp() < startTime) {
                continue;
            }
            timeInForeground = timeInForeground + usageStats.getTotalTimeInForeground();
        }
        return 0;
    }
}
