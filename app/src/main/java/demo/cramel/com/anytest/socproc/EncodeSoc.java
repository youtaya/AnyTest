package demo.cramel.com.anytest.socproc;

/**
 * Created by jinxp on 8/30/16.
 */
public class EncodeSoc {
    private static byte START1 = 0x6A;
    private static byte START2 = (byte)0xAC;
    private static byte END1 = (byte)0xCA;
    private static byte END2 = (byte)0xA6;

    public static byte[] encode(float x, float y, float z, float rx, float ry, float rz, float rw) {
        int trackPos = 0;
        byte[] packData = new byte[7*4+4];
        packData[trackPos++] = START1;
        packData[trackPos++] = START2;
        byte[] px = floatToByte(x);
        System.arraycopy(px,0,packData,trackPos,px.length);
        trackPos += px.length;


        byte[] py = floatToByte(y);
        System.arraycopy(py,0,packData,trackPos,py.length);
        trackPos += py.length;


        byte[] pz = floatToByte(z);
        System.arraycopy(pz,0,packData,trackPos,pz.length);
        trackPos += pz.length;



        byte[] rvx = floatToByte(rx);
        System.arraycopy(rvx,0,packData,trackPos,rvx.length);
        trackPos += rvx.length;

        byte[] rvy = floatToByte(ry);
        System.arraycopy(rvy,0,packData,trackPos,rvy.length);
        trackPos += rvy.length;

        byte[] rvz = floatToByte(rz);
        System.arraycopy(rvz,0,packData,trackPos,rvz.length);
        trackPos += rvz.length;

        byte[] rvw = floatToByte(rw);
        System.arraycopy(rvw,0,packData,trackPos,rvw.length);
        trackPos += rvw.length;

        packData[trackPos++] = END1;
        packData[trackPos++] = END2;

        return packData;
    }

    public static byte[] floatToByte(float v) {
        // 把float转换为byte[]
        int fbit = Float.floatToIntBits(v);

        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (fbit >> (24 - i * 8));
        }

        // 翻转数组
        int len = b.length;
        // 建立一个与源数组元素类型相同的数组
        byte[] dest = new byte[len];
        // 为了防止修改源数组，将源数组拷贝一份副本
        System.arraycopy(b, 0, dest, 0, len);
        byte temp;
        // 将顺位第i个与倒数第i个交换
        for (int i = 0; i < len / 2; ++i) {
            temp = dest[i];
            dest[i] = dest[len - i - 1];
            dest[len - i - 1] = temp;
        }

        return dest;
    }

    /**
     * 字节转换为浮点
     *
     * @param b 字节（至少4个字节）
     * @param index 开始位置
     * @return
     */
    public static float byte2float(byte[] b, int index) {
        int l;
        l = b[index + 0];
        l &= 0xff;
        l |= ((long) b[index + 1] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 2] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 3] << 24);
        return Float.intBitsToFloat(l);
    }
}
