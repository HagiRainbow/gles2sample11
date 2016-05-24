package com.example.control.gles2sample11;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by tommy on 2015/06/19.
 */
public class BufferUtil {
    //convert float array to FloatBuffer
    public static  FloatBuffer makeFloatBuffer(float[] array) {
        FloatBuffer fb= ByteBuffer.allocateDirect(array.length * 4).order(
                ByteOrder.nativeOrder()).asFloatBuffer();
        fb.put(array).position(0);
        return fb;
    }

    public static  void setFloatBuffer(float[] array,FloatBuffer fb) {
        fb.put(array).position(0);
    }

    //convert byte array to ByteBuffer
    public static ByteBuffer makeByteBuffer(byte[] array) {
        ByteBuffer bb= ByteBuffer.allocateDirect(array.length).order(
                ByteOrder.nativeOrder());
        bb.put(array).position(0);
        return bb;
    }

    //convert float array to FloatBuffer
    public static ShortBuffer makeShortBuffer(short[] array) {
        ShortBuffer sb= ByteBuffer.allocateDirect(array.length * 2).order(
                ByteOrder.nativeOrder()).asShortBuffer();
        sb.put(array).position(0);
        return sb;
    }

}
