// ISKInterface.aidl
package demo.cramel.com.anytest.services;

// Declare any non-default types here with import statements

interface ISKInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void startSocket(String destIP, int port);
    boolean isConnnect();
    void sendData(in byte[] data);
    void closeSocket();
}
