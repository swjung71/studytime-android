package kr.co.digitalanchor.studytime.block;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.orhanobut.logger.Logger;

import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.devicepolicy.DummyActivity;

/**
 * Created by Thomas on 2015-07-13.
 */
public class BlockPasswordLayout extends FrameLayout implements View.OnClickListener {

    private Context context;

    private Button buttonAdmin;
    private Button buttonPassword;

    public BlockPasswordLayout(Context context) {
        super(context);

        this.context = context;

        inflate(context, R.layout.layout_block_admin, this);

        buttonAdmin = (Button) findViewById(R.id.buttonAdmin);

        buttonAdmin.setOnClickListener(this);

        buttonPassword = (Button) findViewById(R.id.buttonInputPassword);

        buttonPassword.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.buttonAdmin:

                showDeviceAdminSettings();

                break;

            case R.id.buttonInputPassword:

                showPasswordLayout();

                break;

            default:

                Logger.e("clicked default");

                break;
        }
    }

    private void showDeviceAdminSettings() {

        // TODO hide top view

        Intent intent = new Intent(context, DummyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);

    }

    private void showPasswordLayout() {

        Intent intent = new Intent(context, BlockPasswordActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }
}
