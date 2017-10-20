package phoneaddiction.easyapps.ms.com.application.model;

/**
 * Created by MMT5762 on 02-07-2017.
 */

public class AppInfoBelowL extends AppInfo {


    public long getTimeInForeground() {
        return timeInForeground;
    }

    public void setTimeInForeground(long timeInForeground) {
        this.timeInForeground = timeInForeground;
    }

    private long timeInForeground;

}
