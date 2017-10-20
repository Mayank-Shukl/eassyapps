package phoneaddiction.easyapps.ms.com.application;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;

import io.fabric.sdk.android.Fabric;

/**
 * Created by MMT5762 on 09-09-2017.
 */

public class UsageApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Fabric.with(this, new Answers());
    }
}
