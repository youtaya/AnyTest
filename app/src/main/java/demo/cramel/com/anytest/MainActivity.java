package demo.cramel.com.anytest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import demo.cramel.com.anytest.services.ISKInterface;
import demo.cramel.com.anytest.services.SKservices;
import demo.cramel.com.anytest.socproc.EncodeSoc;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private final static String TAG = "test";

    private SensorManager mSensorManager;
    private Sensor gyroscope;

    String dstAddress = "10.106.11.16";
    int dstPort = 5111;

    // Create a constant to convert nanoseconds to seconds.
    private static final float NS2S = 1.0f / 1000000000.0f;
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;
    private static final float EPSILON = 10000.0f;

    private TextView typeTV;
    private TextView dataTV;


    private ISKInterface mISKInterface;

    private boolean mIsBound = false;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mISKInterface = ISKInterface.Stub.asInterface(service);
            mIsBound = true;
            try {
                mISKInterface.startSocket(dstAddress, dstPort);
            } catch (RemoteException e) {
                Log.d(TAG, "error :"+e.getMessage());
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            mISKInterface = null;
            mIsBound = false;
        }
    };
    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, SKservices.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);


    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        typeTV = (TextView) findViewById(R.id.sensortype);
        dataTV = (TextView) findViewById(R.id.sensordata);


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null){
            gyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            typeTV.setText("sensor type : gyroscope");
        } else {
            Log.d(TAG, "there is no gyroscope sensor. ");
            typeTV.setText("sensor type : no sensor found");
        }


    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // This timestep's delta rotation to be multiplied by the current rotation
        // after computing it from the gyro sample data.
        if (timestamp != 0) {
            final float dT = (event.timestamp - timestamp) * NS2S;
            // Axis of the rotation sample, not normalized yet.
            float axisX = event.values[0];
            float axisY = event.values[1];
            float axisZ = event.values[2];

            // Calculate the angular speed of the sample
            float omegaMagnitude = (float)Math.sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);

            // Normalize the rotation vector if it's big enough to get the axis
            // (that is, EPSILON should represent your maximum allowable margin of error)
            if (omegaMagnitude > EPSILON) {
                axisX /= omegaMagnitude;
                axisY /= omegaMagnitude;
                axisZ /= omegaMagnitude;
            }

            // Integrate around this axis with the angular speed by the timestep
            // in order to get a delta rotation from this sample over the timestep
            // We will convert this axis-angle representation of the delta rotation
            // into a quaternion before turning it into the rotation matrix.
            float thetaOverTwo = omegaMagnitude * dT / 2.0f;
            float sinThetaOverTwo = (float)Math.sin(thetaOverTwo);
            float cosThetaOverTwo = (float)Math.cos(thetaOverTwo);
            deltaRotationVector[0] = sinThetaOverTwo * axisX;
            deltaRotationVector[1] = sinThetaOverTwo * axisY;
            deltaRotationVector[2] = sinThetaOverTwo * axisZ;
            deltaRotationVector[3] = cosThetaOverTwo;
        }
        timestamp = event.timestamp;
        float[] deltaRotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
        // User code should concatenate the delta rotation we computed with the current rotation
        // in order to get the updated rotation.
        // rotationCurrent = rotationCurrent * deltaRotationMatrix;

        //Log.d(TAG, "x :"+deltaRotationVector[0]+" "+"y :"+deltaRotationVector[1]+" "+"z :"+deltaRotationVector[2]);
        dataTV.setText("sensor data: "+ "timestamp :"+timestamp+
                "x :"+deltaRotationVector[0]+" "+"y :"+deltaRotationVector[1]+" "+"z :"+deltaRotationVector[2]);

        byte[] testData = EncodeSoc.encode(timestamp,deltaRotationVector[0],deltaRotationVector[1],deltaRotationVector[2]);

        if (mIsBound) {
            try {
                if(mISKInterface.isConnnect()) {
                    mISKInterface.sendData(testData);
                }
            } catch (RemoteException e) {
                Log.d(TAG, "error :"+e.getMessage());
            }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gyroscope != null)
            mSensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gyroscope != null)
            mSensorManager.unregisterListener(this);
    }

}
