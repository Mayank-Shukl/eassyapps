package phoneaddiction.easyapps.ms.com.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import phoneaddiction.easyapps.ms.com.application.service.ReadAppStateService;
import phoneaddiction.easyapps.ms.com.application.ui.MainActivity;
import phoneaddiction.easyapps.ms.com.application.util.Utils;

import static phoneaddiction.easyapps.ms.com.application.Constants.EXTRA_PACKAGE_NAME_FOR_DETAIL;
import static phoneaddiction.easyapps.ms.com.application.Constants.EXTRA_SERVICE_DELAY_TIME;

public class InterruptDialogActivity extends AppCompatActivity implements View.OnClickListener {

    private String packageName;
    private Intent mServiceIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_interrupt_dialog);
        String placeHolder = "this app";
        if(getIntent()==null|| getIntent().getStringExtra(EXTRA_PACKAGE_NAME_FOR_DETAIL)==null){
            finish();
            return;
        }
        packageName = getIntent().getStringExtra(EXTRA_PACKAGE_NAME_FOR_DETAIL);
        if(getIntent()!=null && getIntent().getStringExtra("Name")!=null){
            placeHolder = getIntent().getStringExtra("Name");

        }
        ((TextView)findViewById(R.id.content)).setText(Html.fromHtml(getString(R.string.interrupt_text,placeHolder)));
        findViewById(R.id.extend).setOnClickListener(this);
        findViewById(R.id.remove_limit).setOnClickListener(this);
        findViewById(R.id.okay).setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mServiceIntent == null) {
            mServiceIntent = new Intent(this, ReadAppStateService.class);
        }
        if (packageName != null) {
            startService(mServiceIntent);// start service again
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.extend:
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(EXTRA_PACKAGE_NAME_FOR_DETAIL,packageName);
                startActivity(intent);
                break;
            case R.id.remove_limit:
                Utils.removeLimit(this,packageName);
                finish();
                break;
            case R.id.okay:
                prepareIntent(7);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        prepareIntent(5);

    }

    private void prepareIntent(int seconds) {
        mServiceIntent = new Intent(this,ReadAppStateService.class);
        mServiceIntent.putExtra(EXTRA_SERVICE_DELAY_TIME,seconds* DateUtils.SECOND_IN_MILLIS);
    }
}
