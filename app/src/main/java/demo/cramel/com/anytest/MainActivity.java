package demo.cramel.com.anytest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import demo.cramel.com.anytest.services.ISKInterface;
import demo.cramel.com.anytest.services.SKservices;
import demo.cramel.com.anytest.socproc.EncodeSoc;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "testMYU";

    Leslam lslam;
    //String dstAddress = "192.168.31.106";
    String dstAddress = "192.168.31.145";
    //String dstAddress = "192.168.31.203";
    int dstPort = 51116;


    private TextView typeTV;
    private TextView dataTV;


    private ISKInterface mISKInterface;

    private boolean mIsBound = false;


    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mISKInterface = ISKInterface.Stub.asInterface(service);


            try {
                Log.d(TAG, "start socket");
                mISKInterface.startSocket(dstAddress, dstPort);
                mIsBound = true;
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


    }

    public void getSlamData() {


        lslam.GetSlamData(lslam.dX, lslam.dQuaternion);
        //Log.d(TAG, "position = " + lslam.dX[0] + " " + lslam.dX[1] + " " + lslam.dX[2]);

        byte[] testData = EncodeSoc.encode((float)lslam.dX[0],
                (float)lslam.dX[1],(float)lslam.dX[2],(float)lslam.dQuaternion[0],
                (float)lslam.dQuaternion[1],(float)lslam.dQuaternion[2],(float)lslam.dQuaternion[3]);

        if (mIsBound) {
            try {
                if(mISKInterface.isConnnect()) {
                    final byte[] rawData = testData;
                    /*
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                mISKInterface.sendData(rawData);
                            }catch (RemoteException e) {
                                Log.d(TAG, "error :"+e.getMessage());
                            }
                        }
                    }).start();
                    */
                    mISKInterface.sendData(rawData);
                    //new NetworkSockTask().execute(rawData);

                }
            } catch (RemoteException e) {
                Log.d(TAG, "error :"+e.getMessage());
            }
        }
    }



    @Override
    protected void onResume() {
        super.onResume();

        lslam = new Leslam();

        lslam.ModeSwitch(true);
        lslam.Start();

        new Thread(new Runnable() {
            public void run() {
                while (true)
                {
                    getSlamData();
                    try {
                        sleep(200);
                    } catch (InterruptedException e) {
                        Log.d(TAG, "interrupt exception : " + e.getMessage());
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*
        if (gyroscope != null)
            mSensorManager.unregisterListener(this);
        */
    }

}
