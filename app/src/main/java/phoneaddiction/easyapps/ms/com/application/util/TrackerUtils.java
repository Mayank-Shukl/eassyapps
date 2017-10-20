package phoneaddiction.easyapps.ms.com.application.util;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

/**
 * Created by MMT5762 on 09-09-2017.
 */

public class TrackerUtils {

    public static void trackAppList(String appList) {
        Answers.getInstance().logCustom(new CustomEvent("Top Apps")
                .putCustomAttribute("listOfApps", appList));
    }

}
