package phoneaddiction.easyapps.ms.com.application.model;

import android.app.usage.UsageStats;

/**
 * Created by MMT5762 on 17-09-2017.
 */

public class UsageStatWrapper {
    UsageStats usageStats;
    long totalTime;

    public UsageStats getUsageStats() {
        return usageStats;
    }

    public void setUsageStats(UsageStats usageStats) {
        this.usageStats = usageStats;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }
}
