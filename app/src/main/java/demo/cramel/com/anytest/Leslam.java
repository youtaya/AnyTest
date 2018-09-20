package demo.cramel.com.anytest;

public class Leslam
{
    private static String TAG = Leslam.class.getSimpleName();

    static {
        System.loadLibrary("native-lib");
    }

    public double dTime;
    public double[] dX;
    public double[] dQuaternion;

    public Leslam() {
        dTime = 0;
        dX = new double[3];
        dQuaternion = new double[4];
    }

    public native int Init();
    public native int Start();
    public native int ModeSwitch(boolean mode);
    public native int GetSlamData(double[] pos, double[] rot );

}