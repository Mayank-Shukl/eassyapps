package phoneaddiction.easyapps.ms.com.application.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import phoneaddiction.easyapps.ms.com.application.R;
import phoneaddiction.easyapps.ms.com.application.util.SPUtil;
import phoneaddiction.easyapps.ms.com.application.util.Utils;

/**
 * Created by MMT5762 on 17-09-2017.
 */

public class PhoneDetailFragment extends BaseFragment {

    TextView tvPhoneUsageValue, tvPhoneUnlockValue;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.phone_detail,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    private void initViews(View view) {
        tvPhoneUsageValue = view.findViewById(R.id.phone_usgae_value);
        tvPhoneUnlockValue = view.findViewById(R.id.lock_unlock_value);
        long seconds= SPUtil.getLong(getActivity(), SPUtil.SPKeys.TOTAL_ACTIVE_TIME_TODAY,0)/ DateUtils.SECOND_IN_MILLIS;
        tvPhoneUsageValue.setText( Utils.getStringFormattedTime(seconds));
        tvPhoneUnlockValue.setText(String.valueOf(SPUtil.getInt(getActivity(),SPUtil.SPKeys.PHONE_UNLOCK_DATA,1)));
    }



}
