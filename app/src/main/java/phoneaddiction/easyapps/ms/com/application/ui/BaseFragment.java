package phoneaddiction.easyapps.ms.com.application.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by MMT5762 on 13-08-2017.
 */

public abstract class BaseFragment extends Fragment {

    protected InteractionListener mListener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof InteractionListener) {
            mListener = (InteractionListener) context;
        }
    }

    @Nullable
    @Override
    @CallSuper
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mListener.setActiveFragmentTag(getTag());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public interface InteractionListener {
        void addUpdateLimitMap(String key,Integer value);
        void removeLimitMap(String key);
        Integer getDailyLimitFor(String packageName);
        boolean isShownAdFullpage();
        void setAdShown(boolean isShown);
        void setActiveFragmentTag(String tag);
    }

}
