package phoneaddiction.easyapps.ms.com.application.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Date;

import phoneaddiction.easyapps.ms.com.application.model.PhoneLockWrapper;
import phoneaddiction.easyapps.ms.com.application.service.GsonUtils;
import phoneaddiction.easyapps.ms.com.application.util.SPUtil;
import phoneaddiction.easyapps.ms.com.application.util.Utils;

/**
 * Created by MMT5762 on 03-09-2017.
 */

public class PhoneStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }
        String action = intent.getAction();
        switch (action) {
            case Intent.ACTION_USER_PRESENT:
                handleUserUnlock(context);
                break;
            case Intent.ACTION_SCREEN_OFF:
                handleUserScreenOff(context);
                break;
            case Intent.ACTION_SCREEN_ON:
                handleUserScreenOn(context);
                break;
            default:
                break;
        }
    }

    private void handleUserScreenOn(Context context) {
    }

    private PhoneLockWrapper setPhoneLockWrapper(PhoneLockWrapper phoneLockWrapper) {
        if (phoneLockWrapper == null) {
            phoneLockWrapper = new PhoneLockWrapper();
        }
        Date savedDate = phoneLockWrapper.getDate();
        int unlockTimes = phoneLockWrapper.getUnlockedTimes();
        Date currentDate = new Date(Utils.getStartOfDay());
        if (savedDate != null && currentDate.compareTo(savedDate) > 0) {
            phoneLockWrapper.setUnlockedTimes(++unlockTimes);
        } else {
            phoneLockWrapper = new PhoneLockWrapper();
            phoneLockWrapper.setDate(currentDate);
        }
        phoneLockWrapper.setLastUnlockedTime(System.currentTimeMillis());
        return phoneLockWrapper;
    }

    private void handleUserScreenOff(Context context) {

    }

    private void updatePhoneActiveTime(Context context, long currentTime, long startOfDay, long lastEventTime) {

    }

    private long getFirstEventTime(Context context) {
        return Utils.getAppInstallTime(context);
    }

    private void handleUserUnlock(Context context) {
        String data = SPUtil.getString(context, SPUtil.SPKeys.PHONE_UNLOCK_DATA, null);
        PhoneLockWrapper phoneLockWrapper = GsonUtils.deserializeJSON(data, PhoneLockWrapper.class);
        phoneLockWrapper = setPhoneLockWrapper(phoneLockWrapper);
        SPUtil.putString(context, SPUtil.SPKeys.PHONE_UNLOCK_DATA, GsonUtils.serializeToJson(phoneLockWrapper, PhoneLockWrapper.class));
    }
}
