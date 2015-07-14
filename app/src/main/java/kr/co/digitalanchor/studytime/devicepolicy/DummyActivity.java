package kr.co.digitalanchor.studytime.devicepolicy;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import kr.co.digitalanchor.studytime.R;

public class DummyActivity extends Activity {

    private final int ACTIVATION_REQUEST = 50002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dummy);

        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                new ComponentName(this, AdminReceiver.class));

        startActivityForResult(intent, ACTIVATION_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == ACTIVATION_REQUEST && resultCode == RESULT_OK) {

            // TODO

        } else {

            // TODO block
        }

        finish();
    }
}
