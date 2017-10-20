package phoneaddiction.easyapps.ms.com.application.ui;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import phoneaddiction.easyapps.ms.com.application.R;

/**
 * Created by MMT5762 on 04-10-2017.
 */

public class PermissionFragment extends BaseFragment {

    private View allowTag;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.permission_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    private void initViews(final View rootView) {
        allowTag = rootView.findViewById(R.id.allow);
        showExample();
        allowTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivityForResult(intent, 100);
                view.setTag(1);
            }
        });
        rootView.findViewById(R.id.how).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allowTag.setTag(1);
                showExample();
            }
        });
    }

    public void showExample() {
        View rootView = getView();
        if (allowTag.getTag() != null) {
            rootView.findViewById(R.id.basic_screen).setVisibility(View.GONE);
            rootView.findViewById(R.id.explanation).setVisibility(View.VISIBLE);
        } else {
            rootView.findViewById(R.id.explanation).setVisibility(View.GONE);
            rootView.findViewById(R.id.basic_screen).setVisibility(View.VISIBLE);
        }
    }

}
