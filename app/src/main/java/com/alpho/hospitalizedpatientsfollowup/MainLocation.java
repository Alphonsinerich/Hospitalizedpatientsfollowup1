package com.alpho.hospitalizedpatientsfollowup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainLocation extends Activity implements SensorEventListener {


    private final String TAG = MainLocation.class.getSimpleName();
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private long lastTime = 0;
    private float lastX, lastY, lastZ;
    private static final int THRESHOLD = 600; //used to see whether a shake gesture has been detected or not.
    TextView coordinates;
    TextView address;
    ConnectionReceiver receiver;
    IntentFilter intentFilter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensoractivity);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        Button update = (Button) findViewById(R.id.update_button);


        update.setOnClickListener(new UpdateLocationClick());
        coordinates = (TextView) findViewById(R.id.location_points);
        address = (TextView) findViewById(R.id.location_address);
        if (SApplication.LOCATION != null) {
            double lat = SApplication.LOCATION.getLatitude();
            double lon = SApplication.LOCATION.getLongitude();
            coordinates.setText(lat + " " + lon);
            Geocoder geocoder = new Geocoder(getApplicationContext(), new Locale("en"));
            try {
                List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                if (addresses != null && addresses.size() != 0) {
                    StringBuilder builder = new StringBuilder();
                    Address returnAddress = addresses.get(0);
                    for (int i = 0; i < returnAddress.getMaxAddressLineIndex(); i++) {
                        builder.append(returnAddress.getAddressLine(i));
                        builder.append(" ");
                    }
                    address.setText(builder);
                    address.setVisibility(View.VISIBLE);
                } else {
                    Log.e(TAG, "Addresses null");
                }
            } catch (IOException e) {
                Log.e(TAG, "Geocoder exception " + e);
            }
        } else {
            coordinates.setText("No location yet");
            address.setVisibility(View.INVISIBLE);
        }

        receiver = new ConnectionReceiver();
        intentFilter = new IntentFilter("com.journaldev.broadcastreceiver.SOME_ACTION");

    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastTime) > 100) {
                long diffTime = (currentTime - lastTime);
                lastTime = currentTime;
                float speed = Math.abs(x + y + z - lastX - lastY - lastZ)/ diffTime * 10000;
                if (speed > THRESHOLD) {
                    getRandomNumber();
                }
                lastX = x;
                lastY = y;
                lastZ = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    private void getRandomNumber() {
        Random randNumber;
        randNumber = new Random();
        int iNumber = randNumber.nextInt(100);
        TextView text = (TextView)findViewById(R.id.number);
        text.setText("" + iNumber);
        RelativeLayout ball = (RelativeLayout) findViewById(R.id.ball);
        Animation a = AnimationUtils.loadAnimation(this, R.anim.move_down_ball_first);
        ball.setVisibility(View.INVISIBLE);
        ball.setVisibility(View.VISIBLE);
        ball.clearAnimation();
        ball.startAnimation(a);
    }




    public class UpdateLocationClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (SApplication.LOCATION != null) {
                double lat = SApplication.LOCATION.getLatitude();
                double lon = SApplication.LOCATION.getLongitude();
                coordinates.setText(lat + " " + lon);
                Geocoder geocoder = new Geocoder(getApplicationContext(), new Locale("en"));
                try {
                    // get address from location
                    List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                    if (addresses != null && addresses.size() != 0) {
                        StringBuilder builder = new StringBuilder();
                        Address returnAddress = addresses.get(0);
                        for (int i = 0; i < returnAddress.getMaxAddressLineIndex(); i++) {
                            builder.append(returnAddress.getAddressLine(i));
                            builder.append(" ");
                        }
                        address.setText(builder);
                        address.setVisibility(View.VISIBLE);
                    } else {
                        Log.e(TAG, "Addresses null");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Geocoder exception " + e);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Check GPS status and internet connection", Toast.LENGTH_LONG).show();
                coordinates.setText("No location yet");
                address.setVisibility(View.INVISIBLE);
            }
        }

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
    }
    void someMethod() {

        Intent intent = new Intent("com.com.alpho.hospitalizedpatientsfollowup.broadcastreceiver.SOME_ACTION");
        sendBroadcast(intent);
    }
}
