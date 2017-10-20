package phoneaddiction.easyapps.ms.com.application.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import phoneaddiction.easyapps.ms.com.application.BuildConfig;
import phoneaddiction.easyapps.ms.com.application.R;
import phoneaddiction.easyapps.ms.com.application.model.AppInfo;
import phoneaddiction.easyapps.ms.com.application.util.LogUtils;
import phoneaddiction.easyapps.ms.com.application.util.Utils;

/**
 * for each app detail of use
 * should have type of active usgae timeframe
 */

public class AppDetailFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    public static final String EXTRA_DETAIL = "EXTRA_DETAIL";
    private AppInfo appInfo;
    private  NumberPicker pickerHrs,pickerMins;
    private TextView timeUnitHrs,timeUnitMins;
    View switchView,tvAppHeaderContainer;
    TextView tvAppHeader,tvTimeSelected;
    Integer dailyLimitInSeconds;
    Switch switchButton;
    private String LOG_TAG="AppDetailPage";
    private TextView tvConfirm,tvCancelReset;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            if(getArguments()==null || getArguments().getParcelable(EXTRA_DETAIL)==null){
                getActivity().getSupportFragmentManager().popBackStack();
                return;
            }
        } catch (Exception e) {
            LogUtils.error(LOG_TAG,"wrong arguments",e);
        }
        appInfo = getArguments().getParcelable(EXTRA_DETAIL);
        Utils.setAppInfoForPackageName(getActivity(),appInfo.packageName,appInfo);
        dailyLimitInSeconds = mListener.getDailyLimitFor(appInfo.packageName);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.app_detail_layout,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
     //   loadAdd(view);
        loadFullPageAd();

    }

    private InterstitialAd mInterstitialAd;

    private void loadFullPageAd(){
        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId(getString(R.string.full_page));
        if(BuildConfig.DEBUG) {
            mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("917A92FBAD64DA672AA0BF16B5ECAE3B").build());
        }else{
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        }
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                if(isAdded() && getActivity()!=null && !getActivity().isFinishing())
                    if(BuildConfig.DEBUG) {
                        mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("917A92FBAD64DA672AA0BF16B5ECAE3B").build());
                    }else{
                        mInterstitialAd.loadAd(new AdRequest.Builder().build());
                    }
            }
        });
    }

    AdView mAdView;

    private void loadAdd(View view){
        mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest ;
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

    @Override
    public void onPause() {
        super.onPause();
        if(mAdView!=null){
            mAdView.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mAdView!=null){
            mAdView.resume();
        }
    }



    private void initViews(View view) {
        TextView tvAppName = (TextView) view.findViewById(R.id.app_name);
        ImageView tvAppIcon = (ImageView) view.findViewById(R.id.app_icon);
        tvAppHeader = (TextView) view.findViewById(R.id.header);
        tvTimeSelected = (TextView)view.findViewById(R.id.time_selected);
        switchButton = (Switch)view.findViewById(R.id.switch_limit);
        pickerMins = (NumberPicker)view.findViewById(R.id.time_picker_min);
        timeUnitHrs = (TextView) view.findViewById(R.id.time_unit_min);
        pickerHrs = (NumberPicker)view.findViewById(R.id.time_picker_hrs);
        timeUnitMins = (TextView) view.findViewById(R.id.time_unit_hrs);
        switchView = view.findViewById(R.id.picker_cotainer);
        tvConfirm = (TextView)view.findViewById(R.id.confirm);
        tvCancelReset = (TextView)view.findViewById(R.id.cancel);
        tvAppHeaderContainer = view.findViewById(R.id.header_container);
        pickerHrs.setMinValue(0);
        pickerHrs.setMaxValue(10);
        pickerMins.setMinValue(0);
        pickerMins.setMaxValue(59);
        tvAppName.setText(appInfo.appName);
        setUpInitLimit();
        tvAppIcon.setImageDrawable(appInfo.icon);
        switchButton.setOnCheckedChangeListener(this);
        if(dailyLimitInSeconds!=null && dailyLimitInSeconds>0){
            setUpChangeLimit();
        }
        tvConfirm.setOnClickListener(this);
        tvCancelReset.setOnClickListener(this);
    }

    private void setUpChangeLimit() {
        tvAppHeader.setText(Html.fromHtml(getString(R.string.DETAIL_HEADER_EDIT,appInfo.appName)));
        tvAppHeaderContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchView.setVisibility(View.VISIBLE);

            }
        });
        switchView.setVisibility(View.VISIBLE);
        int hrs = dailyLimitInSeconds/(60*60);
        int secondsLeft = dailyLimitInSeconds-hrs*60*60;
        int mins = secondsLeft/60;
        pickerHrs.setValue(hrs);
        pickerMins.setValue(mins);
        switchButton.setVisibility(View.GONE);
        tvTimeSelected.setVisibility(View.VISIBLE);
        tvCancelReset.setText(getString(R.string.RESET));
        tvConfirm.setText(getString(R.string.UPDATE));
        if(hrs>0) {
            tvTimeSelected.setText(Html.fromHtml(getString(R.string.time_selected, String.valueOf(hrs), String.valueOf(mins))));
        }else{
            tvTimeSelected.setText(Html.fromHtml(getString(R.string.time_selected_min, String.valueOf(mins))));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mAdView!=null){
            mAdView.destroy();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static AppDetailFragment newInstance(AppInfo appInfo) {
        Bundle args = new Bundle();
        AppDetailFragment fragment = new AppDetailFragment();
        args.putParcelable(EXTRA_DETAIL,appInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            switchView.setVisibility(View.VISIBLE);
        }else{
            switchView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel:
                switchView.setVisibility(View.GONE);
                pickerHrs.setValue(0);
                pickerMins.setValue(0);
                saveLimit();
                updateSwitch();
                break;
            case R.id.confirm:
                saveLimit();
                updateSwitch();
                showFullPageAd();
                break;
        }
    }

    private void showFullPageAd() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!mListener.isShownAdFullpage() && mInterstitialAd!=null && mInterstitialAd.isLoaded()){
                    mInterstitialAd.show();
                    mListener.setAdShown(true);
                }
            }
        },1500);

    }


    private void updateSwitch() {
        if(dailyLimitInSeconds==null || dailyLimitInSeconds<=0){
            setUpInitLimit();
        }else{
           setUpChangeLimit();
        }
        switchView.setVisibility(View.GONE);
    }

    private void setUpInitLimit() {
        pickerMins.setValue(0);
        pickerHrs.setValue(0);
        tvAppHeader.setText(Html.fromHtml(getString(R.string.DETAIL_HEADER,appInfo.appName)));
        tvCancelReset.setText(getString(R.string.CANCEL));
        tvConfirm.setText(getString(R.string.CONFIRM));
        switchButton.setVisibility(View.VISIBLE);
        tvTimeSelected.setVisibility(View.GONE);
        switchButton.setChecked(false);
    }

    private void saveLimit() {
        int hrs = pickerHrs.getValue();
        int mins = pickerMins.getValue();
        int limitInSeconds = hrs*60*60+mins*60;
        dailyLimitInSeconds = limitInSeconds;
        if(dailyLimitInSeconds<=0){
            mListener.removeLimitMap(appInfo.packageName);
        }else {
            mListener.addUpdateLimitMap(appInfo.packageName, limitInSeconds);
        }
    }

}
