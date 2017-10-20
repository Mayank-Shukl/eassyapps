package phoneaddiction.easyapps.ms.com.application.model;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MMT5762 on 15-06-2017.
 */

public class AppInfo implements Parcelable {

    public String packageName;
    public Drawable icon;
    public String appCategory;

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String appType;

    public long getTimeConsumed() {
        return timeConsumed;
    }

    public void setTimeConsumed(long timeConsumed) {
        this.timeConsumed = timeConsumed;
    }

    public String getTimeUsed() {
        return timeUsed;
    }

    public void setTimeUsed(String timeUsed) {
        this.timeUsed = timeUsed;
    }

    private String timeUsed;

    public long timeConsumed=0;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String appName;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.packageName);
        dest.writeString(this.appCategory);
        dest.writeString(this.appType);
        dest.writeString(this.timeUsed);
        dest.writeLong(this.timeConsumed);
        dest.writeString(this.appName);
    }

    public AppInfo() {
    }

    protected AppInfo(Parcel in) {
        this.packageName = in.readString();
        this.icon = in.readParcelable(Drawable.class.getClassLoader());
        this.appCategory = in.readString();
        this.appType = in.readString();
        this.timeUsed = in.readString();
        this.timeConsumed = in.readLong();
        this.appName = in.readString();
    }

    public static final Parcelable.Creator<AppInfo> CREATOR = new Parcelable.Creator<AppInfo>() {
        @Override
        public AppInfo createFromParcel(Parcel source) {
            return new AppInfo(source);
        }

        @Override
        public AppInfo[] newArray(int size) {
            return new AppInfo[size];
        }
    };
}
