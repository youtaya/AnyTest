package demo.cramel.com.anytest.services;

import android.app.Service;
import android.content.Intent;
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
    private static final String TAG = "test";
    Socket socket = null;
    DataOutputStream out = null;

    private ISKInterface.Stub serviceBinder = new ISKInterface.Stub() {

        @Override
        public void startSocket(String destIP, int port) throws RemoteException {
            final String myIP = destIP;
            final int myPort = port;
            new Thread(new Runnable() {

                @Override
                public void run() {
                    InetAddress serverAddr = null;
                    SocketAddress sc_add = null;
                    Log.d(TAG, "SkService start socket");
                    try {
                        serverAddr = InetAddress.getByName(myIP);
                        sc_add= new InetSocketAddress(serverAddr,myPort);

                        socket = new Socket();
                        //timeout: 2000
                        socket.connect(sc_add/*,2000*/);
                        Log.d(TAG, "socket connect!!");

                    } catch (UnknownHostException e) {
                        Log.d(TAG, "host error : "+e);
                    } catch (SocketException e) {
                        Log.d(TAG,"socket error : "+e);
                    }catch(IOException e) {
                        Log.d(TAG,"io error : "+e);
                    }
                }
            }).start();

        }
        @Override
        public boolean isConnnect() throws RemoteException {
            boolean result = false;
            result = socket.isConnected();
            return result;
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
