package kr.co.digitalanchor.studytime.location;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.SimpleXmlRequest;
import com.android.volley.toolbox.Volley;
import com.orhanobut.logger.Logger;

import kr.co.digitalanchor.studytime.STApplication;

public class LocationService extends Service implements LocationListener {

    RequestQueue mQueue;

    boolean isGPSEnabled = false;

    boolean isNetworkEnabled = false;

    boolean isGetLocation = false;

    Location location;

    double lat; // 위도
    double lon; // 경도

    // 최소 GPS 정보 업데이트 거리 10 미터
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATE = 10;

    // 최소 GPS 정보 업데이트 시간 밀리세컨이므로 1분
    private static final long MIN_TIME_BW_UPDATE = 1000 * 60 * 1;

    protected LocationManager locationManager;

    public LocationService() {

        Logger.d("LocationService");

        mQueue = Volley.newRequestQueue(STApplication.applicationContext);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {

            handleActionWork();

        }

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private void handleActionWork() {

            getLocation();

        while (isGPSEnabled) {

            if (location == null) {

                continue;
            }

            lat = location.getLatitude();
            lon = location.getLongitude();

            if (lat == 0.0 && lon == 0.0) {

                continue;

            }

            break;
        }

        Logger.d("com Latitude : " + lat + ", Longitude : " + lon);

        this.stopSelf();
    }

    public Location getLocation() {

        try {

            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // GPS 정보 가져오기
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // 현재 네트워크 상태 값 알아오기
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {

                // GPS 와 네트워크 사용이 가능하지 않을 때 소스 구현
            } else {

                this.isGetLocation = true;

                // 네트워크 정보로 부터 위치값 가져오기
                if (isNetworkEnabled) {

                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATE,
                            MIN_TIME_BW_UPDATE, this);

                    if (locationManager != null) {

                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if (location != null) {

                            // 위도 경도 저장
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                        }
                    }
                }

                if (isGPSEnabled) {

                    if (location == null) {

                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATE,
                                MIN_DISTANCE_CHANGE_FOR_UPDATE, this);

                        if (locationManager != null) {

                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            if (location != null) {

                                lat = location.getLatitude();
                                lon = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {

            Logger.e(e.toString());

            System.out.println(e.toString());
        }

        return location;
    }

    /**
     * 위도 값을 가져옵니다.
     *
     * @return
     */
    public double getLatitude() {

        if (location != null) {

            lat = location.getLatitude();
        }

        return lat;
    }

    /**
     * 경도 값을 가져옵니다.
     *
     * @return
     */
    public double getLongitude() {

        if (location != null) {

            lon = location.getLongitude();
        }

        return lon;
    }

    public boolean isGetLocation() {

        return this.isGetLocation;
    }



    protected void addRequest(SimpleXmlRequest request) {

        try {

            mQueue.add(request);

        } catch (Exception e) {

            Logger.e(e.toString());
        }
    }

    protected void handleResultCode(int code, String msg) {

        switch (code) {

            default:

                if (TextUtils.isEmpty(msg)) {

                    Logger.e(msg);
                }

                break;
        }
    }

    protected void handeleError(VolleyError error) {

        Logger.e(error.toString());
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
