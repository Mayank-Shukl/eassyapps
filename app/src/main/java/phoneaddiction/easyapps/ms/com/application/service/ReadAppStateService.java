package phoneaddiction.easyapps.ms.com.application.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.format.DateUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import phoneaddiction.easyapps.ms.com.application.Constants;
import phoneaddiction.easyapps.ms.com.application.InterruptDialogActivity;
import phoneaddiction.easyapps.ms.com.application.model.AppInfo;
import phoneaddiction.easyapps.ms.com.application.model.AppInfoBelowL;
import phoneaddiction.easyapps.ms.com.application.model.UsageStatWrapper;
import phoneaddiction.easyapps.ms.com.application.receivers.PhoneStateReceiver;
import phoneaddiction.easyapps.ms.com.application.util.LogUtils;
import phoneaddiction.easyapps.ms.com.application.util.SPUtil;
import phoneaddiction.easyapps.ms.com.application.util.Utils;

import static phoneaddiction.easyapps.ms.com.application.util.SPUtil.SPKeys.KEY_MOST_RECENT;
import static phoneaddiction.easyapps.ms.com.application.util.SPUtil.SPKeys.KEY_USAGE_DATA_BELOW_L;
import static phoneaddiction.easyapps.ms.com.application.util.Utils.getStartOfDay;
import static phoneaddiction.easyapps.ms.com.application.util.Utils.isBelowAndroidL;

public class ReadAppStateService extends Service {

    private String TAG = "ReadAppStateService";
    private UsageStatWrapper mostRecentStat;
    private long mostRecentTotalTimeInForeground;
    Map<String, AppInfoBelowL> usedAppDetails = new HashMap<>();
    ExecutorService executorService;
    HashMap<String, Long> timeInForegroundMap;
    PhoneStateReceiver phoneStateReceiver;
    private long processingTime;
    private boolean isRunThread = false;

    public ReadAppStateService() {
        // DO nothing
    }

    @Override
    public void onCreate() {
        super.onCreate();
        usedAppDetails = Utils.getUsageMapFromPreference(this);
        timeInForegroundMap = new HashMap<>();
        registerReceiver();
    }

    private void registerReceiver() {
        try {
            phoneStateReceiver = new PhoneStateReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_USER_PRESENT);
            intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
            intentFilter.addAction(Intent.ACTION_SCREEN_ON);
            this.registerReceiver(phoneStateReceiver, intentFilter);
        } catch (Exception e) {
            LogUtils.error(TAG, "error while registering receiver in service", e);
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        long delay = 0;
        if (intent != null) {
            delay = intent.getLongExtra(Constants.EXTRA_SERVICE_DELAY_TIME, 0);
        }
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                LogUtils.error(TAG, "thread sleep delay exception ", e);
            }
        }
        isRunThread = true;
        if (executorService == null) {
            executorService = Executors.newSingleThreadExecutor();
        }
        executorService.submit(runnable);
        return START_STICKY;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (isRunThread) {
                logCurrentState();
                processingTime = System.currentTimeMillis();
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException e) {
                    LogUtils.error(TAG, "Thread interrupted", e);
                }
            }
        }
    };

    private boolean logCurrentState() {
        if (isBelowAndroidL()) {
            //     logAllDetails();
        } else {
            return checkTopApp();
        }
        return false;
    }

    @SuppressLint("WrongConstant")
    private boolean checkTopApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            long endTime = System.currentTimeMillis();
            long startTime = getStartOfDay();
            long lastTimeUsed = -1;
            String lastPackageName = null;
            if (mostRecentStat == null) {
                mostRecentStat = new UsageStatWrapper();
            }
            if (mostRecentStat.getUsageStats() != null) {
                lastPackageName = mostRecentStat.getUsageStats().getPackageName();
            }
            final UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService("usagestats");
            long totalUsage = 0;
            final List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
            for (UsageStats usageStats : queryUsageStats) {
                if (usageStats == null || Utils.isNullOrEmpty(usageStats.getPackageName())) {
                    continue;
                }
                if (usageStats.getFirstTimeStamp() < startTime) {
                    continue;
                } else if ((usageStats.getTotalTimeInForeground() / DateUtils.SECOND_IN_MILLIS) >= 5) {
                    totalUsage = totalUsage + usageStats.getTotalTimeInForeground()/DateUtils.SECOND_IN_MILLIS;
                }
                if (lastTimeUsed < usageStats.getLastTimeUsed()) {
                    lastTimeUsed = usageStats.getLastTimeUsed();
                    mostRecentStat.setUsageStats(usageStats);
                }
            }
            if (mostRecentStat.getUsageStats() == null || mostRecentStat.getUsageStats().getPackageName() == null) {
                return true;
            }
            mostRecentStat.setTotalTime(0);// reset
            for (UsageStats usageStats : queryUsageStats) {
                if (usageStats == null || usageStats.getFirstTimeStamp() < startTime) {
                    continue;
                }
                if (mostRecentStat.getUsageStats().getPackageName().equalsIgnoreCase(usageStats.getPackageName())) {
                    mostRecentStat.setTotalTime(mostRecentStat.getTotalTime() + usageStats.getTotalTimeInForeground());
                }
            }
            long diff = mostRecentTotalTimeInForeground - mostRecentStat.getTotalTime();
            if (!isAppInForeground(usageStatsManager, diff)) {
                mostRecentStat = null;
                return true;
            }
            if (lastPackageName != null && lastPackageName.equals(mostRecentStat.getUsageStats().getPackageName())) {
                processingTime = processingTime > 0 ? System.currentTimeMillis() - processingTime : DateUtils.SECOND_IN_MILLIS;
                mostRecentTotalTimeInForeground = mostRecentTotalTimeInForeground + processingTime;
            } else {
                mostRecentTotalTimeInForeground = mostRecentStat.getTotalTime();
            }
            lastPackageName = mostRecentStat.getUsageStats().getPackageName();
            if (isPackageCrossedLimit(lastPackageName, mostRecentTotalTimeInForeground)) {
                launchInterruptPopup(lastPackageName);
                isRunThread = false;
                return false;
            }
            if (isPhoneLimitCrossed(totalUsage)) {
                launchInterruptPopup(lastPackageName);
                isRunThread = false;
                return false;
            }
            return true;
        }
        return false;
    }

    @SuppressLint("NewApi")
    private boolean isAppInForeground(UsageStatsManager usageStatsManager, long diff) {
        return mostRecentStat.getUsageStats().getPackageName().equalsIgnoreCase(Utils.crossCheckForeGroundApp(usageStatsManager, diff));
    }


    private void launchInterruptPopup(String packageName) {
        Intent intent = new Intent(this, InterruptDialogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        AppInfo appInfo = new AppInfo();
        Utils.setAppInfoForPackageName(this, packageName, appInfo);
        intent.putExtra(Constants.EXTRA_PACKAGE_NAME_FOR_DETAIL, packageName);
        intent.putExtra("Name", appInfo.appName);
        startActivity(intent);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean isPackageCrossedLimit(String packageName, long mostRecentTotalTimeInForeground) {
        Integer limit = Utils.getLimitForPackage(this, packageName);
        return packageName != null && limit != null && mostRecentTotalTimeInForeground > limit * DateUtils.SECOND_IN_MILLIS;
    }

   /* private void logAllDetails() {
        ActivityManager mActivityManager =(ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
        String packageName;
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH){
             packageName = mActivityManager.getRunningAppProcesses().get(0).processName;
        }
        else{
            packageName = mActivityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
        }
        AppInfoBelowL appInfoBelowL;
        if(Utils.isNullOrEmpty(packageName)){
            return;
        }
        appInfoBelowL = usedAppDetails.get(packageName);
        if(appInfoBelowL==null) {
           appInfoBelowL =  instantiateAppInfoForPackage(packageName);
        }
        long time = appInfoBelowL.getTimeInForeground();
        appInfoBelowL.setTimeInForeground(time);
        appInfoBelowL.set
    }*/

    private AppInfoBelowL instantiateAppInfoForPackage(String packageName) {
        AppInfoBelowL appInfoBelowL = new AppInfoBelowL();
        Utils.setAppInfoForPackageName(this, packageName, appInfoBelowL);
        return appInfoBelowL;
    }


    @Override
    public void onDestroy() {
        saveData();
        sendBroadcast(new Intent(Constants.RESTART_SERVICE_INTENT));
        try {
            unregisterReceiver(phoneStateReceiver);
        } catch (Exception e) {
            LogUtils.error(TAG, "error while unregistring receiver", e);
        }
        super.onDestroy();
    }

    private void saveData() {
        if (mostRecentStat != null) {
            Utils.saveObjectToPreference(this, mostRecentStat, KEY_MOST_RECENT);
        } else if (!usedAppDetails.isEmpty()) {
            Utils.saveObjectToPreference(this, usedAppDetails, KEY_USAGE_DATA_BELOW_L);
        }
    }


    public boolean isPhoneLimitCrossed(long totalUsage) {
        return isPackageCrossedLimit(Constants.PACKAGE_NAME_TOTAL,totalUsage);
    }


}
