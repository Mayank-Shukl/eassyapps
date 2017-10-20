package phoneaddiction.easyapps.ms.com.application;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.NativeExpressAdView;

import java.util.List;

import phoneaddiction.easyapps.ms.com.application.model.AppInfo;
import phoneaddiction.easyapps.ms.com.application.util.Utils;

/**
 * Adpater for list of items and ads
 */

public class AppInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;
    private final ActivityInteractionInterface mListener;
    private List<Object> appData;
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_AD = 1;

    public AppInfoAdapter(Context context, List<Object> appData, ActivityInteractionInterface listener) {
        this.appData = appData;
        mContext = context;
        mListener = listener;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        if (type == VIEW_TYPE_ITEM) {
            return new AppInfoViewHolder(LayoutInflater.from(mContext).inflate(R.layout.app_item, viewGroup, false));
        } else {
            return new AdViewHolder(LayoutInflater.from(mContext).inflate(R.layout.ad_list_item, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (holder instanceof AppInfoViewHolder) {
            bindItem((AppInfoViewHolder) holder, position);
        } else if (holder instanceof AdViewHolder) {
            bindAd((AdViewHolder) holder, position);
        }
    }

    private void bindAd(AdViewHolder holder, int position) {
        ((ViewGroup) holder.itemView).removeAllViews();
        NativeExpressAdView adView = (NativeExpressAdView) appData.get(position);
        if (adView.getParent() != null) {
            ((ViewGroup) adView.getParent()).removeView(adView);
        }
        ((ViewGroup) holder.itemView).addView(adView);
    }

    private void bindItem(AppInfoViewHolder viewHolder, final int pos) {
        final AppInfo appInfo = (AppInfo)appData.get(pos);
        viewHolder.tvAppName.setText(appInfo.getAppName());
        viewHolder.tvConsumedTime.setText(Utils.getStringFormattedTime(appInfo.getTimeConsumed()));
        viewHolder.ivIcon.setImageDrawable(appInfo.getIcon());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClicked(appInfo);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (Utils.isEmptyCollection(appData)) {
            return 0;
        }
        return appData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (appData.get(position) instanceof AppInfo) {
            return  VIEW_TYPE_ITEM;
        }
        return VIEW_TYPE_AD;
    }

    class AppInfoViewHolder extends ViewHolder {

        TextView tvAppName;
        TextView tvConsumedTime;
        ImageView ivIcon;

        AppInfoViewHolder(View itemView) {
            super(itemView);
            tvAppName = (TextView) itemView.findViewById(R.id.app_name);
            tvConsumedTime = (TextView) itemView.findViewById(R.id.app_time);
            ivIcon = (ImageView) itemView.findViewById(R.id.app_icon);
        }
    }

    class AdViewHolder extends ViewHolder {
        ViewGroup view;

        AdViewHolder(View itemView) {
            super(itemView);
            view = (ViewGroup) itemView.findViewById(R.id.ad_view_container);
        }
    }

    public interface ActivityInteractionInterface {
        void onItemClicked(AppInfo appInfo);
    }


}
