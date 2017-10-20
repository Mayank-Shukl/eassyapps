package phoneaddiction.easyapps.ms.com.application.model;

import java.util.Date;

/**
 * Created by MMT5762 on 18-10-2017.
 */

public class PhoneLockWrapper {

    private int unlockedTimes;
    private long lastUnlockedTime;
    private Date date;


    public int getUnlockedTimes() {
        return unlockedTimes;
    }

    public void setUnlockedTimes(int unlockedTimes) {
        this.unlockedTimes = unlockedTimes;
    }

    public long getLastUnlockedTime() {
        return lastUnlockedTime;
    }

    public void setLastUnlockedTime(long lastUnlockedTime) {
        this.lastUnlockedTime = lastUnlockedTime;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
