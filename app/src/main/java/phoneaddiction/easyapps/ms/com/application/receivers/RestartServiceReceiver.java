package phoneaddiction.easyapps.ms.com.application.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import phoneaddiction.easyapps.ms.com.application.Constants;
import phoneaddiction.easyapps.ms.com.application.service.ReadAppStateService;
import phoneaddiction.easyapps.ms.com.application.util.LogUtils;

public class RestartServiceReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || context == null) {
            return;
        }
        LogUtils.info("mayank","received boot complete");
        context.startService(new Intent(context, ReadAppStateService.class));
        if (Constants.RESTART_SERVICE_INTENT.equalsIgnoreCase(intent.getAction())) {

        }
    }
}
