package phoneaddiction.easyapps.ms.com.application.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phoneaddiction.easyapps.ms.com.application.AppInfoAdapter;
import phoneaddiction.easyapps.ms.com.application.BuildConfig;
import phoneaddiction.easyapps.ms.com.application.Constants;
import phoneaddiction.easyapps.ms.com.application.R;
import phoneaddiction.easyapps.ms.com.application.model.AppInfo;
import phoneaddiction.easyapps.ms.com.application.service.ReadAppStateService;
import phoneaddiction.easyapps.ms.com.application.util.SPUtil;
import phoneaddiction.easyapps.ms.com.application.util.TrackerUtils;
import phoneaddiction.easyapps.ms.com.application.util.Utils;

import static phoneaddiction.easyapps.ms.com.application.Constants.EXTRA_PACKAGE_NAME_FOR_DETAIL;
import static phoneaddiction.easyapps.ms.com.application.Constants.PACKAGE_NAME_TOTAL;
import static phoneaddiction.easyapps.ms.com.application.util.Utils.isFragmentActive;
import static phoneaddiction.easyapps.ms.com.application.util.Utils.isOtherVisibleApp;
import static phoneaddiction.easyapps.ms.com.application.util.Utils.isUsedApp;

public class MainActivity extends AppCompatActivity implements AppInfoAdapter.ActivityInteractionInterface, BaseFragment.InteractionListener {

    private static final String DETAIL_FRAGMENT = "detail";
    private static final String PHONE_DETAIL_FRAGMENT = "phone_detail";
    public static final String PERMISSION_FRAGMENT = "permission_fragment";
    private RecyclerView recyclerView;
    AppInfoAdapter appInfoAdapter;
    RelativeLayout mProgressBar;
    private String currentTag;
    Map<String, AppInfo> resultedPackages = new HashMap<>();
    Map<String, Integer> dailyLimitMap;
    private String appList;
    private BottomNavigationView navigation;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(this, getString(R.string.publisher_id));
        setContentView(R.layout.activity_main);
        initViews();
        loadAdd();
        if (isUsagePermissionGranted()) {
            onUsagePermissionGranted();
        } else if (!Utils.isBelowAndroidL()) {
            addPermissionFragment();
        }
    }

    private void addPermissionFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(PERMISSION_FRAGMENT);
        if (fragment == null || !isFragmentActive(fragment)) {
            fragment = getFragmentForTag(PERMISSION_FRAGMENT,null);
            currentTag = PERMISSION_FRAGMENT;
            getSupportFragmentManager().beginTransaction().replace(R.id.full_page_container, fragment, PERMISSION_FRAGMENT).commitAllowingStateLoss();
        }
    }

    @Override
    public void onBackPressed() {
        if(Utils.isFragmentActive(getSupportFragmentManager().findFragmentByTag(PERMISSION_FRAGMENT))){
            finish();
            return;
        }
        super.onBackPressed();
    }

    AdView mAdView;
    private void loadAdd(){
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest;
        if(BuildConfig.DEBUG) {
           adRequest = new AdRequest.Builder()
                    .addTestDevice("917A92FBAD64DA672AA0BF16B5ECAE3B")//TODO:remove
                    .build();
        }else{
            adRequest = new AdRequest.Builder()
                    .build();
        }
        mAdView.loadAd(adRequest);
    }

    private void initViews() {
        TextView textView = (TextView) findViewById(R.id.heading);
        mProgressBar = (RelativeLayout) findViewById(R.id.progress);
        textView.setText(getString(R.string.title_usage));
        spinner = (Spinner) findViewById(R.id.options);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.time_array, R.layout.spinner_default);
        adapter.setDropDownViewResource(R.layout.spinner_list_item);
        spinner.setAdapter(adapter);
        int pos = SPUtil.getInt(MainActivity.this, SPUtil.SPKeys.LAST_SELECTION, 0);
        spinner.setDropDownVerticalOffset(getResources().getDimensionPixelSize(R.dimen.spinner_vertical_offset));
        spinner.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
        spinner.setSelected(false);
        spinner.setSelection(pos, false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(isUserInteracted) {
                    SPUtil.putInt(MainActivity.this, SPUtil.SPKeys.LAST_SELECTION, position);
                    showOrRefreshData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    boolean isUserInteracted;

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        isUserInteracted = true;
    }

    private NativeExpressAdView getAd() {
        NativeExpressAdView adView = new NativeExpressAdView(this);
        adView.setAdUnitId("ca-app-pub-5912217456810893/2629852524");
        adView.setAdSize(new AdSize(360, 100));
        if(BuildConfig.DEBUG) {
            adView.loadAd(new AdRequest.Builder().addTestDevice("917A92FBAD64DA672AA0BF16B5ECAE3B").build());
        }else{
            adView.loadAd(new AdRequest.Builder().build());
        }
        return adView;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mAdView!=null){
            mAdView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mAdView!=null){
            mAdView.resume();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        showOrRefreshData();
    }

    private void showOrRefreshData() {
        resultedPackages.clear();
        if (isUsagePermissionGranted()) {
            onUsagePermissionGranted();
        } else {
            addPermissionFragment();
        }
    }

    private void showHideProgress(int visibility) {
        mProgressBar.setVisibility(visibility);
    }

    @Override
    public void addUpdateLimitMap(String key, Integer value) {
        if (value == null || value <= 0) {
            return;
        }
        if (Utils.isEmptyMap(dailyLimitMap)) {
            dailyLimitMap = new HashMap<>();
        }
        dailyLimitMap.put(key, value);
        Utils.setLimitMap(this, SPUtil.SPKeys.DAILY_LIMIT_MAP, dailyLimitMap);
    }

    @Override
    public void removeLimitMap(String key) {
        if (Utils.isEmptyMap(dailyLimitMap) || !dailyLimitMap.containsKey(key)) {
            return;
        }
        dailyLimitMap.remove(key);
        Utils.setLimitMap(this, SPUtil.SPKeys.DAILY_LIMIT_MAP, dailyLimitMap);
    }

    @Override
    public Integer getDailyLimitFor(String packageName) {
        if (dailyLimitMap != null) {
            return dailyLimitMap.get(packageName);
        }
        return 0;
    }

    boolean isSHownFullPageAd;

    @Override
    public boolean isShownAdFullpage() {
        return isSHownFullPageAd;
    }

    @Override
    public void setAdShown(boolean isShown) {
        isSHownFullPageAd = isSHownFullPageAd?true:isShown;
    }

    @Override
    public void setActiveFragmentTag(String tag) {
        currentTag = tag;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mAdView!=null){
            mAdView.destroy();
        }
    }

    public ArrayList<Object> getListWithAds(List<AppInfo> appInfoList) {
        ArrayList<Object> finalList = new ArrayList<>();
        finalList.addAll(appInfoList);
        /*for(int i=0,j=0;i<appInfoList.size();i++,j++){
            Object content;
            if(false){
                content = getAd();
                i--;
            }else{
                 content = appInfoList.get(i);
            }
            finalList.add(content);
        }*/
        return finalList;
    }

    /**
     *
     * @param tag - tag
     * @param appInfo - appinfo
     * @return - frgament for the tag
     */
    public Fragment getFragmentForTag(String tag,AppInfo appInfo) {
        switch (tag){
            case DETAIL_FRAGMENT:
                return AppDetailFragment.newInstance(appInfo);
            case PHONE_DETAIL_FRAGMENT:
                return new PhoneDetailFragment();
            case PERMISSION_FRAGMENT:
                return new PermissionFragment();
        }
        return null;
    }

    static class UsageTask extends AsyncTask {

        private WeakReference<MainActivity> reference;

        UsageTask(MainActivity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            if (reference.get() != null) {
                reference.get().showHideProgress(View.VISIBLE);
            }
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            if (reference.get() != null) {
                reference.get().initData();
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Object o) {
            if (reference.get() != null) {
                reference.get().onDataFetched();
            }
        }
    }

    private void onUsagePermissionGranted() {
        removeFragmentByTag(PERMISSION_FRAGMENT);
     //   navigation.setVisibility(View.VISIBLE);
        AsyncTask usageTask = new UsageTask(this);
        usageTask.execute(null, null, null);
    }

    private void onDataFetched() {
        mProgressBar.setVisibility(View.GONE);
        ArrayList<AppInfo> appInfoArrayList = sortData();
        ArrayList<Object> finalList = getListWithAds(appInfoArrayList);
        if(Utils.isEmptyCollection(finalList) && spinner.getSelectedItemPosition()<getResources().getStringArray(R.array.time_array).length-1){
            spinner.setSelection(spinner.getSelectedItemPosition()+1,false);
            return;
        }
        if (recyclerView != null && appInfoAdapter != null) {
            appInfoAdapter.notifyDataSetChanged();
        } else {
            recyclerView = (RecyclerView) findViewById(R.id.list);
            AppInfoAdapter appInfoAdapter = new AppInfoAdapter(this, finalList, this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(appInfoAdapter);
        }
        if (getIntent() != null && getIntent().getStringExtra(EXTRA_PACKAGE_NAME_FOR_DETAIL) != null) {
            AppInfo appInfo = new AppInfo();
            appInfo.setPackageName(getIntent().getStringExtra(EXTRA_PACKAGE_NAME_FOR_DETAIL));
            Utils.setAppInfoForPackageName(this, getIntent().getStringExtra(EXTRA_PACKAGE_NAME_FOR_DETAIL), appInfo);
            onItemClicked(appInfo);
            setIntent(null);
        }
        startService(new Intent(this, ReadAppStateService.class));
        TrackerUtils.trackAppList(appList);
    }

    private ArrayList<AppInfo> sortData() {
        ArrayList<AppInfo> appInfos = new ArrayList<>(resultedPackages.values());
        if (!Utils.isEmptyCollection(appInfos)) {
            Utils.sortAppInfoTimeConsumed(appInfos);
        }
        return appInfos;
    }

    /*

         */
    private void initData() {
        dailyLimitMap = Utils.getLimitMap(this, SPUtil.SPKeys.DAILY_LIMIT_MAP);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            resultedPackages.clear();
            long endTime = System.currentTimeMillis();
            int interval = SPUtil.getInt(this, SPUtil.SPKeys.LAST_SELECTION, 0);
            long startTime = Utils.getStartTimeFor(interval);
            int intervalType = Utils.getIntervalType(interval);
            @SuppressLint("WrongConstant") final UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService("usagestats");
            final List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(intervalType, startTime, endTime);
            long totalUsage=0;
            for (UsageStats usageStats : queryUsageStats) {
                if (isUsedApp(usageStats) && isOtherVisibleApp(this, usageStats.getPackageName())) {
                    AppInfo appInfo = resultedPackages.get(usageStats.getPackageName());
                    if (appInfo == null) {
                        appInfo = new AppInfo();
                        Utils.setAppInfoForPackageName(this, usageStats.getPackageName(), appInfo);
                    }
                    if (interval == 0 && usageStats.getFirstTimeStamp() < startTime) {
                        continue;
                    }
                    resultedPackages.put(appInfo.packageName, appInfo);
                    appInfo.setTimeConsumed(appInfo.getTimeConsumed() + (usageStats.getTotalTimeInForeground() / DateUtils.SECOND_IN_MILLIS));
                    if (appInfo.getTimeConsumed() < 5) {
                        resultedPackages.remove(appInfo.packageName);
                    }else{
                        totalUsage = totalUsage + (usageStats.getTotalTimeInForeground() / DateUtils.SECOND_IN_MILLIS);
                    }
                    appList = appList + " , " + appInfo.packageName;
                }
            }
            AppInfo appInfo = new AppInfo();
            appInfo.setPackageName(Constants.PACKAGE_NAME_TOTAL);
            appInfo.setTimeConsumed(totalUsage);
            appInfo.appName =  Constants.OverAllUsage;
            appInfo.icon = getDrawable(R.mipmap.ic_launcher_foreground);
            resultedPackages.put(Constants.PACKAGE_NAME_TOTAL,appInfo);
        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    getSupportFragmentManager().popBackStack();
                    return true;
                case R.id.navigation_dashboard:
                    switchFragment(PHONE_DETAIL_FRAGMENT,null);
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };

    private void removeFragmentByTag(String tag) {
        BaseFragment fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(tag);
        if (Utils.isFragmentActive(fragment)) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean isUsagePermissionGranted() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    @Override
    public void onItemClicked(AppInfo appInfo) {
        switchFragment(DETAIL_FRAGMENT, appInfo);
    }


    private void switchFragment(String tag, AppInfo appInfo) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment == null || !isFragmentActive(fragment)) {
            fragment = getFragmentForTag(tag,appInfo);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_container, fragment, tag).addToBackStack(null).commitAllowingStateLoss();
        }
    }



}
