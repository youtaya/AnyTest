package demo.cramel.com.anytest.socproc;

/**
 * Created by jinxp on 8/30/16.
 */
public class EncodeSoc {
    private static byte START1 = 0x6A;
    private static byte START2 = (byte)0xAC;
    private static byte END1 = (byte)0xCA;
    private static byte END2 = (byte)0xA6;
    private static byte SEPERATOR = (byte)0xAA;

    public static byte[] encode(float timestamp, float x, float y, float z, float w) {
        int trackPos = 0;
        byte[] packData = new byte[5*4+4];
        packData[trackPos++] = START1;
        packData[trackPos++] = START2;
        byte[] bTime = floatToByte(timestamp);
        System.arraycopy(bTime,0,packData,trackPos,bTime.length);
        trackPos += bTime.length;

        //packData[trackPos++] = SEPERATOR;

        byte[] bX = floatToByte(x);
        System.arraycopy(bX,0,packData,trackPos,bX.length);
        trackPos += bX.length;

        //packData[trackPos++] = SEPERATOR;

        byte[] bY = floatToByte(y);
        System.arraycopy(bY,0,packData,trackPos,bY.length);
        trackPos += bY.length;

        //packData[trackPos++] = SEPERATOR;

        byte[] bZ = floatToByte(z);
        System.arraycopy(bZ,0,packData,trackPos,bZ.length);
        trackPos += bZ.length;

        byte[] bW = floatToByte(w);
        System.arraycopy(bW,0,packData,trackPos,bW.length);
        trackPos += bW.length;

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
