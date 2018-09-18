package demo.cramel.com.anytest.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class SKservices extends Service {
    private static final String TAG = "testMYU";
    Socket socket = null;
    DataOutputStream out = null;

    String myIP;
    int myPort;

    class MySockTask extends AsyncTask<Void , Void, Void> {

        private Exception exception;

        protected Void doInBackground(Void... data) {
            try {
                InetAddress serverAddr = null;
                SocketAddress sc_add = null;
                Log.d(TAG, "SkService start socket");
                try {
                    Log.d(TAG, "my ip : "+myIP+" my port : "+myPort);
                    serverAddr = InetAddress.getByName(myIP);
                    sc_add= new InetSocketAddress(serverAddr,myPort);

                    socket = new Socket();
                    //timeout: 2000
                    socket.connect(sc_add,10000);
                    Log.d(TAG, "socket connect!!");

                } catch (UnknownHostException e) {
                    Log.d(TAG, "host error : "+e);
                } catch (SocketException e) {
                    Log.d(TAG,"socket error : "+e);
                } catch(IOException e) {
                    Log.d(TAG,"io error : "+e);
                }
            } catch (Exception e) {
                this.exception = e;

            } finally {
            }

            return null;
        }
    }

    private ISKInterface.Stub serviceBinder = new ISKInterface.Stub() {

        @Override
        public void startSocket(String destIP, int port) throws RemoteException {
            myIP = destIP;
            myPort = port;

            new MySockTask().execute();

        }

        @Override
        public boolean isConnnect() throws RemoteException {
            boolean result = false;
            if(socket != null) {
                result = socket.isConnected();
                return result;
            } else {
                return false;
            }

        }
        @Override
        public void sendData(byte[] data) throws RemoteException {
            try {
                //Log.d(TAG, "data: "+data.toString());
                out = new DataOutputStream(socket.getOutputStream());
                out.write(data);
                out.flush();

            } catch (IOException e) {
                Log.d(TAG,"io error : "+e);
            }
        }
        @Override
        public void closeSocket() throws RemoteException {
            try {
                socket.close();
            } catch(IOException e) {
                Log.d(TAG,"io error : "+e);
            }
        }
    };

    public SKservices() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "SkService onBind");
        return serviceBinder;
    }


    @Override
    public void onCreate() {
        Log.d(TAG, "SkService onCreate");
    }

}
