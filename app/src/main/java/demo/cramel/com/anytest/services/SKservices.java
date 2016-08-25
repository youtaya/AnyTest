package demo.cramel.com.anytest.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
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
    String dstAddress = "10.106.11.16";
    int dstPort = 5111;


    public SKservices() {
    }

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        public SKservices getService() {
            return SKservices.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    public void connServer() {
        InetAddress serverAddr = null;
        SocketAddress sc_add = null;
        Socket socket = null;
        //test string for sent
        String message = "Hello Socket";

        try {
            serverAddr = InetAddress.getByName(dstAddress);
            sc_add= new InetSocketAddress(serverAddr,dstPort);

            socket = new Socket();
            //timeout: 2000
            socket.connect(sc_add,20000);

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(message);
            Log.d(TAG, "send message : "+message);
            out.flush();
            socket.close();

        } catch (UnknownHostException e) {
            Log.d(TAG, "host error : "+e);
        } catch (SocketException e) {
            Log.d(TAG,"socket error : "+e);
        } catch(IOException e) {
            Log.d(TAG,"io error : "+e);
        }
    }


}
