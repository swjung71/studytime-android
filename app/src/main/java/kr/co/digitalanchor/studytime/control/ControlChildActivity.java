package kr.co.digitalanchor.studytime.control;

import android.app.Activity;
import android.os.Bundle;

import kr.co.digitalanchor.studytime.R;

/**
 * Created by Thomas on 2015-06-12.
 */
public class ControlChildActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_control_child);

        /* test        setContentView(R.layout.activity_control_child);

        ImageButton imageButton = (ImageButton) findViewById(R.id.buttonMenu);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MenuPopup menuPopup = new MenuPopup(getApplicationContext(), "나오미");

                menuPopup.setOnClickMenuItemListener(new MenuPopup.OnClickMenuItemListener() {
                    @Override
                    public void onClickPoint() {

                        Toast.makeText(getApplicationContext(), "onClickPoint", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onClickFAQ() {
                        Toast.makeText(getApplicationContext(), "onClickFAQ", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onClickInquiry() {
                        Toast.makeText(getApplicationContext(), "onClickInquiry", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onClickWithdraw() {
                        Toast.makeText(getApplicationContext(), "onClickWithdraw", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onClickLogOut() {

                        Toast.makeText(getApplicationContext(), "onClickLogOut", Toast.LENGTH_SHORT).show();
                    }
                });

                menuPopup.show(v);

            }
        });

        */
    }
}
