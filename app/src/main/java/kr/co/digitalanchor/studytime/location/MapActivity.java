package kr.co.digitalanchor.studytime.location;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import kr.co.digitalanchor.studytime.R;

public class MapActivity extends FragmentActivity {

    private GoogleMap mMap;

    double latitude;
    double longitude;

    TextView textAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

        Bundle bundle = getIntent().getExtras();

        latitude = bundle.getDouble("latitude", 0.0);
        longitude = bundle.getDouble("longitude", 0.0);

        textAddress = (TextView) findViewById(R.id.address);

    }

    @Override
    protected void onResume() {
        super.onResume();

        setUpMapIfNeeded();

    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {

                setUpMap();
            }
        }
    }

    private void updateText() {

        String sLocationInfo = "";

        try {

            Geocoder coder = new Geocoder(this, Locale.KOREA);

            List<Address> addresses = coder.getFromLocation(latitude, longitude, 1);

            if (addresses != null) {

                Address addr = addresses.get(0);

                for (int i = 0; i <= addr.getMaxAddressLineIndex(); i++) {

                    String addLine = addr.getAddressLine(i);

                    sLocationInfo += String.format("%s", addLine);
                }
            }

        } catch (IOException e) {

            Logger.e(e.toString());

            sLocationInfo = null;
        }

        if (TextUtils.isEmpty(sLocationInfo)) {

            textAddress.setText("주소 정보 없음");

        } else {

            textAddress.setText(sLocationInfo);
        }
    }

    private void setUpMap() {


        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Marker")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.img_gps2)));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 17));

        updateText();

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}
