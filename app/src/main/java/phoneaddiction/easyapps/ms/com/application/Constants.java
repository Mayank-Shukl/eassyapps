package phoneaddiction.easyapps.ms.com.application;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;

import phoneaddiction.easyapps.ms.com.application.util.Utils;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by MMT5762 on 02-07-2017.
 */

public class Constants {

    public static final String RESTART_SERVICE_INTENT = "easyapss.ms.receiver.RESTART_SERVICE";


    public static final int TIME_TODAY = 0;
    public static final int TIME_LAST_24 = 1;
    public static final int TIME_WEEK = 2;
    public static final int TIME_MONTH = 3;
    public static final String EXTRA_PACKAGE_NAME_FOR_DETAIL = "extra_detail_package";
    public static final String EXTRA_SERVICE_DELAY_TIME = "extra_delay_time";
    public static final String PACKAGE_NAME_TOTAL = "total_use";
    public static final String OverAllUsage = "Total Phone Usage";

}
